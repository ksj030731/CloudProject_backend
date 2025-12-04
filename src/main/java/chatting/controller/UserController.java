package chatting.controller;

import chatting.config.auth.PrincipalDetails;
import chatting.dto.UserResponseDTO;
import chatting.dto.UserUpdateDTO;
import chatting.service.FavoriteService;
import chatting.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.engine.spi.Resolution;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/api/user") // User 관련 API는 '/api/user'로 통일
public class UserController {



    private UserService userService;
    private FavoriteService favoriteService;

    public UserController(UserService userService , FavoriteService favoriteService) {
        this.favoriteService = favoriteService;
        this.userService = userService;
    }

    /**
     * [GET] /api/user/me
     * 현재 인증된 사용자 정보 조회 API
     */

    @GetMapping("/me")
    public ResponseEntity<UserResponseDTO> me(@AuthenticationPrincipal PrincipalDetails userPrincipal) {

        log.info("API 호출: GET /api/user/me");

        if (userPrincipal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        // 1. 기본 유저 정보 DTO 생성 (기존 로직)
        UserResponseDTO dto = UserResponseDTO.from(userPrincipal);

        // ✨ 2. [추가된 로직] DB에서 찜 목록(Course ID) 조회
        // User 엔티티나 Security 로직을 건드리지 않고, 여기서 직접 조회해서 끼워 넣습니다.
        List<Long> favoriteIds = favoriteService.getFavoriteCourseIds(userPrincipal.getId());

        // 3. DTO에 찜 목록 탑재
        dto.setFavorites(favoriteIds);

        log.info("API 응답: User ID {} (찜 {}개 포함) 반환 완료.", userPrincipal.getId(), favoriteIds.size());

        return ResponseEntity.ok(dto);
    }


    @PutMapping("/me")
    public ResponseEntity<UserResponseDTO> updateMe(
            @AuthenticationPrincipal PrincipalDetails userPrincipal,
            @RequestBody UserUpdateDTO userUpdateDTO){

        log.info("API 호출 : PUT /api/user/me - 사용자 정보 수정 요청 . ID : {} ", userPrincipal.getId());


        if(userPrincipal == null){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }


        UserResponseDTO updateUser = userService.updateUser(userPrincipal.getId(), userUpdateDTO);

        log.info("API 응답 : 사용자 정보 수정 완료. 변경된 닉네임 {}", updateUser.getNickname());

        return ResponseEntity.ok(updateUser);


    }
}