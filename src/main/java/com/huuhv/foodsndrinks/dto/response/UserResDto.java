package com.huuhv.foodsndrinks.dto.response;

import com.huuhv.foodsndrinks.entity.User;
import com.huuhv.foodsndrinks.enums.AuthProvider;
import com.huuhv.foodsndrinks.enums.Role;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class UserResDto {

    private final Long          id;
    private final String        username;
    private final String        fullName;
    private final String        email;
    private final String        phone;
    private final String        avatarUrl;
    private final Role          role;
    private final AuthProvider  authProvider;
    private final Boolean       isActive;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;

    private UserResDto(User u) {
        this.id           = u.getId();
        this.username     = u.getUsername();
        this.fullName     = u.getFullName();
        this.email        = u.getEmail();
        this.phone        = u.getPhone();
        this.avatarUrl    = u.getAvatarUrl();
        this.role         = u.getRole();
        this.authProvider = u.getAuthProvider();
        this.isActive     = u.getIsActive();
        this.createdAt    = u.getCreatedAt();
        this.updatedAt    = u.getUpdatedAt();
    }

    public static UserResDto from(User u) {
        return new UserResDto(u);
    }
}

