package chatting.controller;

import chatting.config.auth.PrincipalDetails;
import chatting.domain.User;
import chatting.dto.RegisterRequestDto;
import chatting.dto.SocialRegisterRequestDto;
import chatting.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
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

    // 1.일반 회원가입 처리
    @PostMapping("/register")
    public String register(RegisterRequestDto registerRequestDto) {

        // (참고: username, email 중복 체크 등 예외처리 로직이 필요합니다)
        User user = registerRequestDto.toEntity(passwordEncoder);
        userRepository.save(user);

        return "redirect:/login"; // 회원가입 성공 시 로그인 페이지로
    }

    // 1. 일반 회원가입 처리
    // (소셜 추가 정보 입력 - 변경)
    @PostMapping("/register-social")
    public String registerSocialUpdate(
            SocialRegisterRequestDto socialRegisterRequestDto,
            @AuthenticationPrincipal PrincipalDetails principalDetails
    ) {
        // (변경) principalDetails.getUser() 대신 getId() 사용
        Long userId = principalDetails.getId();

        // (핵심) DB에서 User 엔티티를 '다시 조회' (새 트랜잭션)
        User userEntity = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        // 3. 닉네임, 지역 정보 업데이트 + ROLE_USER로 등업
        userEntity.updateSocialInfo(
                socialRegisterRequestDto.getNickname(),
                socialRegisterRequestDto.getRegion()
        );
        userRepository.save(userEntity);

        // 4. 세션(SecurityContext) 인증 정보 갱신

        // (변경) User 엔티티 대신, 업데이트된 데이터로 새 PrincipalDetails 생성
        PrincipalDetails newPrincipalDetails = new PrincipalDetails(
                userEntity.getId(),
                userEntity.getUsername(),
                userEntity.getEmail(),
                userEntity.getRole(), // "ROLE_USER"로 변경됨
                userEntity.getProvider(),
                principalDetails.getAttributes(), // 기존 attributes
                userEntity.getRegion()
        );

        // 4-2. 새 Authentication 객체 생성
        Authentication newAuthentication = new UsernamePasswordAuthenticationToken(
                newPrincipalDetails,
                null,
                newPrincipalDetails.getAuthorities() // (업데이트된 권한)
        );

        // 4-3. SecurityContext에 새 Authentication 객체 설정
        SecurityContextHolder.getContext().setAuthentication(newAuthentication);

        return "redirect:/"; // 메인 페이지로
    }






















}
