package chatting.repository;

import chatting.domain.Review;
import chatting.domain.ReviewComment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReviewCommentRepository extends JpaRepository<ReviewComment, Long> {
    List<ReviewComment> findByReviewOrderByCreatedAtAsc(Review review);

    int countByReview(Review review);

    void deleteByReview(Review review);
}
