package chatting.service;

import chatting.domain.Course;
import chatting.domain.Review;
import chatting.domain.User;
import chatting.dto.ReviewRequest;
import chatting.dto.ReviewResponseDto;
import chatting.repository.CourseRepository;
import chatting.repository.ReviewRepository;
import chatting.repository.UserRepository; // User 레포지토리 필요
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final CourseRepository courseRepository;
    private final UserRepository userRepository; // 유저 조회를 위해 추가

    // 모든 리뷰 가져오기
    @Transactional(readOnly = true)
    public List<ReviewResponseDto> getAllReviews() {
        return reviewRepository.findAll().stream()
                .map(ReviewResponseDto::new)
                .collect(Collectors.toList());
    }

    // 리뷰 저장하기
    @Transactional
    public ReviewResponseDto createReview(ReviewRequest request) {
        // 1. 작성자(User) 확인
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("유저를 찾을 수 없습니다. id=" + request.getUserId()));

        // 2. 코스(Course) 확인
        Course course = courseRepository.findById(request.getCourseId())
                .orElseThrow(() -> new IllegalArgumentException("코스를 찾을 수 없습니다. id=" + request.getCourseId()));

        // 3. 리뷰 엔티티 생성
        Review review = Review.builder()
                .user(user)
                .course(course)
                .rating(request.getRating())
                .content(request.getContent())
                .build();

        // 4. DB 저장
        Review savedReview = reviewRepository.save(review);

        // 5. 결과 반환 (DTO로 변환)
        return new ReviewResponseDto(savedReview);
    }
}