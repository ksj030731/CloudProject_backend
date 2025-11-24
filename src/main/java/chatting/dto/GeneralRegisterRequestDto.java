package chatting.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class GeneralRegisterRequestDto {
    private String username; // 아이디
    private String password;
    private String email;
    private String nickname;
    private String region;
}