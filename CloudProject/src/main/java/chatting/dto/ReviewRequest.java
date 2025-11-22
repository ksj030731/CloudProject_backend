package chatting.dto;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ReviewRequest {
    private String userId;
    private String courseId;
    private Byte rating;
    private String content;


}
