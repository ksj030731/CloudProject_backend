package chatting.service;

import chatting.domain.Course;
import chatting.dto.CourseResponseDto;
import chatting.repository.CourseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CourseService {

    private final CourseRepository courseRepository;

    // 모든 코스 조회
    @Transactional(readOnly = true)
    public List<CourseResponseDto> getAllCourses() {
        List<Course> courses = courseRepository.findAll();

        // DB에서 가져온 Course 목록을 -> DTO 목록으로 변환해서 반환
        return courses.stream()
                .map(CourseResponseDto::new)
                .collect(Collectors.toList());
    }

    // 특정 코스 조회 (상세 페이지용)
    @Transactional(readOnly = true)
    public CourseResponseDto getCourseDetail(Long id) {
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 코스가 없습니다. id=" + id));
        return new CourseResponseDto(course);
    }
}