package chatting.controller;

import chatting.dto.ReviewRequest;
import chatting.dto.ReviewResponseDto;
import chatting.service.ReviewService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j; // 💡 로그 사용을 위한 import
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j // 로그 기능 활성화
@RestController
@RequestMapping("/api/reviews") // 공통 경로를 클래스 레벨로 이동
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    // 1. 리뷰 목록 조회 (GET)
    // 주소: GET /api/reviews
    @GetMapping
    public ResponseEntity<List<ReviewResponseDto>> getReviews() {

        // 1. [로그] API 호출 기록
        log.info("API 호출: GET /api/reviews - 리뷰 전체 목록 조회 시작.");

        // 2. Service 계층에서 데이터 조회
        List<ReviewResponseDto> reviews = reviewService.getAllReviews();

        // 3. [로그] 응답 기록
        log.info("API 응답: 총 {}개의 리뷰 데이터 반환 완료. (HTTP 200 OK)", reviews.size());

        // 4. 조회된 목록을 HTTP 200 OK 상태 코드와 함께 반환
        return ResponseEntity.ok(reviews);
    }

    // 2. 리뷰 작성 (POST)
    // 주소: POST /api/reviews
    @PostMapping
    public ResponseEntity<ReviewResponseDto> createReview(@RequestBody ReviewRequest request) {

        // 1. [로그] API 호출 및 입력 데이터 기록
        log.info("API 호출: POST /api/reviews - 리뷰 작성 요청 시작. Course ID: {}", request.getCourseId());

        // 2. Service 계층에서 리뷰 생성 및 DB 저장
        ReviewResponseDto newReview = reviewService.createReview(request);

        // 3. [로그] 응답 기록
        log.info("API 응답: 리뷰 저장을 성공했습니다. Review ID: {} (HTTP 201 Created)", newReview.getId());

        // 4. 생성된 객체를 HTTP 201 Created 상태 코드와 함께 반환
        return ResponseEntity.status(HttpStatus.CREATED).body(newReview);
    }

    // 3. 리뷰 좋아요 토글 (POST)
    // 주소: POST /api/reviews/{reviewId}/like
    @PostMapping("/{reviewId}/like")
    public ResponseEntity<String> toggleLike(@PathVariable Long reviewId,
            @RequestBody chatting.dto.ReviewLikeRequest request) {
        log.info("API 호출: POST /api/reviews/{}/like - 좋아요 토글 요청. User ID: {}", reviewId, request.getUserId());
        boolean liked = reviewService.toggleLike(reviewId, request.getUserId());
        return ResponseEntity.ok(liked ? "좋아요가 추가되었습니다." : "좋아요가 취소되었습니다.");
    }

    // 4. 댓글 작성 (POST)
    // 주소: POST /api/reviews/{reviewId}/comments
    @PostMapping("/{reviewId}/comments")
    public ResponseEntity<chatting.dto.ReviewCommentResponseDto> addComment(@PathVariable Long reviewId,
            @RequestBody chatting.dto.ReviewCommentRequest request) {
        log.info("API 호출: POST /api/reviews/{}/comments - 댓글 작성 요청. User ID: {}", reviewId, request.getUserId());
        chatting.dto.ReviewCommentResponseDto comment = reviewService.addComment(reviewId, request.getUserId(),
                request.getContent());
        return ResponseEntity.status(HttpStatus.CREATED).body(comment);
    }

    // 5. 댓글 조회 (GET)
    // 주소: GET /api/reviews/{reviewId}/comments
    @GetMapping("/{reviewId}/comments")
    public ResponseEntity<List<chatting.dto.ReviewCommentResponseDto>> getComments(@PathVariable Long reviewId) {
        log.info("API 호출: GET /api/reviews/{}/comments - 댓글 조회 요청.", reviewId);
        List<chatting.dto.ReviewCommentResponseDto> comments = reviewService.getComments(reviewId);
        return ResponseEntity.ok(comments);
    }

    // 6. 리뷰 삭제 (DELETE)
    // 주소: DELETE /api/reviews/{reviewId}?userId={userId}
    @DeleteMapping("/{reviewId}")
    public ResponseEntity<String> deleteReview(@PathVariable Long reviewId, @RequestParam Long userId) {
        log.info("API 호출: DELETE /api/reviews/{} - 리뷰 삭제 요청. User ID: {}", reviewId, userId);
        reviewService.deleteReview(reviewId, userId);
        return ResponseEntity.ok("리뷰가 삭제되었습니다.");
    }
}