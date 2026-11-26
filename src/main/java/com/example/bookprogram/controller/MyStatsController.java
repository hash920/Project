package com.example.bookprogram.controller;

import com.example.bookprogram.entity.User;
import com.example.bookprogram.service.FavoriteService;
import com.example.bookprogram.service.ReviewService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MyStatsController {

    private final FavoriteService favoriteService;
    private final ReviewService reviewService;

    public MyStatsController(
            FavoriteService favoriteService,
            ReviewService reviewService) {
        this.favoriteService = favoriteService;
        this.reviewService = reviewService;
    }

    @GetMapping("/my-stats")
    public String myStats(HttpSession session, Model model) {

        User loginUser =
                (User) session.getAttribute("loginUser");

        if (loginUser == null) {
            return "redirect:/login";
        }

        model.addAttribute("loginUser", loginUser);

        // 내가 찜한 도서 수
        model.addAttribute(
                "favoriteCount",
                favoriteService.getFavorites(loginUser.getId()).size()
        );

        // 내가 작성한 리뷰 수
        model.addAttribute(
                "reviewCount",
                reviewService.getUserReviewCount(loginUser.getId())
        );

        // 내가 작성한 리뷰의 평균 평점
        model.addAttribute(
                "averageRating",
                reviewService.getUserAverageRating(loginUser.getId())
        );

        return "my-stats";
    }
}