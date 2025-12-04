package chatting.service;

import chatting.dto.BadgeDto;
import chatting.repository.BadgeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class BadgeService {

    private final BadgeRepository badgeRepository;
    private final chatting.repository.UserBadgeRepository userBadgeRepository;
    private final chatting.repository.ReviewRepository reviewRepository;

    @Transactional(readOnly = true)
    public List<BadgeDto> getAllBadges() {
        log.info("DB: 모든 배지 목록 조회를 시작합니다.");
        List<BadgeDto> badgeList = badgeRepository.findAll().stream()
                .map(BadgeDto::new)
                .collect(Collectors.toList());
        log.info("DB: 모든 배지 목록 조회를 성공했습니다. 총 {}개 조회됨.", badgeList.size());
        return badgeList;
    }

    @Transactional
    public void checkReviewBadges(chatting.domain.User user) {
        // 1. 유저의 리뷰 개수 확인
        long reviewCount = reviewRepository.countByUser(user);
        log.info("BadgeCheck: User {}의 리뷰 개수: {}", user.getNickname(), reviewCount);

        // 2. 조건 달성 여부 확인 (10개 이상)
        if (reviewCount >= 10) {
            // 3. 뱃지 ID 2번 (리뷰 10개 달성) 조회
            // TODO: 실제 DB의 뱃지 ID와 일치시켜야 함. 현재는 2번으로 가정.
            chatting.domain.Badge badge = badgeRepository.findById(2L).orElse(null);

            if (badge != null) {
                // 4. 이미 보유 중인지 확인
                if (!userBadgeRepository.existsByUserAndBadge(user, badge)) {
                    // 5. 뱃지 지급
                    userBadgeRepository.save(chatting.domain.UserBadge.builder()
                            .user(user)
                            .badge(badge)
                            .build());
                    log.info("BadgeAward: User {}에게 '{}' 뱃지 지급 완료!", user.getNickname(), badge.getName());
                }
            }
        }
    }
}