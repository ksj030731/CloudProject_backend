package chatting.service;

import chatting.config.auth.PrincipalDetails;
import chatting.domain.User;
import chatting.dto.OAuthAttributes;
import chatting.repository.UserRepository;
import lombok.extern.slf4j.Slf4j; // 💡 로그 사용을 위한 import
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j // Lombok을 사용하여 로그 기능 활성화
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

        // 1. [로그] OAuth2 로그인 시작
        log.info("--- OAuth2 로그인 요청 시작 ---");

        OAuth2User oAuth2User = super.loadUser(userRequest);

        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        String userNameAttributeName = userRequest.getClientRegistration()
                .getProviderDetails().getUserInfoEndpoint().getUserNameAttributeName();

        // 2. [로그] 현재 접속 서비스 정보 및 획득한 사용자 정보 출력
        log.info("Provider(서비스): {}, AttributeName(키): {}", registrationId, userNameAttributeName);
        log.debug("OAuth2 원본 데이터: {}", oAuth2User.getAttributes()); // 민감하지 않은 디버그 정보

        OAuthAttributes attributes = OAuthAttributes.of(registrationId , userNameAttributeName , oAuth2User.getAttributes());

        // 3. DB 저장 또는 업데이트 로직 수행
        User user = saveOrUpdate(attributes);

        // 4. [로그] DB 작업 결과
        log.info("DB: OAuth2 로그인 처리 완료. User ID: {}, Email: {}", user.getId(), user.getEmail());

        // PrincipalDetails 객체 생성 및 반환
        return new PrincipalDetails(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getRole(),
                user.getProvider(),
                attributes.getAttributes(), // (원본 JSON은 전달)
                user.getRegion(),
                user.getCreateDate(),
                user.getTotalDistance()
        );
    }

    // DB에 사용자 정보가 있으면 업데이트하고, 없으면 새로 저장하는 메서드
    private User saveOrUpdate(OAuthAttributes attributes) {

        // 1. [로그] DB에서 사용자 정보 조회 시도
        log.info("DB: 이메일({})과 Provider({})로 사용자 조회 시도.", attributes.getEmail(), attributes.getProvider());

        User user = userRepository.findByEmailAndProvider(attributes.getEmail(),attributes.getProvider())

                // 2. [DB 업데이트] 이미 존재하면 이름, 사진 정보만 업데이트
                .map(entity -> {
                    log.info("DB: 기존 사용자({}) 정보 업데이트를 수행합니다.", entity.getEmail());
                    return entity.update(attributes.getUsername(),attributes.getPicture());
                })

                // 3. [DB 저장] 존재하지 않으면 새로 엔티티 생성
                .orElseGet(() -> {
                    log.info("DB: 신규 사용자입니다. 새로운 계정을 저장합니다.");
                    return attributes.toEntity();
                });

        // 4. [로그] DB 저장/업데이트 후 결과를 반환
        User savedUser = userRepository.save(user);
        log.info("DB: 저장/업데이트 완료. User ID: {}", savedUser.getId());
        return savedUser;
    }
}