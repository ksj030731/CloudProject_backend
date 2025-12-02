package chatting.service;

import chatting.config.auth.PrincipalDetails;
import chatting.domain.User;
import chatting.repository.UserRepository;
import lombok.extern.slf4j.Slf4j; // 💡 로그 사용을 위한 import
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Slf4j // Lombok을 사용하여 로그 기능 활성화
@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * 사용자가 입력한 username으로 DB에서 UserDetails 객체를 로드합니다.
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        // 1. [로그] DB 조회 시도
        log.info("DB: 사용자 인증을 위해 username '{}'으로 조회를 시작합니다.", username);

        User user = userRepository.findByUsername(username)

                // 2. [예외 처리] 사용자가 DB에 없으면 예외 발생
                .orElseThrow(() -> {
                    log.error("DB: 사용자 조회 실패. username '{}'에 해당하는 사용자가 없습니다.", username);
                    // Spring Security가 요구하는 예외를 던집니다.
                    return new UsernameNotFoundException("사용자를 찾을 수 없습니다: " + username);
                });

        // 3. [로그] DB 조회 성공
        log.info("DB: 사용자 인증 성공. User ID: {}, Role: {}", user.getId(), user.getRole());

        // 4. 조회된 User 엔티티 정보를 바탕으로 PrincipalDetails 객체를 생성하여 반환합니다.
        return new PrincipalDetails(
                user.getId(),
                user.getUsername(),
                user.getPassword(),
                user.getEmail(),
                user.getRole(),
                user.getProvider(),
                user.getRegion(),
                user.getCreateDate(),
                user.getTotalDistance()
        );
    }
}