package com.homefood.rating.controller;

import com.homefood.rating.entity.Rating;
import com.homefood.rating.service.RatingService;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/ratings")
@RequiredArgsConstructor
public class RatingController {

    private final RatingService ratingService;

    @PostMapping
    public ResponseEntity<Rating> submitRating(
            @RequestHeader("X-User-Id") UUID buyerId,
            @RequestBody Map<String, Object> body) {
        Rating rating = ratingService.submitRating(
                (String) body.get("foodId"),
                buyerId,
                UUID.fromString((String) body.get("sellerId")),
                UUID.fromString((String) body.get("orderId")),
                (Integer) body.get("foodRating"),
                (Integer) body.get("deliveryRating"),
                (Integer) body.get("packagingRating"),
                (String) body.get("review")
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(rating);
    }

    @GetMapping("/food/{foodId}")
    public ResponseEntity<Page<Rating>> getFoodRatings(
            @PathVariable String foodId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(ratingService.getFoodRatings(foodId, page, size));
    }

    @GetMapping("/food/{foodId}/summary")
    public ResponseEntity<Map<String, Object>> getRatingSummary(@PathVariable String foodId) {
        return ResponseEntity.ok(ratingService.getFoodRatingSummary(foodId));
    }
}
