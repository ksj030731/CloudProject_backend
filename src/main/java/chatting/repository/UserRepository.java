/**
 *
 * @author 김성준
 * 2025.11.04 작성
 */
package chatting.repository;

import chatting.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    // 1. (일반 로그인용) username으로 사용자 찾기
    Optional<User> findByUsername(String username);

    // 2. (소셜 로그인용) email과 provider로 사용자 찾기
    Optional<User> findByEmailAndProvider(String email, String provider);

    // 3. email로만 사용자 찾기 (일반/소셜 통합 ID)
    Optional<User> findByEmail(String email);
}
