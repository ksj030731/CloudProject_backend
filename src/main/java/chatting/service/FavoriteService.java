package chatting.service;

import chatting.domain.Course;
import chatting.domain.Favorite;
import chatting.domain.User;
import chatting.repository.CourseRepository;
import chatting.repository.FavoriteRepository;
import lombok.extern.slf4j.Slf4j;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class FavoriteService {
    private final FavoriteRepository favoriteRepository;
    private final CourseRepository courseRepository; // 코스 정보도 필요하니까
    private final UserService userService; // 위에서 만든 서비스 주입

    public String toggleFavorite(String username, Long courseId) {
        //  요청이 들어왔음을 확인
        log.info("🔄 찜하기 토글 요청 진입 - 사용자: {}, 코스ID: {}", username, courseId);

        // 1. 안전하게 유저와 코스 찾기
        User user = userService.findByUsername(username);

        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> {
                    // [로그 2] 에러 발생 직전 로그
                    log.error("❌ 찜하기 실패: 존재하지 않는 코스입니다. ID: {}", courseId);
                    return new IllegalArgumentException("코스가 없습니다.");
                });

        // 2. 이미 찜했는지 확인 (Toggle 로직)
        boolean isExists = favoriteRepository.existsByUserAndCourse(user, course);
        log.info("🔎 찜 여부 확인 결과: {}", isExists ? "이미 찜함(삭제 예정)" : "찜 안함(추가 예정)");

        if (isExists) {
            favoriteRepository.deleteByUserAndCourse(user, course);
            log.info("🗑️ 찜 취소 완료 - User: {}, CourseId: {}", username, courseId);
            return "찜 취소";
        } else {
            // 주의: Favorite 엔티티에 생성자가 있어야 함
            Favorite favorite = new Favorite(user, course);
            favoriteRepository.save(favorite);
            log.info("❤️ 찜 추가 완료 - User: {}, CourseId: {}", username, courseId);
            return "찜 하기";
        }
    }


    /**
     * 유저 ID를 통해 해당 유저가 찜한 코스 ID를 List로 반환하는 메서드
     */
    public List<Long> getFavoriteCourseIds(Long userId) {

        // [로그 1] 메서드 진입 및 요청 파라미터 확인
        log.info("🔍 찜 목록 조회 요청 시작 - User ID: {}", userId);

        List<Favorite> favorites = favoriteRepository.findAllByUserId(userId);

        // [로그 2] DB 조회 결과 (Null 체크 및 개수 확인)
        if (favorites == null || favorites.isEmpty()) {
            log.info("📭 찜한 코스가 없습니다. - User ID: {}", userId);
            return new ArrayList<>(); // 빈 리스트 반환
        }

        // Favorite 객체 리스트 -> Course ID 숫자 리스트로 변환
        List<Long> courseIds = favorites.stream()
                .map(favorite -> favorite.getCourse().getId())
                .collect(Collectors.toList());

        // [로그 3] 최종 반환값 확인 (변환된 ID 목록)
        log.info("✅ 찜 목록 조회 완료 - User ID: {}, 개수: {}개, 목록: {}", userId, courseIds.size(), courseIds);

        return courseIds;
    }
}