package chatting.controller;

import chatting.config.auth.PrincipalDetails;
import chatting.dto.GeneralRegisterRequestDto;
import chatting.dto.LoginRequestDto;
import chatting.dto.SocialRegisterRequestDto;
import chatting.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpSession;
@Slf4j
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;
    private final AuthenticationManager authenticationManager;

    // 1. 일반 회원가입
    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody GeneralRegisterRequestDto dto) {
        userService.registerUser(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body("회원가입 성공");
    }

    // 2. 일반 로그인
    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody LoginRequestDto dto, HttpSession session) { // 💡 HttpSession 주입
        try {
            UsernamePasswordAuthenticationToken token =
                    new UsernamePasswordAuthenticationToken(dto.getUsername(), dto.getPassword());

            Authentication authentication = authenticationManager.authenticate(token);
            SecurityContextHolder.getContext().setAuthentication(authentication);

            // ✨ [핵심 수정] 단순히 "성공" 글자 대신, '세션 ID'를 반환합니다.
            // 프론트엔드는 이 ID를 'authToken'으로 사용할 것입니다.

            return ResponseEntity.ok(session.getId());

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인 실패");
        }
    }

    // 3. 소셜 로그인 추가 정보 입력 (GUEST -> USER 등업)
    @PostMapping("/register-social")
    public ResponseEntity<String> completeSocialSignup(@AuthenticationPrincipal PrincipalDetails userPrincipal,
                                                       @RequestBody SocialRegisterRequestDto dto) {

        // A. DB 업데이트 (Service 위임)
        userService.completeSocialSignup(userPrincipal.getId(), dto);

        // B. [중요] 현재 세션의 권한 정보를 'GUEST' -> 'USER'로 실시간 갱신 (옛날 코드의 장점 흡수!)
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        // 업데이트된 정보로 새로운 PrincipalDetails 생성 (권한을 ROLE_USER로 강제 설정한다고 가정)
        // 실제로는 DB에서 다시 조회해오는 게 가장 확실하지만, 성능상 여기선 기존 정보에 role만 바꿔서 갱신하는 트릭을 씁니다.
        PrincipalDetails newPrincipal = new PrincipalDetails(
                userPrincipal.getId(),
                userPrincipal.getUsername(),
                userPrincipal.getEmail(),
                "ROLE_USER", // 강제로 USER 권한 부여
                userPrincipal.getProvider(),
                userPrincipal.getAttributes(),
                dto.getRegion() // 새로 입력받은 지역 정보 반영
        );


        // 새로운 인증 객체 생성 및 세션 등록
        Authentication newAuth = new UsernamePasswordAuthenticationToken(
                newPrincipal,
                authentication.getCredentials(),
                newPrincipal.getAuthorities()
        );
        SecurityContextHolder.getContext().setAuthentication(newAuth);

        return ResponseEntity.ok("소셜 회원가입이 완료되었습니다.");
    }
}