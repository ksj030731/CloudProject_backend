package chatting.service;

import chatting.domain.Course;
import chatting.dto.CourseResponseDto;
import chatting.repository.CourseRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j // 로그 사용
@Service
@RequiredArgsConstructor
public class CourseService {

    private final CourseRepository courseRepository;

    // 모든 코스 조회
    @Transactional(readOnly = true)
    public List<CourseResponseDto> getAllCourses() {

        // 1. [로그] DB 접근 시작
        log.info("DB: 모든 코스 데이터 조회를 시작합니다.");

        List<Course> courses = courseRepository.findAll();

        // 2. [로그] DB 접근 성공 및 결과 개수 출력
        log.info("DB: 모든 코스 데이터 조회를 성공했습니다. 총 {}개 조회됨.", courses.size());

        // DTO 목록으로 변환 후 반환
        return courses.stream()
                .map(CourseResponseDto::new)
                .collect(Collectors.toList());
    }

    // 특정 코스 조회 (상세 페이지용)
    @Transactional(readOnly = true)
    public CourseResponseDto getCourseDetail(Long id) {

        // 1. [로그] 특정 ID로 DB 조회 시도
        log.info("DB: 특정 코스 조회를 시도합니다. Course ID: {}", id);

        Course course = courseRepository.findById(id)
                // 2. [예외 처리] 코스가 없으면 기본 예외(IllegalArgumentException) 발생
                .orElseThrow(() -> {
                    log.error("DB: 코스 조회 실패. ID {}에 해당하는 데이터가 없습니다.", id);
                    return new IllegalArgumentException("해당 코스가 없습니다. id=" + id);
                });

        // 3. [로그] DB 조회 성공
        log.info("DB: 코스 조회 성공. Course ID {} 데이터를 가져왔습니다.", course.getId());

        return new CourseResponseDto(course);
    }
}