/**
 *
 * @author 김성준
 * 2025.11.04 작성
 */
package chatting.dto;

import chatting.domain.User;
import lombok.Builder;
import lombok.Getter;

import java.util.Map;



@Getter
//google JSON을 OAuthAttributes 객체에 매핑, toEntity() 에서 ROLE_GUEST를 부여함
public class OAuthAttributes {
    private Map<String , Object> attributes;
    private String nameAttributeKey;
    private String username; //(Google의 name)
    private String email;
    private String picture;
    private String provider;

    @Builder
    public OAuthAttributes(Map<String, Object> attributes, String nameAttributeKey, String username, String email, String picture, String provider) {
        this.attributes = attributes;
        this.nameAttributeKey = nameAttributeKey;
        this.username = username;
        this.email = email;
        this.picture = picture;
        this.provider = provider;
    }

    //customOAuth2UserDetails 클래스에서 registrationId(ex: "google", "naver") 정보를 보내서 분기처리하게 만듦
    public static OAuthAttributes of(String registrationId, String userNameAttributeName, Map<String, Object> attributes) {


        if ("naver".equals(registrationId)) {
            return ofNaver(userNameAttributeName, attributes);
        }



        if ("kakao".equals(registrationId)) {
            return ofKakao(userNameAttributeName, attributes);
        }

        return ofGoogle(userNameAttributeName, attributes);
    }

    private static OAuthAttributes ofGoogle(String userNameAttributeName, Map<String, Object> attributes){
            return OAuthAttributes.builder()
                    .username((String) attributes.get("name")) // 'name'을 username 필드에 매핑
                    .email((String) attributes.get("email"))
                    .picture((String) attributes.get("picture"))
                    .provider("google")
                    .attributes(attributes)
                    .nameAttributeKey(userNameAttributeName)
                    .build();

    }


    private static OAuthAttributes ofNaver(String userNameAttributeName, Map<String, Object> attributes) {
        Map<String, Object> response = (Map<String, Object>) attributes.get("response");

        return OAuthAttributes.builder()
                .username((String) response.get("name"))
                .email((String) response.get("email"))
                .picture((String) response.get("profile_image"))
                .provider("naver")
                .attributes(response)
                .nameAttributeKey(userNameAttributeName)
                .build();
    }



    private static OAuthAttributes ofKakao(String userNameAttributeName, Map<String, Object> attributes) {
        Map<String, Object> kakaoAccount = (Map<String, Object>) attributes.get("kakao_account");
        Map<String, Object> kakaoProfile = (Map<String, Object>) kakaoAccount.get("profile");

        return OAuthAttributes.builder()
                .username((String) kakaoProfile.get("nickname"))
                .email((String) kakaoAccount.get("email"))
                .picture((String) kakaoProfile.get("profile_image_url"))
                .provider("kakao")
                .attributes(attributes)
                .nameAttributeKey(userNameAttributeName)
                .build();
    }



    //  신규 사용자일 경우, DTO -> Entity로 변환
    public User toEntity() {
        return User.builder()
                .username(username) // (Google 'name')
                .email(email)
                .picture(picture)
                .provider(provider)
                .role("ROLE_GUEST") //  신규 가입 시 임시 권한 부여
                // password, nickname, region은 null 상태
                .build();
    }




}
