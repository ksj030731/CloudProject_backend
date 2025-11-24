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
import lombok.extern.slf4j.Slf4j; // 💡 로그 사용을 위한 import
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j // Lombok을 사용하여 로그 기능 활성화
@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final CourseRepository courseRepository;
    private final UserRepository userRepository;

    // 모든 리뷰 가져오기
    @Transactional(readOnly = true)
    public List<ReviewResponseDto> getAllReviews() {
        // 1. [로그] 전체 리뷰 조회 시작
        log.info("DB: 모든 리뷰 데이터 조회를 시작합니다.");

        List<ReviewResponseDto> reviews = reviewRepository.findAll().stream()
                .map(ReviewResponseDto::new)
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
                    return new IllegalArgumentException("코스를 찾을 수 없습니다. id=" + request.getCourseId());
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

        // 7. 결과 반환 (DTO로 변환)
        return new ReviewResponseDto(savedReview);
    }
}