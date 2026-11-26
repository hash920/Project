
package com.example.bookprogram.repository;

import com.example.bookprogram.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    // 로그인 아이디로 회원 조회
    Optional<User> findByUsername(String username);

    // 아이디 중복 여부 확인
    boolean existsByUsername(String username);
}
