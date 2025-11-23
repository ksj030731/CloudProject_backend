package chatting.repository;

import chatting.domain.CourseCompletion;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CourseCompletionRepository extends JpaRepository<CourseCompletion, Long> {
}