
package com.example.bookprogram.repository;

import com.example.bookprogram.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, Long> {

    // 특정 도서에 작성된 리뷰 조회
    List<Review> findByBookId(Long bookId);

    // 특정 회원이 작성한 리뷰 조회
    List<Review> findByUserId(Long userId);

    // 특정 회원이 작성한 리뷰의 평균 평점
    @Query("SELECT AVG(r.rating) FROM Review r WHERE r.user.id = :userId")
    Double findAverageRatingByUserId(@Param("userId") Long userId);

    // 특정 도서의 평균 평점 계산
    @Query("SELECT AVG(r.rating) FROM Review r WHERE r.book.id = :bookId")
    Double findAverageRatingByBookId(@Param("bookId") Long bookId);

    // 특정 도서의 리뷰 개수 계산
    long countByBookId(Long bookId);

    // 특정 도서의 리뷰 전체 삭제
    void deleteByBookId(Long bookId);
}
