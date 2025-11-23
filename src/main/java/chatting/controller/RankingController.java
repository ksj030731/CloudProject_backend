package chatting.controller;

import chatting.dto.RankingDto.*; // Inner class DTO들
import chatting.service.RankingService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class RankingController {

    private final RankingService rankingService;

    // 코스별 랭킹 조회
    @GetMapping("/api/rankings/courses")
    public List<CourseRankingResponse> getCourseRankings() {
        return rankingService.getCourseRankings();
    }

    // 전체 통합 랭킹 조회
    @GetMapping("/api/rankings/global")
    public GlobalRankingResponse getGlobalRanking() {
        return rankingService.getGlobalRanking();
    }
}