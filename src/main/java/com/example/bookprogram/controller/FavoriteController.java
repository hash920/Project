
package com.example.bookprogram.controller;

import com.example.bookprogram.entity.User;
import com.example.bookprogram.service.FavoriteService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.ui.Model;

@Controller
public class FavoriteController {

    private final FavoriteService favoriteService;

    public FavoriteController(FavoriteService favoriteService) {
        this.favoriteService = favoriteService;
    }

    // 도서 찜하기
    @PostMapping("/books/{id}/favorite")
    public String addFavorite(
            @PathVariable Long id,
            HttpSession session) {

        User loginUser = (User) session.getAttribute("loginUser");

        if (loginUser == null) {
            return "redirect:/login";
        }

        favoriteService.addFavorite(loginUser, id);

        return "redirect:/books/" + id;
    }

    // 도서 찜 취소
    @PostMapping("/books/{id}/favorite/delete")
    public String removeFavorite(
            @PathVariable Long id,
            HttpSession session) {

        User loginUser = (User) session.getAttribute("loginUser");

        if (loginUser == null) {
            return "redirect:/login";
        }

        favoriteService.removeFavorite(loginUser, id);

        return "redirect:/books/" + id;
    }

    // 나의 찜 목록
    @GetMapping("/favorites")
    public String favorites(HttpSession session, Model model) {

        User loginUser = (User) session.getAttribute("loginUser");

        if (loginUser == null) {
            return "redirect:/login";
        }

        model.addAttribute("favorites",
                favoriteService.getFavorites(loginUser.getId()));

        return "favorites";
    }
}
