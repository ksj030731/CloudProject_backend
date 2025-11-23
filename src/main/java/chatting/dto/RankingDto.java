package chatting.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

public class RankingDto {

    // 1. 유저 랭킹 정보 (공통)
    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserRanking {
        private int rank;
        private Long userId;
        private String userName;
        private Integer completionCount; // 완주 횟수
        private String bestTime; // 최고 기록 (코스별)
        private Double totalDistance; // 총 거리 (전체)
        private String lastCompletionDate;
        // 뱃지 리스트 등은 복잡도를 위해 생략하거나 필요시 추가
    }

    // 2. 코스별 랭킹 응답
    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CourseRankingResponse {
        private Long courseId;
        private String courseName;
        private String period; // "all-time"
        private List<UserRanking> rankings;
        private String lastUpdated;
    }

    // 3. 전체 통합 랭킹 응답
    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class GlobalRankingResponse {
        private String period; // "all-time"
        private List<UserRanking> rankings;
        private String lastUpdated;
    }
}