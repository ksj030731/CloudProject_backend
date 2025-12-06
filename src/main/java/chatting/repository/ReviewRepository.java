package chatting.repository;

import chatting.domain.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    List<Review> findAllByOrderByCreatedAtDesc();

    long countByUser(chatting.domain.User user); // Use full package name if import is missing, or User if imported. The
                                                 // file uses chatting.domain.User in countByUser param?
    // Checking file content again: line 10 uses chatting.domain.User.

    long countByUserId(Long userId);
}
