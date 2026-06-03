package com.homefood.rating;

import com.homefood.rating.controller.RatingController;
import com.homefood.rating.entity.Rating;
import com.homefood.rating.service.RatingService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;
import org.springframework.http.ResponseEntity;

import java.util.*;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RatingControllerUnitTest {

    @Mock RatingService ratingService;
    @InjectMocks RatingController ratingController;

    @Test
    void getFoodRatings_returns200() {
        Rating r = Rating.builder().foodId("food_001").overallRating(4.5).build();
        Page<Rating> page = new PageImpl<>(List.of(r));
        when(ratingService.getFoodRatings("food_001", 0, 10)).thenReturn(page);

        ResponseEntity<Page<Rating>> result = ratingController.getFoodRatings("food_001", 0, 10);
        assertThat(result.getStatusCode().value()).isEqualTo(200);
        assertThat(result.getBody()).isNotNull();
    }

    @Test
    void getRatingSummary_returns200() {
        Map<String, Object> summary = Map.of("foodId", "food_001", "averageRating", 4.5, "totalRatings", 10L);
        when(ratingService.getFoodRatingSummary("food_001")).thenReturn(summary);

        ResponseEntity<Map<String, Object>> result = ratingController.getRatingSummary("food_001");
        assertThat(result.getStatusCode().value()).isEqualTo(200);
    }
}
