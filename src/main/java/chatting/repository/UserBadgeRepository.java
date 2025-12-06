package chatting.repository;

import chatting.domain.Badge;
import chatting.domain.User;
import chatting.domain.UserBadge;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserBadgeRepository extends JpaRepository<UserBadge, Long> {
    boolean existsByUserAndBadge(User user, Badge badge);

    List<UserBadge> findByUserId(Long userId);
}
