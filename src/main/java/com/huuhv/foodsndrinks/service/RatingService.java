package com.huuhv.foodsndrinks.service;

import com.huuhv.foodsndrinks.dto.response.RatingResDto;
import com.huuhv.foodsndrinks.entity.Product;
import com.huuhv.foodsndrinks.entity.Rating;
import com.huuhv.foodsndrinks.entity.User;
import com.huuhv.foodsndrinks.repository.ProductRepository;
import com.huuhv.foodsndrinks.repository.RatingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RatingService {

    private final RatingRepository  ratingRepository;
    private final ProductRepository productRepository;

    @Transactional(readOnly = true)
    public List<RatingResDto> getRatingsForProduct(Long productId) {
        return ratingRepository.findByProductIdOrderByCreatedAtDesc(productId)
                .stream().map(RatingResDto::from).collect(Collectors.toList());
    }

    /** The current user's own rating for this product, or null if they haven't rated it yet. */
    @Transactional(readOnly = true)
    public Rating getMyRating(Long productId, Long userId) {
        return ratingRepository.findByUserIdAndProductId(userId, productId).orElse(null);
    }

    /** Creates the user's rating for this product, or overwrites their existing one (one rating per user/product). */
    @Transactional
    public void rateProduct(User user, Long productId, Byte stars, String comment) {
        if (stars == null || stars < 1 || stars > 5) {
            throw new IllegalArgumentException("Vui lòng chọn số sao từ 1 đến 5!");
        }
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Sản phẩm không tồn tại!"));

        Rating rating = ratingRepository.findByUserIdAndProductId(user.getId(), productId)
                .orElseGet(() -> Rating.builder().user(user).product(product).build());
        rating.setStars(stars);
        rating.setComment(blank(comment) ? null : comment.trim());
        ratingRepository.save(rating);

        recalculateProductRating(product);
    }

    private void recalculateProductRating(Product product) {
        Double avg = ratingRepository.avgStarsByProductId(product.getId());
        long count = ratingRepository.countByProductId(product.getId());

        product.setRatingAvg(avg == null ? BigDecimal.ZERO : BigDecimal.valueOf(avg).setScale(1, RoundingMode.HALF_UP));
        product.setRatingCount((int) count);
        productRepository.save(product);
    }

    private static boolean blank(String s) {
        return s == null || s.isBlank();
    }
}
