package chatting.controller;

import chatting.config.auth.PrincipalDetails;
import chatting.domain.User;
import chatting.dto.CourseResponseDto;
import chatting.service.CourseService;
import chatting.service.FavoriteService;
import chatting.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j; // 💡 로그 사용을 위한 import
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.nio.file.attribute.UserPrincipal;
import java.util.List;

@Slf4j // 로그 기능 활성화
@RestController
@RequestMapping("/api/courses") // 공통 경로를 클래스 레벨로 이동 (코드 간결화)
@RequiredArgsConstructor
public class CourseController {

    private final CourseService courseService;
    private final FavoriteService favoriteService;
    // 1. 전체 코스 목록 조회 API
    // 주소: GET /api/courses
    @GetMapping
    public ResponseEntity<List<CourseResponseDto>> getCourses() {

        // 1. [로그] API 호출 기록
        log.info("API 호출: GET /api/courses - 전체 코스 목록 조회 시작.");

        // 2. Service 계층에서 데이터 조회
        List<CourseResponseDto> courses = courseService.getAllCourses();

        // 3. [로그] 응답 기록
        log.info("API 응답: 총 {}개의 코스 데이터 반환 완료. (HTTP 200 OK)", courses.size());

        // 4. 조회된 목록을 HTTP 200 OK 상태 코드와 함께 반환
        return ResponseEntity.ok(courses);
    }

    // 2. 특정 코스 상세 조회 API
    // 주소: GET /api/courses/{id}
    @GetMapping("/{id}")
    public ResponseEntity<CourseResponseDto> getCourseDetail(@PathVariable Long id) {

        // 1. [로그] API 호출 기록 (PathVariable 포함)
        log.info("API 호출: GET /api/courses/{} - 특정 코스 상세 조회 시작.", id);

        // 2. Service 계층에서 데이터 조회 (Service에서 예외 처리 담당)
        CourseResponseDto courseDetail = courseService.getCourseDetail(id);

        // 3. [로그] 응답 기록
        log.info("API 응답: 코스 ID {} 상세 정보 반환 완료. (HTTP 200 OK)", id);

        // 4. 조회된 상세 정보를 HTTP 200 OK 상태 코드와 함께 반환
        return ResponseEntity.ok(courseDetail);
    }

    @PostMapping("/{courseId}/favorite")
    public ResponseEntity<String> toggleFavorite(
            @PathVariable Long courseId,
            @AuthenticationPrincipal PrincipalDetails principalDetails // 스프링 시큐리티가 주입해줌
    ) {
        if (principalDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인이 필요합니다.");
        }

        String result = favoriteService.toggleFavorite(principalDetails.getUsername(), courseId);
        return ResponseEntity.ok(result);
    }
}