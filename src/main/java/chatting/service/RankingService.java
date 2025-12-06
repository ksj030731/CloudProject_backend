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

    private final chatting.repository.UserRepository userRepository;
    private final CourseCompletionRepository completionRepository;
    private final CourseRepository courseRepository;

    // 1. 코스별 랭킹 조회
    @Transactional(readOnly = true)
    public List<CourseRankingResponse> getCourseRankings() {
        log.info("--- 코스별 랭킹 계산을 시작합니다. ---");
        List<Course> courses = courseRepository.findAll();
        List<CourseCompletion> allCompletions = completionRepository.findAll();

        List<CourseRankingResponse> result = new ArrayList<>();

        for (Course course : courses) {
            List<CourseCompletion> courseCompletions = allCompletions.stream()
                    .filter(c -> c.getCourse().getId().equals(course.getId()))
                    .sorted(Comparator.comparingInt(CourseCompletion::getCompletionCount).reversed())
                    .collect(Collectors.toList());

            List<UserRanking> rankings = new ArrayList<>();
            int rank = 1;
            for (CourseCompletion cc : courseCompletions) {
                rankings.add(UserRanking.builder()
                        .rank(rank++)
                        .userId(cc.getUser().getId())
                        .userName(cc.getUser().getNickname())
                        // [변경] 코스별 랭킹에서도 유저의 '총 누적 완주 횟수'를 표시 (요청사항 반영)
                        .completionCount(cc.getUser().getCompletedCourseCount())
                        .bestTime(cc.getCompletionTime())
                        // [변경] CourseCompletion의 코스별 누적 거리 사용
                        .totalDistance(cc.getCourseTotalDistance() != null ? cc.getCourseTotalDistance() : 0.0)
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
        log.info("--- 2. 전체 통합 랭킹 계산을 시작합니다. (User 테이블 기반) ---");

        // 1. 모든 유저 조회
        List<chatting.domain.User> users = userRepository.findAll();

        // 2. UserRanking 리스트 생성 (거리: User.totalDistance, 횟수: User.completedCourseCount)
        List<UserRanking> rankings = users.stream()
                .filter(u -> u.getTotalDistance() > 0) // 거리가 0보다 큰 유저만 랭킹에 표시
                .sorted(Comparator.comparingDouble(chatting.domain.User::getTotalDistance).reversed()) // 총 거리 내림차순 정렬
                .map(user -> UserRanking.builder()
                        .userId(user.getId())
                        .userName(user.getNickname())
                        .totalDistance(Math.round(user.getTotalDistance() * 10) / 10.0)
                        // [변경] User 테이블의 completedCourseCount (누적) 사용
                        .completionCount(user.getCompletedCourseCount())
                        .build())
                .collect(Collectors.toList());

        // 순위 매기기
        int rank = 1;
        List<UserRanking> rankedList = new ArrayList<>();
        for (UserRanking r : rankings) {
            rankedList.add(UserRanking.builder()
                    .rank(rank++)
                    .userId(r.getUserId())
                    .userName(r.getUserName())
                    .totalDistance(r.getTotalDistance())
                    .completionCount(r.getCompletionCount())
                    .build());
        }

        return GlobalRankingResponse.builder()
                .period("all-time")
                .rankings(rankedList)
                .lastUpdated(LocalDate.now().toString())
                .build();
    }
}