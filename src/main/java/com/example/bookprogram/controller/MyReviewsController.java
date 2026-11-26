
package com.example.bookprogram.controller;

import com.example.bookprogram.entity.User;
import com.example.bookprogram.service.ReviewService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MyReviewsController {

    private final ReviewService reviewService;

    public MyReviewsController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @GetMapping("/my-reviews")
    public String myReviews(HttpSession session, Model model) {

        User loginUser = (User) session.getAttribute("loginUser");

        if (loginUser == null) {
            return "redirect:/login";
        }

        model.addAttribute(
                "reviews",
                reviewService.getReviewsByUserId(loginUser.getId())
        );

        return "my-reviews";
    }
}
