package chatting.repository;

import chatting.domain.Review;
import chatting.domain.ReviewLike;
import chatting.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ReviewLikeRepository extends JpaRepository<ReviewLike, Long> {
    Optional<ReviewLike> findByReviewAndUser(Review review, User user);

    boolean existsByReviewAndUser(Review review, User user);

    int countByReview(Review review);

    void deleteByReview(Review review);
}
