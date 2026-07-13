package com.huuhv.foodsndrinks.dto.response;

import com.huuhv.foodsndrinks.entity.Suggestion;
import com.huuhv.foodsndrinks.entity.User;
import com.huuhv.foodsndrinks.enums.SuggestionStatus;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class SuggestionResDto {

    private final Long             id;
    private final Long             userId;
    private final String           username;
    private final String           userFullName;
    private final String           content;
    private final SuggestionStatus status;
    private final String           adminNote;
    private final LocalDateTime    createdAt;
    private final LocalDateTime    updatedAt;

    private SuggestionResDto(Suggestion s) {
        User u            = s.getUser();
        this.id           = s.getId();
        this.userId       = u != null ? u.getId()       : null;
        this.username     = u != null ? u.getUsername() : "—";
        this.userFullName = u != null ? u.getFullName() : "—";
        this.content      = s.getContent();
        this.status       = s.getStatus();
        this.adminNote    = s.getAdminNote();
        this.createdAt    = s.getCreatedAt();
        this.updatedAt    = s.getUpdatedAt();
    }

    public static SuggestionResDto from(Suggestion s) {
        return new SuggestionResDto(s);
    }
}

