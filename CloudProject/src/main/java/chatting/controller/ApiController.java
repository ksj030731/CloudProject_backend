package chatting.controller;

import chatting.config.auth.PrincipalDetails;
import chatting.dto.SocialRegisterRequestDto;
import chatting.dto.UserResponseDTO;
import chatting.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import java.nio.file.attribute.UserPrincipal;

@Controller
@RestController("/api")
public class ApiController {

    public final UserService userService;

    public ApiController(UserService userService) {
        this.userService = userService;
    }

    // 추가정보 입력을 반영하는 메서드
    @PostMapping("/auth/register-social")
    public ResponseEntity<String> completeSignup(@AuthenticationPrincipal
                                                     PrincipalDetails userPrincipal,
                                                @RequestBody SocialRegisterRequestDto signupDTO
    ) {
        userService.completeSocialSignup(userPrincipal.getId(), signupDTO);
        return ResponseEntity.ok("추가 정보 입력 및 가입 완료!");

    }

    //사용자 정보를 프론트로 보내는 메서드
    @PostMapping("/me")
    public ResponseEntity<UserResponseDTO> me(@AuthenticationPrincipal PrincipalDetails userPrincipal) {
        if(userPrincipal == null){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        UserResponseDTO dto= UserResponseDTO.from(userPrincipal);
        return ResponseEntity.ok(dto);
    }


}
