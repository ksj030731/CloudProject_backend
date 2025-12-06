package chatting.dto;

import chatting.config.auth.PrincipalDetails;
import chatting.domain.User; // User 엔티티 import (from(User) 메서드용)
import lombok.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Setter
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
    private List<Long> favorites = new ArrayList<>();

    @Builder.Default
    private Double totalDistance = 0.0;

    @Builder.Default
    private Integer completedCourseCount = 0;

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
                // 완주 코스 개수 연결
                .completedCourseCount(user.getCompletedCourseCount() != null ? user.getCompletedCourseCount() : 0)
                // 완주한 코스 ID 목록 연결
                .completedCourses(user.getUserCourses().stream()
                        .map(uc -> uc.getCourse().getId())
                        .collect(java.util.stream.Collectors.toList()))
                .badges(Collections.emptyList()) // 뱃지는 별도 로직으로 채우거나 여기서 생략(Controller에서 채움)
                .build();
    }

    // 2. PrincipalDetails(세션)에서 DTO 변환 (로그인 직후 등 세션 정보용)
    // UserResponseDTO.java

    // ... 기존 코드 ...

    // 2. PrincipalDetails(세션)에서 DTO 변환
    public static UserResponseDTO from(PrincipalDetails principalDetails) {

        // 실제 닉네임을 가져오고, 없으면 기본값 설정
        String displayName = principalDetails.getNickname();
        if (displayName == null || displayName.isEmpty()) {
            // 닉네임이 없으면 이메일 앞부분이라도 보여주기 (google_... 보단 나음)
            displayName = principalDetails.getEmail().split("@")[0];
        }

        return UserResponseDTO.builder()
                .id(principalDetails.getId())
                .username(principalDetails.getUsername())
                .email(principalDetails.getEmail())

                .nickname(displayName) //

                .picture(null) // 세션에 사진 정보가 없다면 null이 맞음
                .region(principalDetails.getRegion())
                .provider(principalDetails.getProvider())
                .role(principalDetails.getRole())

                // 가입일 처리 (null 안전 처리)
                .joinDate(principalDetails.getCreateDate() != null
                        ? principalDetails.getCreateDate().toLocalDate().toString()
                        : java.time.LocalDate.now().toString())

                // 거리 처리
                .totalDistance(principalDetails.getTotalDistance() != null
                        ? principalDetails.getTotalDistance()
                        : 0.0)
                .completedCourseCount(0) // 세션 정보에는 아직 없을 수 있음
                .completedCourses(Collections.emptyList())
                .badges(Collections.emptyList())
                .build();
    }
}