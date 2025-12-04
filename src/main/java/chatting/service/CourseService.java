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

@Slf4j
@Service
@RequiredArgsConstructor
public class CourseService {

    private final CourseRepository courseRepository;
    private final chatting.repository.SectionRepository sectionRepository;
    private final chatting.repository.UserSectionRepository userSectionRepository;
    private final chatting.repository.UserCourseRepository userCourseRepository;
    private final chatting.repository.UserRepository userRepository;

    // 모든 코스 조회
    @Transactional(readOnly = true)
    public List<CourseResponseDto> getAllCourses() {
        log.info("DB: 모든 코스 데이터 조회를 시작합니다.");
        List<Course> courses = courseRepository.findAll();
        log.info("DB: 모든 코스 데이터 조회를 성공했습니다. 총 {}개 조회됨.", courses.size());
        return courses.stream()
                .map(CourseResponseDto::new)
                .collect(Collectors.toList());
    }

    // 특정 코스 조회 (상세 페이지용)
    @Transactional(readOnly = true)
    public CourseResponseDto getCourseDetail(Long id) {
        log.info("DB: 특정 코스 조회를 시도합니다. Course ID: {}", id);
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("DB: 코스 조회 실패. ID {}에 해당하는 데이터가 없습니다.", id);
                    return new IllegalArgumentException("해당 코스가 없습니다. id=" + id);
                });
        log.info("DB: 코스 조회 성공. Course ID {} 데이터를 가져왔습니다.", course.getId());
        return new CourseResponseDto(course);
    }

    // 섹션 완료 처리 (QR 인증)
    @Transactional
    public void completeSection(Long userId, Long sectionId) {
        // 1. 유저 및 섹션 조회
        chatting.domain.User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("유저를 찾을 수 없습니다."));
        chatting.domain.Section section = sectionRepository.findById(sectionId)
                .orElseThrow(() -> new IllegalArgumentException("섹션을 찾을 수 없습니다."));

        // 2. 이미 완료한 섹션인지 확인
        if (userSectionRepository.existsByUserAndSection(user, section)) {
            log.info("이미 완료한 섹션입니다: User {}, Section {}", user.getNickname(), section.getName());
            return;
        }

        // 3. UserSection 저장 (섹션 완료 처리)
        userSectionRepository.save(chatting.domain.UserSection.builder()
                .user(user)
                .section(section)
                .build());

        // 4. 유저 총 거리 증가
        // 섹션 거리가 null이면 0으로 처리
        double distanceToAdd = section.getDistance() != null ? section.getDistance() : 0.0;
        user.addDistance(distanceToAdd);
        log.info("User {} - 섹션 {} 완료! 거리 {}km 추가됨.", user.getNickname(), section.getName(), distanceToAdd);

        // 5. 코스 전체 완료 여부 확인
        chatting.domain.Course course = section.getCourse();
        long completedSectionsCount = userSectionRepository.countByUserAndCourseId(user, course.getId());
        int totalSectionsCount = course.getSections().size();

        log.info("코스 진행률: {} / {}", completedSectionsCount, totalSectionsCount);

        if (completedSectionsCount >= totalSectionsCount) {
            // 6. 코스 완료 처리 (UserCourse 저장)
            if (!userCourseRepository.existsByUserAndCourse(user, course)) {
                userCourseRepository.save(chatting.domain.UserCourse.builder()
                        .user(user)
                        .course(course)
                        .build());
                log.info("축하합니다! User {}님이 코스 '{}'를 완주하셨습니다!", user.getNickname(), course.getName());
            }
        }
    }
}