package chatting.service;

import chatting.dto.AnnouncementDto;
import chatting.repository.AnnouncementRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j; // 💡 로그 사용을 위한 import
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j // Lombok을 사용하여 로그 기능 활성화
@Service
@RequiredArgsConstructor
public class AnnouncementService {
    private final AnnouncementRepository announcementRepository;

    @Transactional(readOnly = true)
    public List<AnnouncementDto> getAllAnnouncements() {

        // 1. [로그] DB 조회 시작
        log.info("DB: 모든 공지사항 목록 조회를 시작합니다.");

        List<AnnouncementDto> announcements = announcementRepository.findAll().stream()
                .map(AnnouncementDto::new)
                .collect(Collectors.toList());

        // 2. [로그] 조회 성공 및 결과 개수 출력
        log.info("DB: 공지사항 목록 조회를 성공했습니다. 총 {}개 조회됨.", announcements.size());

        return announcements;
    }
}