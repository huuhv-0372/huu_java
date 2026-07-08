package com.huuhv.foodsndrinks.dto.response;

import com.huuhv.foodsndrinks.entity.Rating;
import com.huuhv.foodsndrinks.entity.User;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class RatingResDto {

    private final Long          id;
    private final String        userFullName;
    private final Byte          stars;
    private final String        comment;
    private final LocalDateTime createdAt;

    private RatingResDto(Rating r) {
        User u             = r.getUser();
        this.id            = r.getId();
        this.userFullName  = u != null ? u.getFullName() : "Khách hàng";
        this.stars         = r.getStars();
        this.comment       = r.getComment();
        this.createdAt     = r.getCreatedAt();
    }

    public static RatingResDto from(Rating r) {
        return new RatingResDto(r);
    }
}
