package chatting.service;

import chatting.domain.Course;
import chatting.domain.Review;
import chatting.domain.User;
import chatting.dto.ReviewRequest;
import chatting.dto.ReviewResponseDto;
import chatting.repository.CourseRepository;
import chatting.repository.ReviewRepository;
import chatting.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReviewService {

        private final ReviewRepository reviewRepository;
        private final CourseRepository courseRepository;
        private final UserRepository userRepository;
        private final chatting.repository.ReviewLikeRepository reviewLikeRepository;
        private final chatting.repository.ReviewCommentRepository reviewCommentRepository;
        private final BadgeService badgeService;

        // 모든 리뷰 가져오기
        @Transactional(readOnly = true)
        public List<ReviewResponseDto> getAllReviews() {
                // 1. [로그] 전체 리뷰 조회 시작
                log.info("DB: 모든 리뷰 데이터 조회를 시작합니다. (최신순 정렬)");

                List<ReviewResponseDto> reviews = reviewRepository.findAllByOrderByCreatedAtDesc().stream()
                                .map(review -> {
                                        ReviewResponseDto dto = new ReviewResponseDto(review);
                                        dto.setLikes(reviewLikeRepository.countByReview(review));
                                        dto.setCommentCount(reviewCommentRepository.countByReview(review));
                                        return dto;
                                })
                                .collect(Collectors.toList());

                // 2. [로그] 조회 성공 및 개수 출력
                log.info("DB: 총 {}개의 리뷰 데이터를 성공적으로 조회했습니다.", reviews.size());

                return reviews;
        }

        // 리뷰 저장하기
        @Transactional
        public ReviewResponseDto createReview(ReviewRequest request) {

                // 1. [로그] 리뷰 생성 요청 정보 출력
                log.info("리뷰 생성 요청: User ID: {}, Course ID: {}, 평점: {}",
                                request.getUserId(), request.getCourseId(), request.getRating());

                // 2. 작성자(User) 확인
                User user = userRepository.findById(request.getUserId())
                                .orElseThrow(() -> {
                                        log.error("유저 조회 실패: User ID {}를 찾을 수 없습니다.", request.getUserId());
                                        return new IllegalArgumentException("유저를 찾을 수 없습니다. id=" + request.getUserId());
                                });
                log.debug("DB: User {} 조회 성공.", user.getNickname());

                // 3. 코스(Course) 확인
                Course course = courseRepository.findById(request.getCourseId())
                                .orElseThrow(() -> {
                                        log.error("코스 조회 실패: Course ID {}를 찾을 수 없습니다.", request.getCourseId());
                                        return new IllegalArgumentException(
                                                        "코스를 찾을 수 없습니다. id=" + request.getCourseId());
                                });
                log.debug("DB: Course '{}' 조회 성공.", course.getName());

                // 4. 리뷰 엔티티 생성
                Review review = Review.builder()
                                .user(user)
                                .course(course)
                                .rating(request.getRating())
                                .content(request.getContent())
                                .build();

                // 5. DB 저장
                Review savedReview = reviewRepository.save(review);

                // 6. [로그] 저장 성공
                log.info("DB: 리뷰 저장을 성공했습니다. Review ID: {}", savedReview.getId());

                // 7. 뱃지 달성 여부 체크
                badgeService.checkReviewBadges(user);

                // 8. 결과 반환 (DTO로 변환)
                return new ReviewResponseDto(savedReview);
        }

        // 리뷰 좋아요 토글
        @Transactional
        public boolean toggleLike(Long reviewId, Long userId) {
                Review review = reviewRepository.findById(reviewId)
                                .orElseThrow(() -> new IllegalArgumentException("리뷰를 찾을 수 없습니다. id=" + reviewId));
                User user = userRepository.findById(userId)
                                .orElseThrow(() -> new IllegalArgumentException("유저를 찾을 수 없습니다. id=" + userId));

                return reviewLikeRepository.findByReviewAndUser(review, user)
                                .map(like -> {
                                        reviewLikeRepository.delete(like);
                                        return false; // 좋아요 취소됨
                                })
                                .orElseGet(() -> {
                                        reviewLikeRepository.save(chatting.domain.ReviewLike.builder()
                                                        .review(review)
                                                        .user(user)
                                                        .build());

                                        return true; // 좋아요 추가됨
                                });
        }

        // 댓글 작성
        @Transactional
        public chatting.dto.ReviewCommentResponseDto addComment(Long reviewId, Long userId, String content) {
                Review review = reviewRepository.findById(reviewId)
                                .orElseThrow(() -> new IllegalArgumentException("리뷰를 찾을 수 없습니다. id=" + reviewId));
                User user = userRepository.findById(userId)
                                .orElseThrow(() -> new IllegalArgumentException("유저를 찾을 수 없습니다. id=" + userId));

                chatting.domain.ReviewComment comment = chatting.domain.ReviewComment.builder()
                                .review(review)
                                .user(user)
                                .content(content)
                                .build();

                chatting.domain.ReviewComment savedComment = reviewCommentRepository.save(comment);
                return new chatting.dto.ReviewCommentResponseDto(savedComment);
        }

        // 댓글 조회
        @Transactional(readOnly = true)
        public List<chatting.dto.ReviewCommentResponseDto> getComments(Long reviewId) {
                Review review = reviewRepository.findById(reviewId)
                                .orElseThrow(() -> new IllegalArgumentException("리뷰를 찾을 수 없습니다. id=" + reviewId));

                return reviewCommentRepository.findByReviewOrderByCreatedAtAsc(review).stream()
                                .map(chatting.dto.ReviewCommentResponseDto::new)
                                .collect(Collectors.toList());
        }

        // 리뷰 삭제
        @Transactional
        public void deleteReview(Long reviewId, Long userId) {
                // 1. 리뷰 조회
                Review review = reviewRepository.findById(reviewId)
                                .orElseThrow(() -> new IllegalArgumentException("리뷰를 찾을 수 없습니다. id=" + reviewId));

                // 2. 작성자 본인 확인
                if (!review.getUser().getId().equals(userId)) {
                        throw new IllegalArgumentException("본인의 리뷰만 삭제할 수 있습니다.");
                }

                // 3. 연관된 데이터 삭제 (좋아요, 댓글)
                reviewLikeRepository.deleteByReview(review);
                reviewCommentRepository.deleteByReview(review);

                // 4. 리뷰 삭제
                reviewRepository.delete(review);
                log.info("DB: 리뷰 삭제 완료. Review ID: {}", reviewId);
        }
}