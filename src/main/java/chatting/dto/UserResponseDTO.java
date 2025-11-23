package chatting.dto;

import chatting.config.auth.PrincipalDetails;
import chatting.domain.User; // User 엔티티 import (from(User) 메서드용)
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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
                .joinDate("2024-01-01") // 임시 데이터
                .totalDistance(0.0)     // 임시 데이터
                .completedCourses(Collections.emptyList())
                .badges(Collections.emptyList())
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
                .joinDate("2024-01-01")
                .totalDistance(0.0)
                .completedCourses(Collections.emptyList())
                .badges(Collections.emptyList())
                .build();
    }
}