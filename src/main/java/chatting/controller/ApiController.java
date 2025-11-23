package chatting.controller;

import chatting.config.auth.PrincipalDetails;
import chatting.dto.SocialRegisterRequestDto;
import chatting.dto.UserResponseDTO;
import chatting.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api") // 공통 주소 설정
public class ApiController {

    public final UserService userService;

    public ApiController(UserService userService) {
        this.userService = userService;
    }

    // 1. 소셜 로그인 추가 정보 입력 및 가입 완료
    // URL: POST /api/auth/register-social
    @PostMapping("/auth/register-social")
    public ResponseEntity<String> completeSignup(@AuthenticationPrincipal PrincipalDetails userPrincipal,
                                                 @RequestBody SocialRegisterRequestDto signupDTO) {

        // UserService에서 정보 업데이트 및 등급 변경(GUEST -> USER) 처리
        userService.completeSocialSignup(userPrincipal.getId(), signupDTO);

        return ResponseEntity.ok("추가 정보 입력 및 가입 완료!");
    }

    // 2. 내 정보 조회 (프론트엔드 fetchUserWithToken에서 호출)
    // URL: GET /api/me
    @GetMapping("/me")
    public ResponseEntity<UserResponseDTO> me(@AuthenticationPrincipal PrincipalDetails userPrincipal) {

        // 인증 정보가 없으면 401 에러 반환
        if (userPrincipal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        // 인증된 사용자 정보를 DTO로 변환하여 반환
        UserResponseDTO dto = UserResponseDTO.from(userPrincipal);
        return ResponseEntity.ok(dto);
    }
}