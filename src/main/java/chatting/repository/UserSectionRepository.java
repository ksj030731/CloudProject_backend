package chatting.repository;

import chatting.domain.Section;
import chatting.domain.User;
import chatting.domain.UserSection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UserSectionRepository extends JpaRepository<UserSection, Long> {
    boolean existsByUserAndSection(User user, Section section);

    @Query("SELECT COUNT(us) FROM UserSection us WHERE us.user = :user AND us.section.course.id = :courseId")
    long countByUserAndCourseId(@Param("user") User user, @Param("courseId") Long courseId);
}
