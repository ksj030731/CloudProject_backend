package chatting.controller;

import chatting.dto.RankingDto.CourseRankingResponse;
import chatting.dto.RankingDto.GlobalRankingResponse;
import chatting.service.RankingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j // 로그 기능 활성화
@RestController
@RequestMapping("/api/rankings") // 공통 경로 설정
@RequiredArgsConstructor
public class RankingController {

    private final RankingService rankingService;

    /**
     * [GET] /api/rankings/courses
     * 코스별 랭킹 목록을 조회하여 반환합니다.
     */
    @GetMapping("/courses")
    public ResponseEntity<List<CourseRankingResponse>> getCourseRankings() {

        log.info("API 호출: GET /api/rankings/courses - 코스별 랭킹 조회 시작.");

        // Service 호출
        List<CourseRankingResponse> rankings = rankingService.getCourseRankings();

        log.info("API 응답: 총 {}개의 코스 랭킹 데이터 반환 완료.", rankings.size());

        return ResponseEntity.ok(rankings);
    }

    /**
     * [GET] /api/rankings/global
     * 전체 통합 랭킹을 조회하여 반환합니다.
     */
    @GetMapping("/global")
    public ResponseEntity<GlobalRankingResponse> getGlobalRanking() {

        log.info("API 호출: GET /api/rankings/global - 전체 통합 랭킹 조회 시작.");

        // Service 호출
        GlobalRankingResponse ranking = rankingService.getGlobalRanking();

        log.info("API 응답: 통합 랭킹 데이터 반환 완료.");

        return ResponseEntity.ok(ranking);
    }
}