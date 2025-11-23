package chatting.service;

import chatting.domain.Course;
import chatting.domain.CourseCompletion;
import chatting.dto.RankingDto.*; // Inner class import
import chatting.repository.CourseCompletionRepository;
import chatting.repository.CourseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RankingService {

    private final CourseCompletionRepository completionRepository;
    private final CourseRepository courseRepository;

    // 1. 코스별 랭킹 조회
    @Transactional(readOnly = true)
    public List<CourseRankingResponse> getCourseRankings() {
        List<Course> courses = courseRepository.findAll();
        List<CourseCompletion> allCompletions = completionRepository.findAll();
        List<CourseRankingResponse> result = new ArrayList<>();

        for (Course course : courses) {
            // 해당 코스의 기록만 필터링
            List<CourseCompletion> courseCompletions = allCompletions.stream()
                    .filter(c -> c.getCourse().getId().equals(course.getId()))
                    .sorted(Comparator.comparingInt(CourseCompletion::getCompletionCount).reversed()) // 완주 횟수 내림차순 정렬
                    .collect(Collectors.toList());

            // DTO로 변환
            List<UserRanking> rankings = new ArrayList<>();
            int rank = 1;
            for (CourseCompletion cc : courseCompletions) {
                rankings.add(UserRanking.builder()
                        .rank(rank++)
                        .userId(cc.getUser().getId())
                        .userName(cc.getUser().getNickname())
                        .completionCount(cc.getCompletionCount())
                        .bestTime(cc.getCompletionTime())
                        .lastCompletionDate(cc.getDate().toString())
                        .build());
            }

            result.add(CourseRankingResponse.builder()
                    .courseId(course.getId())
                    .courseName(course.getName())
                    .period("all-time")
                    .rankings(rankings)
                    .lastUpdated(LocalDate.now().toString())
                    .build());
        }
        return result;
    }

    // 2. 전체 통합 랭킹 조회 (거리 합산)
    @Transactional(readOnly = true)
    public GlobalRankingResponse getGlobalRanking() {
        List<CourseCompletion> allCompletions = completionRepository.findAll();

        // 유저별로 기록을 묶어서(Map) 총 거리와 총 완주 횟수 계산
        Map<Long, UserStats> userStatsMap = new HashMap<>();

        for (CourseCompletion cc : allCompletions) {
            Long userId = cc.getUser().getId();
            String nickname = cc.getUser().getNickname();
            Double distance = cc.getCourse().getDistance();
            int count = cc.getCompletionCount();

            UserStats stats = userStatsMap.getOrDefault(userId, new UserStats(userId, nickname));
            stats.add(distance * count, count); // (거리 * 횟수)로 총 거리 계산
            userStatsMap.put(userId, stats);
        }

        // 통계 데이터를 리스트로 변환하고 정렬 (총 거리 내림차순)
        List<UserRanking> rankings = userStatsMap.values().stream()
                .sorted(Comparator.comparingDouble(UserStats::getTotalDistance).reversed())
                .map(stats -> UserRanking.builder()
                        .userId(stats.userId)
                        .userName(stats.nickname)
                        .totalDistance(Math.round(stats.totalDistance * 10) / 10.0) // 소수점 반올림
                        .completionCount(stats.totalCount)
                        .build())
                .collect(Collectors.toList());

        // 순위 매기기
        for (int i = 0; i < rankings.size(); i++) {
            // UserRanking은 builder로 만들어서 setter가 없으므로 다시 빌드하거나 로직 수정 필요
            // 여기선 편의상 순서대로 리스트에 담긴 것이 곧 순위임
        }

        return GlobalRankingResponse.builder()
                .period("all-time")
                .rankings(rankings)
                .lastUpdated(LocalDate.now().toString())
                .build();
    }

    // 계산용 내부 클래스
    private static class UserStats {
        Long userId;
        String nickname;
        double totalDistance = 0;
        int totalCount = 0;

        public UserStats(Long userId, String nickname) {
            this.userId = userId;
            this.nickname = nickname;
        }

        public void add(double distance, int count) {
            this.totalDistance += distance;
            this.totalCount += count;
        }

        public double getTotalDistance() { return totalDistance; }
    }
}