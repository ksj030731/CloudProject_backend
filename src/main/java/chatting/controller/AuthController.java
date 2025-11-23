package chatting.controller;

import chatting.domain.User;
import chatting.dto.RegisterRequestDto;
import chatting.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    // 1. 일반 회원가입 처리
    // URL: POST /api/auth/register
    @PostMapping("/register")
    public String register(RegisterRequestDto registerRequestDto) {
        // (참고: 실무에서는 Service 계층으로 로직을 옮기는 것이 좋습니다)
        User user = registerRequestDto.toEntity(passwordEncoder);
        userRepository.save(user);

        return "redirect:/login"; // 회원가입 성공 시 로그인 페이지로
    }

    // ❌ [삭제됨] registerSocialUpdate 메서드
    // 이유: ApiController의 completeSignup 메서드와 URL(/api/auth/register-social)이 겹쳐서
    // 서버 실행 오류(Ambiguous mapping)가 발생하므로 삭제했습니다.
    // 이제 해당 기능은 ApiController에서 담당합니다.
}