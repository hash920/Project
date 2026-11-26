package com.example.bookprogram.controller;

import com.example.bookprogram.entity.User;
import com.example.bookprogram.service.RecommendationService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import com.example.bookprogram.service.ReviewService;
import com.example.bookprogram.entity.Book;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;

@Controller
public class RecommendationController {

    private final RecommendationService recommendationService;
    private final ReviewService reviewService;

    public RecommendationController(
            RecommendationService recommendationService,
            ReviewService reviewService) {

        this.recommendationService = recommendationService;
        this.reviewService = reviewService;
    }

    // 개인 맞춤형 도서 추천 페이지
    @GetMapping("/recommendations")
    public String recommendations(
            HttpSession session,
            Model model) {

        // 로그인한 회원 확인
        User loginUser = (User) session.getAttribute("loginUser");

        if (loginUser == null) {
            return "redirect:/login";
        }

        // 회원의 선호 카테고리 조회
        String preferredCategory =
                recommendationService.getPreferredCategory(loginUser.getId());

        // 개인 맞춤형 추천 도서 조회
        List<Book> recommendedBooks =
                new ArrayList<>(
                        recommendationService.getRecommendedBooks(loginUser.getId())
                );
// 추천 도서의 평균 평점이 높은 순서로 정렬
        recommendedBooks.sort((book1, book2) ->
                Double.compare(
                        reviewService.getAverageRating(book2.getId()),
                        reviewService.getAverageRating(book1.getId())
                )
        );

// 추천 도서 목록 전달
        model.addAttribute("recommendedBooks", recommendedBooks);

// 추천 도서별 평균 평점 저장
        Map<Long, Double> recommendedRatings = new HashMap<>();

// 추천 도서별 리뷰 개수 저장
        Map<Long, Long> recommendedReviewCounts = new HashMap<>();

        for (Book book : recommendedBooks) {

            recommendedRatings.put(
                    book.getId(),
                    reviewService.getAverageRating(book.getId())
            );

            recommendedReviewCounts.put(
                    book.getId(),
                    reviewService.getReviewCount(book.getId())
            );
        }

        model.addAttribute("recommendedRatings", recommendedRatings);
        model.addAttribute("recommendedReviewCounts", recommendedReviewCounts);

        model.addAttribute("preferredCategory", preferredCategory);

        return "recommendations";
    }
}