
package com.example.bookprogram.service;

import com.example.bookprogram.entity.Book;
import com.example.bookprogram.repository.BookRepository;
import com.example.bookprogram.repository.FavoriteRepository;
import com.example.bookprogram.repository.ReviewRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Sort;

import java.util.List;

@Service
public class BookService {

    private final BookRepository bookRepository;
    private final ReviewService reviewService;
    private final FavoriteRepository favoriteRepository;
    private final ReviewRepository reviewRepository;

    public BookService(
            BookRepository bookRepository,
            ReviewService reviewService,
            FavoriteRepository favoriteRepository,
            ReviewRepository reviewRepository) {

        this.bookRepository = bookRepository;
        this.reviewService = reviewService;
        this.favoriteRepository = favoriteRepository;
        this.reviewRepository = reviewRepository;
    }

    // 전체 도서 조회
    public List<Book> getAllBooks() {
        return bookRepository.findAll();
    }

    // 도서 제목순 정렬
    public List<Book> getBooksOrderByTitle() {
        return bookRepository.findAll(Sort.by(Sort.Direction.ASC, "title"));
    }

    // 가격 낮은 순 정렬
    public List<Book> getBooksOrderByPriceAsc() {
        return bookRepository.findAll(Sort.by(Sort.Direction.ASC, "price"));
    }

    // 가격 높은 순 정렬
    public List<Book> getBooksOrderByPriceDesc() {
        return bookRepository.findAll(Sort.by(Sort.Direction.DESC, "price"));
    }

    // 도서 제목 검색
    public List<Book> searchBooks(String keyword) {
        return bookRepository.findByTitleContaining(keyword);
    }

    // 카테고리별 도서 검색
    public List<Book> searchBooksByCategory(String category) {
        return bookRepository.findByCategory(category);
    }

    // 도서 상세 조회
    public Book getBookById(Long id) {
        return bookRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 도서가 없습니다."));
    }

    // 도서 등록
    public Book saveBook(Book book) {
        return bookRepository.save(book);
    }

    // 같은 카테고리의 다른 도서를 평균 평점순으로 추천
    public List<Book> getRecommendedBooks(String category, Long bookId) {

        List<Book> books = bookRepository.findByCategoryAndIdNot(category, bookId);

        books.sort((book1, book2) ->
                Double.compare(
                        reviewService.getAverageRating(book2.getId()),
                        reviewService.getAverageRating(book1.getId())
                )
        );

        return books;
    }

    // 도서 삭제
    @Transactional
    public void deleteBook(Long id) {

        // 해당 도서의 찜 기록 삭제
        favoriteRepository.deleteByBookId(id);

        // 해당 도서의 리뷰 기록 삭제
        reviewRepository.deleteByBookId(id);

        // 도서 삭제
        bookRepository.deleteById(id);
    }
}
