package chatting.config;


import chatting.config.handler.CustomAuthenticationSuccessHandler;
import chatting.service.CustomOAuth2UserService;
import chatting.service.CustomUserDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final CustomUserDetailsService customUserDetailsService;
    private final CustomOAuth2UserService customOAuth2UserService;
    private final CustomAuthenticationSuccessHandler customAuthenticationSuccessHandler;


    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        configuration.setAllowedOrigins(Arrays.asList(
                "http://localhost:5173",   // 1. 로컬 개발 (npm run dev)
                "http://localhost:4173",   // 2. 로컬 빌드 확인 (npm run preview)
                "http://49.50.128.20",     // 3. 서버 공인 IP (또는 도메인 주소)
                "https://my-cloud-project2222.duckdns.org/"   // 4. 나중에 도메인 연결하면 그것도 추가
        ));

        configuration.addAllowedMethod("*");
        configuration.addAllowedHeader("*");
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))

                .csrf(csrf -> csrf.disable())

                .authorizeHttpRequests(authz -> authz
                        // --- 👇 [수정] React 빌드 파일(루트 경로)을 명시적으로 허용 ---
                        .requestMatchers(
                                "/", "/login", "/register", "/register-social",
                                "/api/auth/**",
                                // 기존 폴더 경로
                                "/css/**", "/js/**", "/images/**",
                                "/static/**", "/vendor/**", "/assets/**",
                                // (중요) 루트 경로의 정적 파일들
                                "/*.js",
                                "/*.css",
                                "/*.ico",
                                "/*.json",
                                "/*.png"
                        ).permitAll()
                        // --- 👆 [수정] 여기까지 ---

                        // .requestMatchers("/admin/**").hasRole("ADMIN")
                        // .requestMatchers("/register-social").hasRole("GUEST")

                        .anyRequest().authenticated() // 나머지는 인증 필요
                )
                // ★ 1. 폼(Form) 로그인 설정
                .formLogin(form -> form
                        .loginPage("/login") // 커스텀 로그인 페이지
                        .loginProcessingUrl("/auth/login-proc") // (POST) 로그인 처리 URL
                        .usernameParameter("username") // (주의) ID 파라미터명 (HTML의 <input name="">)
                        .passwordParameter("password") // (주의) PW 파라미터명
                        .defaultSuccessUrl("/", true) // (일반 로그인 성공 시)
                        .failureUrl("/login?error=true") // (일반 로그인 실패 시)
                        .permitAll()
                )

                // ★ 2. OAuth2 (소셜) 로그인 설정
                .oauth2Login(oauth2 -> oauth2
                        .loginPage("/login") // (폼 로그인과 동일한 페이지 사용)

                        // (필수) 6~7단계 담당자 지정
                        .userInfoEndpoint(userInfo -> userInfo
                                .userService(customOAuth2UserService)
                        )
                        // (필수) 로그인 성공 후 리다이렉트 담당 핸들러 지정
                        .successHandler(customAuthenticationSuccessHandler)
                )

                // ★ 3. 로그아웃 설정
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/")
                        .invalidateHttpSession(true)
                        .deleteCookies("JSESSIONID")
                )
                 .userDetailsService(customUserDetailsService);

                    return http.build();

    }
}
