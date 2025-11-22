package chatting.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PageController {
    @GetMapping("/")
    public String home() {
        return "index"; // index.html
    }

    // SecurityConfig의 .loginPage("/login")
    @GetMapping("/login")
    public String login() {
        return "login"; // login.html
    }

    @GetMapping("/register")
    public String register() {
        return "register"; // register.html (일반 회원가입)
    }

    @GetMapping("/register-social")
    public String registerSocial() {
        // (권한 체크는 SecurityConfig에서 .hasRole("GUEST")로 처리 가능)
        return "register-social"; // register-social.html (소셜 추가 정보)
    }
}
