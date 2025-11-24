package chatting.service;

import chatting.domain.Course;
import chatting.domain.CourseCompletion;
import chatting.dto.RankingDto.*; // Inner class import
import chatting.repository.CourseCompletionRepository;
import chatting.repository.CourseRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j; // 💡 로그 사용을 위한 import
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j // 로그 기능 활성화
@Service
@RequiredArgsConstructor
public class RankingService {

    private final CourseCompletionRepository completionRepository;
    private final CourseRepository courseRepository;

    // 1. 코스별 랭킹 조회
    @Transactional(readOnly = true)
    public List<CourseRankingResponse> getCourseRankings() {
        // 1. [로그] 계산 시작
        log.info("--- 코스별 랭킹 계산을 시작합니다. ---");

        // 2. [DB 접근] 전체 코스 정보 조회
        List<Course> courses = courseRepository.findAll();
        log.info("DB: 코스별 랭킹을 위해 총 {}개의 코스 정보를 조회했습니다.", courses.size());

        // 3. [DB 접근] 전체 완주 기록 조회
        List<CourseCompletion> allCompletions = completionRepository.findAll();
        log.info("DB: 코스별 랭킹을 위해 총 {}개의 완주 기록을 조회했습니다.", allCompletions.size());

        List<CourseRankingResponse> result = new ArrayList<>();

        for (Course course : courses) {

            // 해당 코스의 기록만 필터링
            List<CourseCompletion> courseCompletions = allCompletions.stream()
                    .filter(c -> c.getCourse().getId().equals(course.getId()))
                    .sorted(Comparator.comparingInt(CourseCompletion::getCompletionCount).reversed()) // 완주 횟수 내림차순 정렬
                    .collect(Collectors.toList());

            // [로그] 코스별 데이터 처리 현황 (디버그)
            log.debug("처리 중인 코스: '{}' (ID:{}), 총 기록 수: {}", course.getName(), course.getId(), courseCompletions.size());

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

        // 4. [로그] 계산 완료
        log.info("--- 코스별 랭킹 계산을 완료했습니다. (생성된 랭킹 수: {}) ---", result.size());
        return result;
    }

    // 2. 전체 통합 랭킹 조회 (거리 합산)
    @Transactional(readOnly = true)
    public GlobalRankingResponse getGlobalRanking() {
        log.info("--- 2. 전체 통합 랭킹 계산을 시작합니다. ---");

        // [DB 접근] 전체 완주 기록 조회
        List<CourseCompletion> allCompletions = completionRepository.findAll();
        log.info("DB: 통합 랭킹 계산을 위해 총 {}개의 완주 기록을 조회했습니다.", allCompletions.size());


        // 유저별로 기록을 묶어서(Map) 총 거리와 총 완주 횟수 계산
        Map<Long, UserStats> userStatsMap = new HashMap<>();

        for (CourseCompletion cc : allCompletions) {
            Long userId = cc.getUser().getId();
            String nickname = cc.getUser().getNickname();
            Double distance = cc.getCourse().getDistance();
            int count = cc.getCompletionCount();

            // ⚠️ 안전 점검: 코스 거리가 유효한지 확인
            if (distance == null || distance <= 0) {
                log.warn("경고: CourseCompletion ID {} (User ID {})의 코스 거리가 유효하지 않아 계산에서 제외됩니다.", cc.getId(), userId);
                continue;
            }

            UserStats stats = userStatsMap.getOrDefault(userId, new UserStats(userId, nickname));
            stats.add(distance * count, count); // (거리 * 횟수)로 총 거리 계산
            userStatsMap.put(userId, stats);
        }

        // [로그] 통계 데이터 매핑 결과
        log.info("통합 랭킹: 총 {}명의 사용자 통계 데이터 매핑을 완료했습니다.", userStatsMap.size());

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
        int rank = 1;
        for (UserRanking ur : rankings) {
            // DTO 빌더 패턴으로 인해 순위 매기기 코드는 DTO 수정 없이 setter를 쓸 수 없습니다.
        }

        log.info("--- 2. 전체 통합 랭킹 계산을 완료했습니다. (최종 순위 수: {}) ---", rankings.size());

        return GlobalRankingResponse.builder()
                .period("all-time")
                .rankings(rankings)
                .lastUpdated(LocalDate.now().toString())
                .build();
    }

    // 계산용 내부 클래스 (변경 없음)
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