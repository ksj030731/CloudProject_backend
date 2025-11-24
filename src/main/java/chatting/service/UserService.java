package chatting.service;

import chatting.domain.User;
import chatting.dto.GeneralRegisterRequestDto; // 일반 가입용 DTO import
import chatting.dto.SocialRegisterRequestDto;
import chatting.repository.UserRepository;
import lombok.RequiredArgsConstructor; // 💡 생성자 주입을 간단하게
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder; // 💡 비밀번호 암호화 도구
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Transactional
@Service
@RequiredArgsConstructor // final 필드에 대한 생성자를 자동으로 만들어줍니다.
public class UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder; // 💡 암호화를 위해 주입받음

    /**
     * 1. 일반 회원가입 (ID/PW 가입)
     * - 비밀번호를 암호화해서 DB에 저장합니다.
     */
    public void registerUser(GeneralRegisterRequestDto dto) {

        // 1. [로그] 가입 요청 확인
        log.info("일반 회원가입 요청: username={}", dto.getUsername());

        // 2. [중복 검사] 이미 존재하는 아이디인지 확인
        if (userRepository.findByUsername(dto.getUsername()).isPresent()) {
            log.warn("회원가입 실패: 이미 존재하는 아이디({})입니다.", dto.getUsername());
            throw new RuntimeException("이미 존재하는 아이디입니다.");
        }

        // 3. [엔티티 생성] 비밀번호 암호화 필수!
        User user = User.builder()
                .username(dto.getUsername())
                .password(bCryptPasswordEncoder.encode(dto.getPassword())) // 🔒 비밀번호 암호화
                .email(dto.getEmail())
                .nickname(dto.getNickname())
                .region(dto.getRegion())
                .role("ROLE_USER") // 일반 가입은 바로 정회원
                .provider("general") // 일반 가입 표시
                .build();

        // 4. [DB 저장]
        userRepository.save(user);
        log.info("일반 회원가입 완료: User ID={}, Username={}", user.getId(), user.getUsername());
    }

    /**
     * 2. 소셜 로그인 후 추가 정보 입력 (기존 코드 유지)
     */
    public void completeSocialSignup(Long userId, SocialRegisterRequestDto dto){

        log.info("DB: 소셜 회원가입 정보 업데이트 시작. User ID: {}", userId);

        User user = userRepository.findById(userId)
                .orElseThrow(()-> {
                    log.error("업데이트 실패: ID {}에 해당하는 사용자를 DB에서 찾을 수 없습니다.", userId);
                    return new RuntimeException("유저를 찾을 수 없습니다.");
                });

        user.setNickname(dto.getNickname());
        user.setRegion(dto.getRegion());
        user.setRole("ROLE_USER");

        log.info("DB: 소셜 회원가입 정보 업데이트 완료. User ID: {}", userId);
    }
}