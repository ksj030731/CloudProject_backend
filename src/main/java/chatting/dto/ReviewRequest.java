package chatting.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

@Getter
@Setter
@NoArgsConstructor
public class ReviewRequest {
    // 프론트엔드에서 숫자로 보내므로 Long 사용 (String -> Long 자동 변환도 되지만 명시적인게 좋음)
    private Long userId;
    private Long courseId;
    private Byte rating;
    private String content;
}