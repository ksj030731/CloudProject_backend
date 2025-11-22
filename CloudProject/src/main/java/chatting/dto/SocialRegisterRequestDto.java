package chatting.dto;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
//소셜 로그인에서 추가가입용
public class SocialRegisterRequestDto {
    // 폼에서 받을 추가 데이터
    private String nickname;
    private String region;
}
