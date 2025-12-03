package chatting.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ReviewCommentRequest {
    private Long userId;
    private String content;
}
