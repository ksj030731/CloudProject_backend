package chatting.dto;


import chatting.config.auth.PrincipalDetails;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UserResponseDTO {

    private Long id;
    private String username ;
    private String nickname;
    private String  email;
    private String  picture;
    private String  provider;
    private String  role;
    private String  password;
    private String  region;

    public UserResponseDTO() {}

    public UserResponseDTO(Long id, String username, String nickname, String picture, String email, String provider, String role, String password, String region) {
        this.id = id;
        this.username = username;
        this.nickname = nickname;
        this.password = password;
        this.region = region;
        this.picture = picture;
        this.email = email;
        this.provider = provider;
        this.role = role;

    }

    public static UserResponseDTO from(PrincipalDetails principalDetails) {

        return UserResponseDTO.builder()
                .id(principalDetails.getId())
                .username(principalDetails.getUsername())
                .password(null)
                .email(principalDetails.getEmail())
                .nickname(principalDetails.getName())
                .region(principalDetails.getRegion() )
                .provider(principalDetails.getProvider())
                .role(principalDetails.getRole())
                .build();

    }
}
