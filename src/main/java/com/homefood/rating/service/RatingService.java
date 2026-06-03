package com.homefood.rating.service;

import com.homefood.rating.entity.Rating;
import com.homefood.rating.repository.RatingRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.OptionalDouble;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class RatingService {

    private final RatingRepository ratingRepository;
    private final KafkaTemplate<String, String> kafkaTemplate;

    public Rating submitRating(String foodId, UUID buyerId, UUID sellerId, UUID orderId,
                                int foodRating, int deliveryRating, int packagingRating, String review) {
        if (ratingRepository.existsByFoodIdAndBuyerId(foodId, buyerId)) {
            throw new IllegalArgumentException("You have already rated this food item");
        }

        double overall = (foodRating + deliveryRating + packagingRating) / 3.0;

        Rating rating = Rating.builder()
                .foodId(foodId)
                .buyerId(buyerId)
                .sellerId(sellerId)
                .orderId(orderId)
                .foodRating(foodRating)
                .deliveryRating(deliveryRating)
                .packagingRating(packagingRating)
                .review(review)
                .overallRating(Math.round(overall * 10.0) / 10.0)
                .isVerifiedPurchase(true)
                .build();

        rating = ratingRepository.save(rating);

        // Recalculate and publish new average
        updateFoodAverageRating(foodId);

        kafkaTemplate.send("report.event", foodId,
                String.format("{\"type\":\"RATING_SUBMITTED\",\"foodId\":\"%s\",\"rating\":%.1f}", foodId, overall));

        log.info("Rating submitted for food: {} by buyer: {}", foodId, buyerId);
        return rating;
    }

    public Page<Rating> getFoodRatings(String foodId, int page, int size) {
        return ratingRepository.findByFoodIdOrderByCreatedAtDesc(foodId,
                PageRequest.of(page, size, Sort.by("createdAt").descending()));
    }

    public Map<String, Object> getFoodRatingSummary(String foodId) {
        List<Rating> ratings = ratingRepository.findOverallRatingsByFoodId(foodId);
        long count = ratingRepository.countByFoodId(foodId);
        OptionalDouble avg = ratings.stream().mapToDouble(Rating::getOverallRating).average();
        return Map.of(
                "foodId", foodId,
                "totalRatings", count,
                "averageRating", avg.orElse(0.0)
        );
    }

    private void updateFoodAverageRating(String foodId) {
        List<Rating> allRatings = ratingRepository.findOverallRatingsByFoodId(foodId);
        if (!allRatings.isEmpty()) {
            double avg = allRatings.stream().mapToDouble(Rating::getOverallRating).average().orElse(0.0);
            int count = allRatings.size();
            // Notify food service to update
            kafkaTemplate.send("rating.submitted", foodId,
                    String.format("{\"foodId\":\"%s\",\"averageRating\":%.1f,\"totalRatings\":%d}", foodId, avg, count));
        }
    }
}
