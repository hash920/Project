package com.example.bookprogram.repository;

import com.example.bookprogram.entity.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface BookRepository extends JpaRepository<Book, Long> {

    List<Book> findByTitleContaining(String title);

    // 카테고리별 도서 검색
    List<Book> findByCategory(String category);

    // 같은 카테고리의 다른 도서 추천
    List<Book> findByCategoryAndIdNot(String category, Long id);

    // 특정 카테고리에서 이미 찜한 도서를 제외하고 조회
    List<Book> findByCategoryAndIdNotIn(
            String category,
            List<Long> ids
    );

}