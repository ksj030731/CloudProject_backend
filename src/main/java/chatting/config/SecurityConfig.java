package chatting.config;

import chatting.config.handler.CustomAuthenticationSuccessHandler;
import chatting.service.CustomOAuth2UserService;
import chatting.service.CustomUserDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
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
        public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration)
                        throws Exception {
                return authenticationConfiguration.getAuthenticationManager();
        }

        @Bean
        public BCryptPasswordEncoder bCryptPasswordEncoder() {
                return new BCryptPasswordEncoder();
        }

        @Bean
        public CorsConfigurationSource corsConfigurationSource() {
                CorsConfiguration configuration = new CorsConfiguration();

                configuration.setAllowedOrigins(Arrays.asList(
                                "http://localhost:5173", // 로컬 개발
                                "http://localhost:4173", // 로컬 빌드
                                "http://49.50.128.20", // 서버 IP
                                "https://my-cloud-project2222.duckdns.org" // 도메인 (끝에 / 제거)
                ));
                configuration.setAllowCredentials(true);

                configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS")); // 명시적 허용 추천
                configuration.setAllowedHeaders(Arrays.asList("*"));
                configuration.setAllowCredentials(true);

                UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
                source.registerCorsConfiguration("/**", configuration);
                return source;
        }

        @Bean
        public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
                http
                                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                                .csrf(AbstractHttpConfigurer::disable)

                                // 👇 [핵심 수정 1] 세션 관리 정책을 명시합니다 (IF_REQUIRED가 기본이지만 명시하는 게 안전)
                                .sessionManagement(session -> session
                                                .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED))

                                // 👇 [핵심 수정 2] SecurityContext를 세션에서 명시적으로 저장/로드하도록 설정
                                .securityContext(securityContext -> securityContext
                                                .requireExplicitSave(false) // false로 설정해야 자동 저장됨 (중요!)
                                )

                                .authorizeHttpRequests(authz -> authz
                                                .requestMatchers(
                                                                "/", "/login", "/register", "/register-social",
                                                                "/api/auth/**",
                                                                "/css/**", "/js/**", "/images/**", "/static/**",
                                                                "/vendor/**", "/assets/**",
                                                                "/*.js", "/*.css", "/*.ico", "/*.json", "/*.png",
                                                                "/index.html",
                                                                "/swagger-ui/**", "/v3/api-docs/**", "/api-test.html")
                                                .permitAll()

                                                .requestMatchers(HttpMethod.GET,
                                                                "/api/courses/**", "/api/reviews/**",
                                                                "/api/announcements/**",
                                                                "/api/badges/**", "/api/rankings/**", "/api/user/**")
                                                .permitAll()

                                                .anyRequest().authenticated())

                                // 1. 폼 로그인 비활성화
                                .formLogin(AbstractHttpConfigurer::disable)
                                .httpBasic(AbstractHttpConfigurer::disable)

                                // 🚨 [핵심 수정] 인증 예외 발생 시 로그인 페이지로 리다이렉트(302) 하지 않고 401 에러 반환
                                .exceptionHandling(exception -> exception
                                                .authenticationEntryPoint(
                                                                new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED)))

                                // 2. OAuth2 설정
                                .oauth2Login(oauth2 -> oauth2
                                                .loginPage("/login")
                                                .userInfoEndpoint(userInfo -> userInfo
                                                                .userService(customOAuth2UserService))
                                                .successHandler(customAuthenticationSuccessHandler))

                                // 3. 로그아웃 설정
                                .logout(logout -> logout
                                                .logoutUrl("/logout")
                                                .logoutSuccessUrl("/")
                                                .invalidateHttpSession(true)
                                                .deleteCookies("JSESSIONID"))
                                .userDetailsService(customUserDetailsService);

                return http.build();
        }
}