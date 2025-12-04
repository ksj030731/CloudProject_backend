package chatting.dto;

import chatting.domain.User;
import lombok.Builder;
import lombok.Getter;

import java.util.Map;

@Getter
public class OAuthAttributes {
    private Map<String, Object> attributes;
    private String nameAttributeKey;
    private String username; // 이제 여기에 "google_12345" 같은 고유 ID가 들어갑니다.
    private String email;
    private String picture;
    private String provider;
    private String realName; // (선택) 실제 이름을 따로 저장하고 싶다면 필드 추가

    @Builder
    public OAuthAttributes(Map<String, Object> attributes, String nameAttributeKey, String username, String email, String picture, String provider) {
        this.attributes = attributes;
        this.nameAttributeKey = nameAttributeKey;
        this.username = username;
        this.email = email;
        this.picture = picture;
        this.provider = provider;
    }

    public static OAuthAttributes of(String registrationId, String userNameAttributeName, Map<String, Object> attributes) {
        if ("naver".equals(registrationId)) {
            return ofNaver(userNameAttributeName, attributes);
        }
        if ("kakao".equals(registrationId)) {
            return ofKakao(userNameAttributeName, attributes);
        }
        return ofGoogle(userNameAttributeName, attributes);
    }

    //
    private static OAuthAttributes ofGoogle(String userNameAttributeName, Map<String, Object> attributes) {
        // 구글의 고유 ID는 "sub"에 있습니다.
        String providerId = (String) attributes.get("sub");

        return OAuthAttributes.builder()
                .username("google_" + providerId) // 예: google_123456789 (중복 안 됨!)
                .email((String) attributes.get("email"))
                .picture((String) attributes.get("picture"))
                .provider("google")
                .attributes(attributes)
                .nameAttributeKey(userNameAttributeName)
                .build();
    }

    // 네이버
    private static OAuthAttributes ofNaver(String userNameAttributeName, Map<String, Object> attributes) {
        Map<String, Object> response = (Map<String, Object>) attributes.get("response");

        // 네이버의 고유 ID는 response 안의 "id"에 있습니다.
        String providerId = (String) response.get("id");

        return OAuthAttributes.builder()
                .username("naver_" + providerId) // 예: naver_AbCdEf...
                .email((String) response.get("email"))
                .picture((String) response.get("profile_image"))
                .provider("naver")
                .attributes(response)
                .nameAttributeKey(userNameAttributeName)
                .build();
    }

    // 카카오
    private static OAuthAttributes ofKakao(String userNameAttributeName, Map<String, Object> attributes) {
        Map<String, Object> kakaoAccount = (Map<String, Object>) attributes.get("kakao_account");
        Map<String, Object> kakaoProfile = (Map<String, Object>) kakaoAccount.get("profile");

        // 카카오는 root attributes에 "id"가 있습니다. (보통 Long 타입이라 String 변환 필요)
        String providerId = String.valueOf(attributes.get("id"));

        return OAuthAttributes.builder()
                .username("kakao_" + providerId) // 예: kakao_12345
                .email((String) kakaoAccount.get("email"))
                .picture((String) kakaoProfile.get("profile_image_url"))
                .provider("kakao")
                .attributes(attributes)
                .nameAttributeKey(userNameAttributeName)
                .build();
    }

    public User toEntity() {
        return User.builder()
                .username(username) // 이제 고유한 ID가 들어갑니다.
                .email(email)
                .picture(picture)
                .provider(provider)
                .role("ROLE_GUEST")
                // nickname은 초기엔 null로 두거나, 필요하면 "게스트" 등으로 설정
                .build();
    }
}