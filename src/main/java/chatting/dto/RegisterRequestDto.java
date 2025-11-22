package chatting.dto;

import chatting.domain.User;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.crypto.password.PasswordEncoder;

@Getter
@Setter
public class RegisterRequestDto {
    // 폼에서 받을 데이터
    private String username;
    private String password;
    private String email;
    private String nickname;
    private String region;

    // DTO -> Entity 변환 (비밀번호 암호화, 권한/제공자 설정)
    public User toEntity(PasswordEncoder passwordEncoder) {
        return User.builder()
                .username(username)
                .password(passwordEncoder.encode(password))
                .email(email)
                .nickname(nickname)
                .region(region)
                .provider("local") // (일반 가입)
                .role("ROLE_USER") // (일반 가입은 바로 정회원)
                .build();
    }
}
