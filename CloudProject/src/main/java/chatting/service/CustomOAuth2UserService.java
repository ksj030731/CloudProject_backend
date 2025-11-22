package chatting.service;

import chatting.config.auth.PrincipalDetails;
import chatting.domain.User;
import chatting.dto.OAuthAttributes;
import chatting.repository.UserRepository;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
//user 엔티티에서 데이터를 꺼내서 OAuth용 생성자에 전달
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;

    public CustomOAuth2UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    @Transactional
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {

        OAuth2User oAuth2User = super.loadUser(userRequest);

        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        String userNameAttributeName = userRequest.getClientRegistration()
                .getProviderDetails().getUserInfoEndpoint().getUserNameAttributeName();

        OAuthAttributes attributes = OAuthAttributes.of(registrationId , userNameAttributeName , oAuth2User.getAttributes());

        User user = saveOrUpdate(attributes);

        return new PrincipalDetails(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getRole(),
                user.getProvider(),
                attributes.getAttributes(), // (원본 JSON은 전달)
                user.getRegion()
        );
    }

    private User saveOrUpdate(OAuthAttributes attributes) {
        User user = userRepository.findByEmailAndProvider(attributes.getEmail(),attributes.getProvider())
                .map(entity -> entity.update(attributes.getUsername(),attributes.getPicture()))
                .orElse(attributes.toEntity());

        return userRepository.save(user);
    }


}
