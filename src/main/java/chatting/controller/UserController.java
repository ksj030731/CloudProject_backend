package chatting.controller;

import chatting.config.auth.PrincipalDetails;
import chatting.dto.UserResponseDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/user") // User 관련 API는 '/api/user'로 통일
public class UserController {

    /**
     * [GET] /api/user/me
     * 현재 인증된 사용자 정보 조회 API
     */
    @GetMapping("/me")
    public ResponseEntity<UserResponseDTO> me(@AuthenticationPrincipal PrincipalDetails userPrincipal) {

        log.info("API 호출: GET /api/user/me - 현재 사용자 정보 조회 시작.");

        // 1. 인증 정보 확인
        if (userPrincipal == null) {
            log.warn("API 응답: 인증 정보 누락. HTTP 401 UNAUTHORIZED 반환.");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        // 2. 인증된 사용자 정보를 DTO로 변환하여 반환
        UserResponseDTO dto = UserResponseDTO.from(userPrincipal);

        log.info("API 응답: User ID {} 정보 반환 완료. (HTTP 200 OK)", userPrincipal.getId());

        return ResponseEntity.ok(dto);
    }
}