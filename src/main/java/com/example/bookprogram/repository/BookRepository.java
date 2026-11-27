
package com.example.bookprogram.repository;

import com.example.bookprogram.entity.Book;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BookRepository extends JpaRepository<Book, Long> {

    List<Book> findByTitleContaining(String title);

    // 제목 검색 페이징
    Page<Book> findByTitleContaining(String title, Pageable pageable);

    // 카테고리별 도서 검색
    List<Book> findByCategory(String category);

    // 카테고리별 검색 페이징
    Page<Book> findByCategory(String category, Pageable pageable);

    // 같은 카테고리의 다른 도서 추천
    List<Book> findByCategoryAndIdNot(String category, Long id);

    // 특정 카테고리에서 이미 찜한 도서를 제외하고 조회
    List<Book> findByCategoryAndIdNotIn(
            String category,
            List<Long> ids
    );
}
