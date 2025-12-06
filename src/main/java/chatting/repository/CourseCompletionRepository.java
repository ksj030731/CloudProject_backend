package chatting.repository;

import chatting.domain.CourseCompletion;
import org.springframework.data.jpa.repository.JpaRepository;

import chatting.domain.Course;
import chatting.domain.User;
import java.util.Optional;

public interface CourseCompletionRepository extends JpaRepository<CourseCompletion, Long> {
    Optional<CourseCompletion> findByUserAndCourse(User user, Course course);
}