package chatting.service;

import chatting.dto.BadgeDto;
import chatting.repository.BadgeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class BadgeService {

    private final BadgeRepository badgeRepository;
    private final chatting.repository.UserBadgeRepository userBadgeRepository;
    private final chatting.repository.ReviewRepository reviewRepository;
    private final chatting.repository.UserCourseRepository userCourseRepository;
    private final chatting.repository.UserRepository userRepository;

    // 1. 모든 배지 조회
    @Transactional(readOnly = true)
    public List<BadgeDto> getAllBadges() {
        return badgeRepository.findAll().stream()
                .map(BadgeDto::new)
                .sorted(Comparator.comparing(BadgeDto::getId))
                .collect(Collectors.toList());
    }

    // 2. 유저가 획득한 배지 조회
    @Transactional(readOnly = true)
    public List<BadgeDto> getUserBadges(Long userId) {
        return userBadgeRepository.findByUserId(userId).stream()
                .map(userBadge -> new BadgeDto(userBadge.getBadge()))
                .sorted(Comparator.comparing(BadgeDto::getId))
                .collect(Collectors.toList());
    }

    // 3. 유저 도전과제 현황 조회 (로버스트: 이름 기반 매핑)
    @Transactional(readOnly = true)
    public List<chatting.dto.ChallengeDto> getUserChallenges(Long userId) {
        chatting.domain.User user = userRepository.findById(userId).orElse(null);
        if (user == null)
            return new java.util.ArrayList<>();

        // 통계 데이터 조회
        long completedCourseCount = userCourseRepository.findByUserId(userId).size();
        long reviewCount = reviewRepository.countByUserId(userId);
        double totalDistance = (user.getTotalDistance() != null) ? user.getTotalDistance() : 0.0;

        // DB 뱃지 정보 로드 (이름으로 매핑)
        Map<String, chatting.domain.Badge> badgeMap = badgeRepository.findAll().stream()
                .collect(Collectors.toMap(chatting.domain.Badge::getName, b -> b));

        List<chatting.dto.ChallengeDto> challenges = new java.util.ArrayList<>();

        // [1] 첫 걸음
        addChallenge(challenges, badgeMap.get("첫 걸음"), "첫 번째 코스를 완주하세요", 1, completedCourseCount, "courses");

        // [2] 갈맷길 마니아
        addChallenge(challenges, badgeMap.get("갈맷길 마니아"), "5개의 코스를 완주하세요", 5, completedCourseCount, "courses");

        // [3] 리뷰어
        addChallenge(challenges, badgeMap.get("리뷰어"), "첫 번째 리뷰를 작성하세요", 1, reviewCount, "reviews");

        // [4] 장거리 트래커
        addChallenge(challenges, badgeMap.get("장거리 트래커"), "총 50km 이상 걸어보세요", 50, totalDistance, "distance");

        return challenges;
    }

    // 도전과제 DTO 생성 헬퍼
    private void addChallenge(List<chatting.dto.ChallengeDto> list, chatting.domain.Badge badge,
            String description, int target, double current, String category) {
        if (badge != null) {
            list.add(chatting.dto.ChallengeDto.builder()
                    .id(badge.getId())
                    .title(badge.getName())
                    .description(description)
                    .target(target)
                    .current(Math.min(current, target))
                    .reward(badge.getName() + " 뱃지")
                    .category(category)
                    .completed(current >= target)
                    .build());
        }
    }

    // -----------------------------------------------------------
    // 뱃지 지급 체크 로직 (이름 기반 조회)
    // -----------------------------------------------------------

    // A. 리뷰 작성 시 체크
    @Transactional
    public void checkReviewBadges(chatting.domain.User user) {
        long reviewCount = reviewRepository.countByUser(user);
        if (reviewCount >= 1) {
            awardBadgeByName(user, "리뷰어");
        }
    }

    // B. 코스 완주 시 체크
    @Transactional
    public void checkCourseBadges(chatting.domain.User user) {
        long courseCount = userCourseRepository.findByUserId(user.getId()).size();

        if (courseCount >= 1) {
            awardBadgeByName(user, "첫 걸음");
        }
        if (courseCount >= 5) {
            awardBadgeByName(user, "갈맷길 마니아");
        }
    }

    // C. 거리 업데이트 시 체크
    @Transactional
    public void checkDistanceBadges(chatting.domain.User user) {
        if (user.getTotalDistance() != null && user.getTotalDistance() >= 50.0) {
            awardBadgeByName(user, "장거리 트래커");
        }
    }

    // 이름으로 뱃지 찾아서 지급
    private void awardBadgeByName(chatting.domain.User user, String badgeName) {
        // 이름으로 뱃지 찾기 (동적 탐색)
        chatting.domain.Badge badge = badgeRepository.findAll().stream()
                .filter(b -> b.getName().equals(badgeName))
                .findFirst()
                .orElse(null);

        if (badge != null && !userBadgeRepository.existsByUserAndBadge(user, badge)) {
            userBadgeRepository.save(chatting.domain.UserBadge.builder()
                    .user(user)
                    .badge(badge)
                    .build());
            log.info("BadgeAward: User(ID:{})에게 '{}' 뱃지 지급 완료!", user.getId(), badge.getName());
        }
    }

}