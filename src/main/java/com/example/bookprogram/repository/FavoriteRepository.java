
package com.example.bookprogram.repository;

import com.example.bookprogram.entity.Favorite;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface FavoriteRepository extends JpaRepository<Favorite, Long> {

    // 특정 회원이 찜한 도서 목록 조회
    List<Favorite> findByUserId(Long userId);

    // 특정 회원이 특정 도서를 이미 찜했는지 확인
    Optional<Favorite> findByUserIdAndBookId(Long userId, Long bookId);

    // 특정 회원의 특정 도서 찜 기록 확인
    boolean existsByUserIdAndBookId(Long userId, Long bookId);

    // 특정 회원이 찜한 도서의 ID 목록 조회
    @Query("SELECT f.book.id FROM Favorite f WHERE f.user.id = :userId")
    List<Long> findFavoriteBookIdsByUserId(@Param("userId") Long userId);

    // 특정 도서의 찜 기록 전체 삭제
    void deleteByBookId(Long bookId);
}
