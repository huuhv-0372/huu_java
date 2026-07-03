package com.huuhv.foodsndrinks.security;

import com.huuhv.foodsndrinks.entity.User;
import com.huuhv.foodsndrinks.enums.AuthProvider;
import com.huuhv.foodsndrinks.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Facebook and Twitter sign-in (plain OAuth2, not OIDC). Neither provider's user-info response
 * can be trusted to expose a real name attribute usable as {@code Authentication#getName()}
 * (Twitter nests everything under "data" and never exposes an email at all), so this always
 * injects a synthetic "local_username" attribute pointing at the resolved local account instead —
 * that's what {@code UserService.getCurrentUser(principal.getName())} ends up looking up.
 */
@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private static final String NAME_ATTRIBUTE_KEY = "local_username";

    private final UserService userService;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oauth2User = super.loadUser(userRequest);
        String registrationId = userRequest.getClientRegistration().getRegistrationId();

        ProviderProfile profile = switch (registrationId) {
            case "facebook" -> readFacebookProfile(oauth2User.getAttributes());
            case "twitter" -> readTwitterProfile(oauth2User.getAttributes());
            default -> throw new OAuth2AuthenticationException("Nhà cung cấp đăng nhập không được hỗ trợ: " + registrationId);
        };

        User localUser = userService.provisionOAuthUser(
                profile.provider(), profile.providerId(), profile.email(), profile.name(), profile.avatarUrl());

        Map<String, Object> attributes = new HashMap<>(profile.attributes());
        attributes.put(NAME_ATTRIBUTE_KEY, localUser.getUsername());

        List<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority(localUser.getRole().name()));
        return new DefaultOAuth2User(authorities, attributes, NAME_ATTRIBUTE_KEY);
    }

    private ProviderProfile readFacebookProfile(Map<String, Object> attributes) {
        String pictureUrl = null;
        if (attributes.get("picture") instanceof Map<?, ?> pictureWrapper
                && pictureWrapper.get("data") instanceof Map<?, ?> pictureData) {
            pictureUrl = (String) pictureData.get("url");
        }
        return new ProviderProfile(
                AuthProvider.FACEBOOK,
                String.valueOf(attributes.get("id")),
                (String) attributes.get("email"),
                (String) attributes.getOrDefault("name", "Facebook User"),
                pictureUrl,
                attributes
        );
    }

    @SuppressWarnings("unchecked")
    private ProviderProfile readTwitterProfile(Map<String, Object> attributes) {
        // Twitter's /2/users/me nests every field under "data" instead of returning it flat.
        Map<String, Object> data = attributes.get("data") instanceof Map<?, ?> nested
                ? (Map<String, Object>) nested
                : Map.of();
        return new ProviderProfile(
                AuthProvider.TWITTER,
                String.valueOf(data.get("id")),
                null, // Twitter's API does not expose an email address via OAuth2
                (String) data.getOrDefault("name", "Twitter User"),
                (String) data.get("profile_image_url"),
                data
        );
    }

    private record ProviderProfile(AuthProvider provider, String providerId, String email, String name,
                                   String avatarUrl, Map<String, Object> attributes) {
    }
}
