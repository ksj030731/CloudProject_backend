/**
 *
 * @author 김성준
 * 2025.11.04 작성
 */
package chatting.domain;

import jakarta.persistence.*;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "users", uniqueConstraints ={
        //username과 email이 중복되지 않도록 설정
        @UniqueConstraint(columnNames = "username"),
        @UniqueConstraint(columnNames =  "email")
})
public class User {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    //(일반 로그인용 ID)
    @Column(nullable = false , length = 50)
    private String username;

    @Column( length = 50)
    private String nickname;

    @Column(nullable = false , length = 100)
    private String email;

    @Column
    private String picture;

    // "google" , "naver" , "kakao"
    @Column(nullable = false)
    private String provider;

    // "ROLE_USER", "ROLE_ADMIN" , "ROLE_GUEST" (소셜 임시)
    @Column(nullable = false)
    private String role;

    @Column
    private String password;

    //(필수) 지역정보
    @Column
    private String region;

    @Builder
    public User(String username, String nickname, String email, String picture, String provider, String role, String password, String region) {
        this.username = username;
        this.nickname = nickname;
        this.email = email;
        this.picture = picture;
        this.provider = provider;
        this.role = role;
        this.password = password;
        this.region = region;
    }

    // 소셜 로그인 시 정보 업데이트
    public User update(String username, String picture) {
        this.username = username;
        this.picture = picture;
        return this;
    }

    //소셜 로그인 후 추가 정보(닉네임 , 지역) 가입 및 등업
    public User updateSocialInfo(String nickname, String region) {
        this.nickname = nickname;
        this.region = region;
        this.role = "ROLE_USER"; // GUEST -> USER 등업
        return this;
    }
}
