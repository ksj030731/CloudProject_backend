package chatting.dto;

import chatting.domain.ReviewComment;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ReviewCommentResponseDto {
    private Long id;
    private Long reviewId;
    private Long userId;
    private String userName;
    private String content;
    private String date;

    public ReviewCommentResponseDto(ReviewComment comment) {
        this.id = comment.getId();
        this.reviewId = comment.getReview().getId();
        this.userId = comment.getUser().getId();
        this.userName = comment.getUser().getNickname();
        this.content = comment.getContent();
        this.date = comment.getCreatedAt().toString();
    }
}
