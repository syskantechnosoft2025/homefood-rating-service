package com.homefood.rating;

import com.homefood.rating.entity.Rating;
import com.homefood.rating.repository.RatingRepository;
import com.homefood.rating.service.RatingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RatingServiceUnitTest {

    @Mock
    RatingRepository ratingRepository;

    @Mock
    KafkaTemplate<String, String> kafkaTemplate;

    @InjectMocks
    RatingService ratingService;

    @BeforeEach
    void setUp() {
        when(kafkaTemplate.send(anyString(), anyString(), anyString()))
            .thenReturn(CompletableFuture.completedFuture(mock(SendResult.class)));
    }

    @Test
    void submitRating_success() {
        String foodId = "food_001";
        UUID buyerId = UUID.randomUUID();
        UUID sellerId = UUID.randomUUID();
        UUID orderId = UUID.randomUUID();

        when(ratingRepository.existsByFoodIdAndBuyerId(foodId, buyerId)).thenReturn(false);

        Rating saved = Rating.builder()
            .foodId(foodId)
            .buyerId(buyerId)
            .sellerId(sellerId)
            .orderId(orderId)
            .foodRating(5)
            .deliveryRating(4)
            .packagingRating(5)
            .overallRating(4.7)
            .isVerifiedPurchase(true)
            .build();

        when(ratingRepository.save(any())).thenReturn(saved);
        when(ratingRepository.findOverallRatingsByFoodId(foodId)).thenReturn(List.of(saved));

        Rating result = ratingService.submitRating(foodId, buyerId, sellerId, orderId, 5, 4, 5, "Great food!");
        assertThat(result.getFoodRating()).isEqualTo(5);
    }

    @Test
    void submitRating_alreadyRated_throwsException() {
        String foodId = "food_001";
        UUID buyerId = UUID.randomUUID();

        when(ratingRepository.existsByFoodIdAndBuyerId(foodId, buyerId)).thenReturn(true);

        assertThatThrownBy(() ->
            ratingService.submitRating(foodId, buyerId, UUID.randomUUID(), UUID.randomUUID(), 5, 4, 4, "test"))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("already rated");
    }

    @Test
    void getFoodRatings_returnsPage() {
        Rating r = Rating.builder().foodId("food_001").overallRating(4.5).build();
        Page<Rating> page = new PageImpl<>(List.of(r));
        when(ratingRepository.findByFoodIdOrderByCreatedAtDesc(eq("food_001"), any())).thenReturn(page);

        Page<Rating> result = ratingService.getFoodRatings("food_001", 0, 10);
        assertThat(result.getContent()).hasSize(1);
    }

    @Test
    void getFoodRatingSummary_returnsMap() {
        Rating r = Rating.builder().foodId("food_001").overallRating(4.5).build();

        when(ratingRepository.countByFoodId("food_001")).thenReturn(1L);
        when(ratingRepository.findOverallRatingsByFoodId("food_001")).thenReturn(List.of(r));

        var summary = ratingService.getFoodRatingSummary("food_001");
        assertThat(summary.get("totalRatings")).isEqualTo(1L);
        assertThat((Double) summary.get("averageRating")).isGreaterThan(0);
    }
}
