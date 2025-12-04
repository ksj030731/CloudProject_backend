package chatting.repository;

import chatting.domain.Course;
import chatting.domain.Favorite;
import chatting.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FavoriteRepository extends JpaRepository<Favorite,Long> {
    boolean existsByUserAndCourse(User user, Course course);
    void deleteByUserAndCourse(User user, Course course);
    List<Favorite> findAllByUserId(Long userId);
}

