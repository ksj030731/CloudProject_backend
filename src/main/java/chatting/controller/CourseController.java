package chatting.controller;

import chatting.dto.CourseResponseDto;
import chatting.service.CourseService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/courses")
@RequiredArgsConstructor
public class CourseController {

    private final CourseService courseService;

    // 1. 전체 코스 목록 조회 API
    @GetMapping
    public ResponseEntity<List<CourseResponseDto>> getCourses() {
        log.info("API 호출: GET /api/courses - 전체 코스 목록 조회 시작.");
        List<CourseResponseDto> courses = courseService.getAllCourses();
        log.info("API 응답: 총 {}개의 코스 데이터 반환 완료. (HTTP 200 OK)", courses.size());
        return ResponseEntity.ok(courses);
    }

    // 2. 특정 코스 상세 조회 API
    @GetMapping("/{id}")
    public ResponseEntity<CourseResponseDto> getCourseDetail(@PathVariable Long id) {
        log.info("API 호출: GET /api/courses/{} - 특정 코스 상세 조회 시작.", id);
        CourseResponseDto courseDetail = courseService.getCourseDetail(id);
        log.info("API 응답: 코스 ID {} 상세 정보 반환 완료. (HTTP 200 OK)", id);
        return ResponseEntity.ok(courseDetail);
    }

    // 3. 섹션 인증 (QR) API
    @org.springframework.web.bind.annotation.PostMapping("/sections/{sectionId}/complete")
    public ResponseEntity<String> completeSection(
            @org.springframework.security.core.annotation.AuthenticationPrincipal chatting.config.auth.PrincipalDetails principalDetails,
            @PathVariable Long sectionId) {

        if (principalDetails == null) {
            return ResponseEntity.status(401).body("로그인이 필요합니다.");
        }

        courseService.completeSection(principalDetails.getId(), sectionId);
        return ResponseEntity.ok("섹션 인증 완료");
    }
}