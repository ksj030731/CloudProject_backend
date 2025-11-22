package chatting.service;

import chatting.domain.User;
import chatting.dto.SocialRegisterRequestDto;
import chatting.repository.UserRepository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;

@Transactional
@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public void completeSocialSignup(Long userId, SocialRegisterRequestDto dto){

        User user = userRepository.findById(userId)
                .orElseThrow(()-> new RuntimeException("유저를 찾을 수 없습니다."));

        user.setNickname(dto.getNickname());
        user.setRegion(dto.getRegion());

        user.setRole("ROLE_USER");

    }

}
