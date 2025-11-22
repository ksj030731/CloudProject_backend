package chatting.config.handler;

import chatting.utils.JwtTokenProvider;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
//성공적으로 소셜로그인이 완료됐을때 실행할 메서드를 Custom한 클래스
public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    private final JwtTokenProvider jwtTokenProvider;
    private final String reactAppUrl = "https://my-cloud-project2222.duckdns.org";

    public CustomAuthenticationSuccessHandler(JwtTokenProvider jwtTokenProvider) {
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request , HttpServletResponse response,
                                        Authentication authentication)throws IOException, ServletException {

        String jwtToken = jwtTokenProvider.createToken(authentication);

        boolean hasGuestRole = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(role -> role.equals("ROLE_GUEST"));

        String targetUrl;
        if (hasGuestRole) {
            // 권한이 "ROLE_GUEST" (신규 소셜 사용자)이면
            // 추가 정보 입력 페이지로 리다이렉트
            targetUrl = reactAppUrl + "/register-social?token=" + jwtToken;
        } else {
            // "ROLE_USER" (기존 사용자)이면
            // 메인 페이지로 리다이렉트
            targetUrl = reactAppUrl + "/auth/callback?token=" + jwtToken;
        }
        response.sendRedirect(targetUrl);
    }





}

