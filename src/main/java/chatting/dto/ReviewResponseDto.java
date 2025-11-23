package chatting.dto;

import chatting.domain.Review;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Collections;
import java.util.List;

@Getter
@NoArgsConstructor
public class ReviewResponseDto {
    // 프론트엔드 interface Review와 필드명을 일치시킵니다.
    private Long id;
    private Long courseId;
    private Long userId;
    private String userName; // 백엔드 User 엔티티의 nickname을 여기에 담음
    private Byte rating;
    private String content;
    private List<String> photos; // 현재 DB에 없으면 빈 리스트 반환
    private String date; // createdAt을 문자열로 변환
    private Integer likes; // 현재 DB에 없으면 0 반환

    public ReviewResponseDto(Review review) {
        this.id = review.getId();

        // 연관관계(Entity)에서 ID만 추출
        if (review.getCourse() != null) {
            this.courseId = review.getCourse().getId();
        }

        // User 객체에서 필요한 정보 추출 (Flattening)
        if (review.getUser() != null) {
            this.userId = review.getUser().getId();
            this.userName = review.getUser().getNickname(); // 닉네임을 userName으로 매핑
        }

        this.rating = review.getRating();
        this.content = review.getContent();

        // 날짜 포맷 변환 (LocalDateTime -> String)
        this.date = review.getCreatedAt() != null ? review.getCreatedAt().toString() : "";

        // 임시 데이터 (DB에 해당 컬럼이 생기기 전까지 기본값 제공)
        this.photos = Collections.emptyList();
        this.likes = 0;
    }
}