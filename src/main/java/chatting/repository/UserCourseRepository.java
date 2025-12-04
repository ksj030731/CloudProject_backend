package chatting.repository;

import chatting.domain.Course;
import chatting.domain.User;
import chatting.domain.UserCourse;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserCourseRepository extends JpaRepository<UserCourse, Long> {
    boolean existsByUserAndCourse(User user, Course course);
}
