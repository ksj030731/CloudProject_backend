package chatting.dto;

import chatting.config.auth.PrincipalDetails;
import chatting.domain.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserResponseDTO {

        private Long id;
        private String username;
        private String nickname;
        private String email;
        private String picture;
        private String provider;
        private String role;
        private String region;
        private String joinDate;

        @Builder.Default
        private Double totalDistance = 0.0;

        @Builder.Default
        private List<Long> completedCourses = new ArrayList<>();

        @Builder.Default
        private List<Object> badges = new ArrayList<>();

        // 1. User 엔티티에서 DTO 변환 (DB에서 조회한 최신 정보용)
        public static UserResponseDTO from(User user) {
                return UserResponseDTO.builder()
                                .id(user.getId())
                                .username(user.getUsername())
                                .email(user.getEmail())
                                .nickname(user.getNickname())
                                .picture(user.getPicture())
                                .region(user.getRegion())
                                .provider(user.getProvider())
                                .role(user.getRole())
                                // 실제 가입일 연결 (null일 경우 대비)
                                .joinDate(user.getCreateDate() != null
                                                ? user.getCreateDate().toLocalDate().toString()
                                                : java.time.LocalDate.now().toString())

                                // 실제 총 거리 연결
                                .totalDistance(user.getTotalDistance() != null ? user.getTotalDistance() : 0.0)
                                .completedCourses(Collections.emptyList())
                                .badges(user.getUserBadges() != null ? user.getUserBadges().stream()
                                                .map(userBadge -> new BadgeDto(userBadge.getBadge()))
                                                .collect(Collectors.toList()) : Collections.emptyList())
                                .build();
        }

        // 2. PrincipalDetails(세션)에서 DTO 변환 (로그인 직후 등 세션 정보용)
        public static UserResponseDTO from(PrincipalDetails principalDetails) {
                // principalDetails에는 nickname, picture 필드가 없으므로
                // username을 닉네임 대신 사용하고, 사진은 null로 설정합니다.
                return UserResponseDTO.builder()
                                .id(principalDetails.getId())
                                .username(principalDetails.getUsername())
                                .email(principalDetails.getEmail())
                                .nickname(principalDetails.getUsername()) // nickname 대신 username 사용 (fallback)
                                .picture(null) // picture 정보 없음
                                .region(principalDetails.getRegion())
                                .provider(principalDetails.getProvider())
                                .role(principalDetails.getRole())
                                // 실제 가입일 연결
                                // PrincipalDetails에 값이 있으면 변환, 없으면 현재 날짜
                                .joinDate(principalDetails.getCreateDate() != null
                                                ? principalDetails.getCreateDate().toLocalDate().toString()
                                                : java.time.LocalDate.now().toString())

                                // 실제 총 거리 연결
                                .totalDistance(principalDetails.getTotalDistance() != null
                                                ? principalDetails.getTotalDistance()
                                                : 0.0)
                                .completedCourses(Collections.emptyList())
                                .badges(Collections.emptyList()) // 세션 정보에는 뱃지 정보가 없으므로 빈 리스트
                                .build();
        }
}