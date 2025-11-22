package chatting.controller;

import chatting.dto.CourseResponseDto;
import chatting.service.CourseService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class CourseController {

    private final CourseService courseService;

    // 1. 전체 코스 목록 조회 API
    // 주소: http://localhost:8080/api/courses
    @GetMapping("/api/courses")
    public List<CourseResponseDto> getCourses() {
        return courseService.getAllCourses();
    }

    // 2. 특정 코스 상세 조회 API
    // 주소: http://localhost:8080/api/courses/1
    @GetMapping("/api/courses/{id}")
    public CourseResponseDto getCourseDetail(@PathVariable Long id) {
        return courseService.getCourseDetail(id);
    }
}