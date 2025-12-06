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
    private chatting.service.BadgeService badgeService;

    public UserController(UserService userService, FavoriteService favoriteService,
            chatting.service.BadgeService badgeService) {
        this.favoriteService = favoriteService;
        this.userService = userService;
        this.badgeService = badgeService;
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

        // 1. [수정] DB에서 최신 유저 정보 조회 (세션 정보 대신 사용)
        chatting.domain.User user = userService.findById(userPrincipal.getId());
        UserResponseDTO dto = UserResponseDTO.from(user);

        // 2. [추가된 로직] DB에서 찜 목록(Course ID) 조회
        List<Long> favoriteIds = favoriteService.getFavoriteCourseIds(userPrincipal.getId());

        // 3. DTO에 찜 목록 탑재
        dto.setFavorites(favoriteIds);

        // (completedCourses는 이제 UserResponseDTO.from(user) 내부에서 자동 처리되므로 별도 설정 불필요)

        // 4. [추가된 로직] 획득 뱃지 목록 조회 및 탑재
        List<chatting.dto.BadgeDto> myBadges = badgeService.getUserBadges(userPrincipal.getId());
        dto.setBadges(new java.util.ArrayList<>(myBadges));

        log.info("API 응답: User ID {} (찜 {}개, 뱃지 {}개 포함) 반환 완료.", userPrincipal.getId(), favoriteIds.size(),
                myBadges.size());

        return ResponseEntity.ok(dto);
    }

    @GetMapping("/challenges")
    public ResponseEntity<List<chatting.dto.ChallengeDto>> getChallenges(
            @AuthenticationPrincipal PrincipalDetails userPrincipal) {
        if (userPrincipal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        List<chatting.dto.ChallengeDto> challenges = badgeService.getUserChallenges(userPrincipal.getId());
        return ResponseEntity.ok(challenges);
    }

    @PutMapping("/me")
    public ResponseEntity<UserResponseDTO> updateMe(
            @AuthenticationPrincipal PrincipalDetails userPrincipal,
            @RequestBody UserUpdateDTO userUpdateDTO) {

        log.info("API 호출 : PUT /api/user/me - 사용자 정보 수정 요청 . ID : {} ", userPrincipal.getId());

        if (userPrincipal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        UserResponseDTO updateUser = userService.updateUser(userPrincipal.getId(), userUpdateDTO);

        log.info("API 응답 : 사용자 정보 수정 완료. 변경된 닉네임 {}", updateUser.getNickname());

        return ResponseEntity.ok(updateUser);

    }
}