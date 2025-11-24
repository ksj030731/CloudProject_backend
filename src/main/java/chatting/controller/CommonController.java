package chatting.controller;

import chatting.dto.AnnouncementDto;
import chatting.dto.BadgeDto;
import chatting.service.AnnouncementService;
import chatting.service.BadgeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api") // /api를 공통 경로로 사용
@RequiredArgsConstructor
public class CommonController {

    private final AnnouncementService announcementService;
    private final BadgeService badgeService;

    // [GET] /api/announcements
    @GetMapping("/announcements")
    public ResponseEntity<List<AnnouncementDto>> getAnnouncements() {
        log.info("API 호출: GET /api/announcements - 공지사항 목록 조회 시작.");
        List<AnnouncementDto> announcements = announcementService.getAllAnnouncements();
        log.info("API 응답: 총 {}개의 공지사항 데이터 반환 완료.", announcements.size());
        return ResponseEntity.ok(announcements);
    }

    // [GET] /api/badges
    @GetMapping("/badges")
    public ResponseEntity<List<BadgeDto>> getBadges() {
        log.info("API 호출: GET /api/badges - 뱃지 목록 조회 시작.");
        List<BadgeDto> badges = badgeService.getAllBadges();
        log.info("API 응답: 총 {}개의 뱃지 데이터 반환 완료.", badges.size());
        return ResponseEntity.ok(badges);
    }
}