package chatting.controller;

import chatting.dto.AnnouncementDto;
import chatting.dto.BadgeDto;
import chatting.service.AnnouncementService;
import chatting.service.BadgeService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class CommonController {

    private final AnnouncementService announcementService;
    private final BadgeService badgeService;

    // 공지사항 목록
    @GetMapping("/api/announcements")
    public List<AnnouncementDto> getAnnouncements() {
        return announcementService.getAllAnnouncements();
    }

    // 뱃지 목록
    @GetMapping("/api/badges")
    public List<BadgeDto> getBadges() {
        return badgeService.getAllBadges();
    }
}