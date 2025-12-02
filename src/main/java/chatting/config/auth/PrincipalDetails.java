package chatting.config.auth;


import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.io.Serializable;
import java.util.ArrayList;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Map;

@Getter

//UserDetails(폼 로그인)와 Oauth2User(소셜 로그인)을 모두 구현하는 통일 인증 객체
public class PrincipalDetails implements UserDetails, OAuth2User , Serializable {

    private static final long serialVersionUID = 1L;
    // (User 엔티티 대신) DB에서 가져온 '데이터'를 직접 필드로 가짐
    private Long id;
    private String username;
    private String password; // (null일 수 있음)
    private String email;
    private String role;
    private String provider;
    private Map<String, Object> attributes; // (OAuth2User)
    private String region;

    private LocalDateTime createDate;
    private Double totalDistance;
    // 생성자 1: 일반 로그인 (UserDetails)
    public PrincipalDetails(Long id, String username, String password, String email, String role, String provider , String region , LocalDateTime createDate, Double totalDistance) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.email = email;
        this.role = role;
        this.provider = provider;
        this.attributes = null; // 일반 로그인은 attributes 없음
        this.region = region;
        this.createDate = createDate;
        this.totalDistance = totalDistance;
    }

    // 생성자 2: OAuth 로그인 (OAuth2User)
    public PrincipalDetails(Long id, String username, String email, String role, String provider, Map<String, Object> attributes  ,String region , LocalDateTime createDate, Double totalDistance) {
        this.id = id;
        this.username = username;
        this.password = null; // OAuth 로그인은 password 없음
        this.email = email;
        this.role = role;
        this.provider = provider;
        this.attributes = attributes;
        this.region =region;
        this.createDate = createDate;
        this.totalDistance = totalDistance;
    }


    // === OAuth2User 인터페이스 ===
    @Override
    public Map<String, Object> getAttributes() {
        return attributes;
    }

    @Override
    public String getName() {
        // (application.properties의 'user-name-attribute' 값)
        return this.username;
    }

    // === UserDetails 인터페이스 ===
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Collection<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(() -> this.role); // "ROLE_USER" 또는 "ROLE_GUEST"
        return authorities;
    }

    @Override
    public String getPassword() {
        return this.password; // (폼 로그인 시 사용)
    }

    @Override
    public String getUsername() {
        return this.username; // (폼 로그인 시 ID)
    }

    // (계정 상태 - 모두 true)
    @Override public boolean isAccountNonExpired() { return true; }
    @Override public boolean isAccountNonLocked() { return true; }
    @Override public boolean isCredentialsNonExpired() { return true; }
    @Override public boolean isEnabled() { return true; }
}
