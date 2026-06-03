package com.homefood.rating.entity;

import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.UUID;

@Document(collection = "ratings")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@CompoundIndexes({
        @CompoundIndex(name = "idx_food_buyer", def = "{'foodId': 1, 'buyerId': 1}", unique = true),
        @CompoundIndex(name = "idx_order", def = "{'orderId': 1}")
})
public class Rating {

    @Id
    private String id;

    private String foodId;
    private UUID buyerId;
    private UUID sellerId;
    private UUID orderId;

    private int foodRating;       // 1-5
    private int deliveryRating;   // 1-5
    private int packagingRating;  // 1-5

    private String review;
    private boolean isVerifiedPurchase;
    private String imageUrl;

    private double overallRating; // computed average

    @CreatedDate
    private LocalDateTime createdAt;
}
