package chatting.controller;

import chatting.dto.ReviewRequest;
import chatting.dto.ReviewResponseDto;
import chatting.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    // 1. 리뷰 목록 조회 (GET)
    @GetMapping("/api/reviews")
    public List<ReviewResponseDto> getReviews() {
        return reviewService.getAllReviews();
    }

    // 2. 리뷰 작성 (POST)
    @PostMapping("/api/reviews")
    public ReviewResponseDto createReview(@RequestBody ReviewRequest request) {
        return reviewService.createReview(request);
    }
}