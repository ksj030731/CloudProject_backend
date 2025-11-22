package chatting.utils;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import javax.crypto.SecretKey;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.stream.Collectors;

@Component
public class JwtTokenProvider {

    private SecretKey key; // 서명에 사용할 키

    @Value("${jwt.secret}")
    private String secretKey; // application.yml에서 주입받을 시크릿 키 문자열

    @Value("${jwt.expiration}")
    private long expirationTime; // application.yml에서 주입받을 토큰 만료 시간 (ms)

    private static final String AUTHORITIES_KEY = "auth";

    /**
     * @PostConstruct: 빈(Bean)이 생성된 후(의존성 주입이 완료된 후) 1회 실행되는 메서드입니다.
     * application.yml에서 주입받은 secretKey 문자열을 HMAC-SHA 알고리즘에 맞는 SecretKey 객체로 변환합니다.
     */
    @PostConstruct
    protected void init() {
        this.key = Keys.hmacShaKeyFor(secretKey.getBytes());
    }

    /**
     * 인증(Authentication) 객체를 기반으로 JWT를 생성합니다.
     *
     * @param authentication Spring Security의 인증 객체 (로그인한 사용자 정보 포함)
     * @return 생성된 JWT 문자열
     */
    public String createToken(Authentication authentication) {
        // 1. 인증 객체에서 권한(Role) 목록을 문자열로 변환합니다. (e.g., "ROLE_USER,ROLE_ADMIN")
        String authorities = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));

        long now = (new Date()).getTime();
        Date validity = new Date(now + this.expirationTime);

        // 2. JWT를 빌드합니다.
        return Jwts.builder()
                .setSubject(authentication.getName()) // 3. 'subject'로 사용자의 ID(username 또는 email)를 저장
                .claim(AUTHORITIES_KEY, authorities)   // 4. 'claims'에 권한 정보를 저장
                .signWith(key, SignatureAlgorithm.HS512) // 5. 시크릿 키로 서명 (HS512 알고리즘)
                .setIssuedAt(new Date())               // 6. 토큰 발행 시간
                .setExpiration(validity)               // 7. 토큰 만료 시간
                .compact(); // 8. 문자열로 압축
    }

    /**
     * JWT 문자열에서 인증(Authentication) 객체를 추출합니다.
     * API 요청 시 헤더에 담긴 토큰을 검증할 때 사용됩니다.
     *
     * @param token 클라이언트가 보낸 JWT 문자열
     * @return Spring Security의 인증 객체
     */
    public Authentication getAuthentication(String token) {
        // 1. JWT를 파싱(해독)하여 Claims(데이터 조각)를 추출합니다.
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();

        // 2. Claims에서 권한(auth) 정보를 꺼냅니다.
        Collection<? extends GrantedAuthority> authorities =
                Arrays.stream(claims.get(AUTHORITIES_KEY).toString().split(","))
                        .map(SimpleGrantedAuthority::new)
                        .collect(Collectors.toList());

        // 3. Claims에서 subject(사용자 ID)를 꺼내 UserDetails 객체를 생성합니다.
        UserDetails principal = new User(claims.getSubject(), "", authorities);

        // 4. 인증(Authentication) 객체를 생성하여 반환합니다.
        return new UsernamePasswordAuthenticationToken(principal, token, authorities);
    }

    /**
     * JWT의 유효성을 검증합니다.
     *
     * @param token 클라이언트가 보낸 JWT 문자열
     * @return 유효하면 true, 아니면 false
     */
    public boolean validateToken(String token) {
        try {
            // 1. 토큰을 파싱해보고, 예외가 발생하지 않으면 유효한 토큰입니다.
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (io.jsonwebtoken.security.SecurityException | MalformedJwtException e) {
            // "잘못된 JWT 서명입니다."
        } catch (ExpiredJwtException e) {
            // "만료된 JWT 토큰입니다."
        } catch (UnsupportedJwtException e) {
            // "지원되지 않는 JWT 토큰입니다."
        } catch (IllegalArgumentException e) {
            // "JWT 토큰이 잘못되었습니다."
        }
        return false;
    }
}
