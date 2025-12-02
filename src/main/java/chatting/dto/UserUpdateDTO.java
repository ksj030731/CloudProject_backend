package chatting.dto;

import lombok.Getter;

@Getter
public class UserUpdateDTO {

    private String nickname;
    private String region;

    public UserUpdateDTO(String nickname, String region) {
        this.nickname = nickname;
        this.region = region;
    }
}
