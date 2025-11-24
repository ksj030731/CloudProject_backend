package chatting.service;

import chatting.dto.BadgeDto;
import chatting.repository.BadgeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j; // 💡 로그 사용을 위한 import
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j // Lombok을 사용하여 로그 기능 활성화
@Service
@RequiredArgsConstructor
public class BadgeService {

    private final BadgeRepository badgeRepository;

    @Transactional(readOnly = true)
    public List<BadgeDto> getAllBadges() {

        // 1. [로그] DB 조회 시작
        log.info("DB: 모든 배지 목록 조회를 시작합니다.");

        List<BadgeDto> badgeList = badgeRepository.findAll().stream()
                .map(BadgeDto::new)
                .collect(Collectors.toList());

        // 2. [로그] 조회 성공 및 결과 개수 출력
        log.info("DB: 모든 배지 목록 조회를 성공했습니다. 총 {}개 조회됨.", badgeList.size());

        return badgeList;
    }
}