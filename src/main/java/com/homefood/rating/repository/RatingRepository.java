package com.homefood.rating.repository;

import com.homefood.rating.entity.Rating;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface RatingRepository extends MongoRepository<Rating, String> {

    Page<Rating> findByFoodIdOrderByCreatedAtDesc(String foodId, Pageable pageable);

    List<Rating> findBySellerIdOrderByCreatedAtDesc(UUID sellerId);

    boolean existsByFoodIdAndBuyerId(String foodId, UUID buyerId);

    @Query(value = "{'foodId': ?0}", fields = "{'overallRating': 1}")
    List<Rating> findRatingsByFoodId(String foodId);

    long countByFoodId(String foodId);

    @Query(value = "{'foodId': ?0}", fields = "{'overallRating': 1, '_id': 0}")
    List<Rating> findOverallRatingsByFoodId(String foodId);
}
