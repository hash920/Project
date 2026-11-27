
package com.example.bookprogram.controller;

import com.example.bookprogram.entity.Book;
import com.example.bookprogram.entity.Review;
import com.example.bookprogram.entity.User;
import com.example.bookprogram.service.BookService;
import com.example.bookprogram.service.ReviewService;
import com.example.bookprogram.service.FavoriteService;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import java.util.Map;
import java.util.HashMap;
import java.util.List;

@Controller
public class BookController {

    private final BookService bookService;
    private final ReviewService reviewService;
    private final FavoriteService favoriteService;

    public BookController(
            BookService bookService,
            ReviewService reviewService,
            FavoriteService favoriteService) {

        this.bookService = bookService;
        this.reviewService = reviewService;
        this.favoriteService = favoriteService;
    }

    // 도서 목록 화면
    @GetMapping("/books")
    public String list(HttpSession session,
                       @RequestParam(required = false) String keyword,
                       @RequestParam(required = false) String category,
                       @RequestParam(required = false) String sort,
                       @RequestParam(defaultValue = "0") int page,
                       Model model) {

        if (page < 0) {
            page = 0;
        }

        Sort sortOption;

        if ("title".equals(sort)) {
            sortOption = Sort.by(Sort.Direction.ASC, "title");
        } else if ("priceAsc".equals(sort)) {
            sortOption = Sort.by(Sort.Direction.ASC, "price");
        } else if ("priceDesc".equals(sort)) {
            sortOption = Sort.by(Sort.Direction.DESC, "price");
        } else {
            sortOption = Sort.unsorted();
        }

        PageRequest pageable = PageRequest.of(page, 10, sortOption);

        Page<Book> bookPage;

        if (category != null && !category.isBlank()) {
            bookPage = bookService.searchBooksByCategory(
                    category, pageable);
        } else if (keyword != null && !keyword.isBlank()) {
            bookPage = bookService.searchBooks(
                    keyword, pageable);
        } else {
            bookPage = bookService.getBooksPage(pageable);
        }

        model.addAttribute("books", bookPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", bookPage.getTotalPages());
        model.addAttribute("totalBooks", bookPage.getTotalElements());
        model.addAttribute("resultCount", bookPage.getTotalElements());

        model.addAttribute("keyword", keyword);
        model.addAttribute("category", category);
        model.addAttribute("sort", sort);
        model.addAttribute("loginUser", session.getAttribute("loginUser"));

        return "book-list";
    }

    // 도서 등록 화면
    @GetMapping("/books/new")
    public String createForm(HttpSession session, Model model) {

        if (session.getAttribute("loginUser") == null) {
            return "redirect:/login";
        }

        model.addAttribute("book", new Book());

        return "book-form";
    }

    // 도서 등록 처리
    @PostMapping("/books")
    public String save(
            @Valid @ModelAttribute Book book,
            BindingResult bindingResult,
            HttpSession session) {

        if (session.getAttribute("loginUser") == null) {
            return "redirect:/login";
        }

        // 입력값 검증에 실패하면 등록 화면으로 돌아가기
        if (bindingResult.hasErrors()) {
            return "book-form";
        }

        bookService.saveBook(book);

        return "redirect:/books";
    }

    // 도서 삭제 처리
    @PostMapping("/books/{id}/delete")
    public String delete(
            @PathVariable Long id,
            HttpSession session) {

        if (session.getAttribute("loginUser") == null) {
            return "redirect:/login";
        }

        bookService.deleteBook(id);

        return "redirect:/books";
    }

    // 도서 수정 화면
    @GetMapping("/books/{id}/edit")
    public String editForm(
            @PathVariable Long id,
            Model model,
            HttpSession session) {

        if (session.getAttribute("loginUser") == null) {
            return "redirect:/login";
        }

        Book book = bookService.getBookById(id);

        model.addAttribute("book", book);

        return "book-edit";
    }

    // 도서 상세 정보 화면
    @GetMapping("/books/{id}")
    public String detail(
            @PathVariable Long id,
            Model model,
            HttpSession session) {

        Book book = bookService.getBookById(id);

        model.addAttribute("book", book);

        // 해당 도서의 리뷰 조회
        model.addAttribute("reviews",
                reviewService.getReviewsByBookId(id));

        // 해당 도서의 평균 평점 조회
        model.addAttribute("averageRating",
                reviewService.getAverageRating(id));

        // 해당 도서의 리뷰 개수 조회
        model.addAttribute("reviewCount",
                reviewService.getReviewCount(id));

        // 같은 카테고리의 다른 도서 추천
        List<Book> recommendedBooks =
                bookService.getRecommendedBooks(book.getCategory(), id);

        model.addAttribute("recommendedBooks", recommendedBooks);

        // 추천 도서별 평균 평점 조회
        Map<Long, Double> recommendedRatings = new HashMap<>();

        for (Book recommendedBook : recommendedBooks) {
            recommendedRatings.put(
                    recommendedBook.getId(),
                    reviewService.getAverageRating(recommendedBook.getId())
            );
        }

        model.addAttribute("recommendedRatings", recommendedRatings);

        // 로그인한 회원의 찜 여부 확인
        User loginUser = (User) session.getAttribute("loginUser");

        boolean isFavorite = false;

        if (loginUser != null) {
            isFavorite = favoriteService.isFavorite(
                    loginUser.getId(), id);
        }

        model.addAttribute("isFavorite", isFavorite);

        // 추천 도서별 리뷰 개수 조회
        Map<Long, Long> recommendedReviewCounts = new HashMap<>();

        for (Book recommendedBook : recommendedBooks) {
            recommendedReviewCounts.put(
                    recommendedBook.getId(),
                    reviewService.getReviewCount(recommendedBook.getId())
            );
        }

        model.addAttribute(
                "recommendedReviewCounts", recommendedReviewCounts);

        return "book-detail";
    }

    // 리뷰 등록 처리
    @PostMapping("/books/{id}/reviews")
    public String addReview(
            @PathVariable Long id,
            @RequestParam(required = false) Integer rating,
            @RequestParam String content,
            HttpSession session,
            Model model) {

        User loginUser = (User) session.getAttribute("loginUser");

        if (loginUser == null) {
            return "redirect:/login";
        }

        // 별점 범위 검사
        if (rating == null || rating < 1 || rating > 5) {
            return "redirect:/books/" + id + "?error=rating";
        }


        // 리뷰 내용 길이 및 공백 검사
        if (content == null ||
                content.trim().isEmpty() ||
                content.trim().length() > 500) {

            return "redirect:/books/" + id + "?error=review";
        }


        Book book = bookService.getBookById(id);

        Review review = new Review();
        review.setRating(rating);
        review.setContent(content);
        review.setBook(book);

        // 리뷰 작성자 저장
        review.setUser(loginUser);

        reviewService.saveReview(review);

        return "redirect:/books/" + id;
    }

    // 리뷰 삭제 처리
    @PostMapping("/books/{bookId}/reviews/{reviewId}/delete")
    public String deleteReview(
            @PathVariable Long bookId,
            @PathVariable Long reviewId,
            HttpSession session,
            Model model) {

        User loginUser = (User) session.getAttribute("loginUser");

        if (loginUser == null) {
            return "redirect:/login";
        }

        // 리뷰 작성자 확인 후 삭제
        boolean deleted =
                reviewService.deleteReview(reviewId, loginUser);

        if (!deleted) {
            model.addAttribute(
                    "error", "본인이 작성한 리뷰만 삭제할 수 있습니다.");
        }

        return "redirect:/books/" + bookId;
    }


    // 도서 수정 처리
    @PostMapping("/books/{id}/edit")
    public String edit(
            @PathVariable Long id,
            @Valid @ModelAttribute Book book,
            BindingResult bindingResult,
            HttpSession session,
            Model model) {

        if (session.getAttribute("loginUser") == null) {
            return "redirect:/login";
        }

        // 입력값 검증에 실패하면 수정 화면으로 돌아가기
        if (bindingResult.hasErrors()) {
            model.addAttribute("bookId", id);
            return "book-edit";
        }

        Book existingBook = bookService.getBookById(id);

        existingBook.setTitle(book.getTitle());
        existingBook.setAuthor(book.getAuthor());
        existingBook.setPublisher(book.getPublisher());
        existingBook.setPrice(book.getPrice());
        existingBook.setCategory(book.getCategory());

        bookService.saveBook(existingBook);

        return "redirect:/books";
    }

}
