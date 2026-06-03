package com.homefood.rating;

import com.homefood.rating.service.RatingService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class RatingServiceApplicationTest {

    @Mock
    RatingService ratingService;

    @Test
    void contextLoads() {
        assertThat(ratingService).isNotNull();
    }
}
