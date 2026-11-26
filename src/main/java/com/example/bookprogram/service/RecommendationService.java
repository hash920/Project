package com.example.bookprogram.service;

import com.example.bookprogram.entity.Favorite;
import com.example.bookprogram.entity.Book;
import com.example.bookprogram.repository.FavoriteRepository;
import com.example.bookprogram.repository.BookRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.HashMap;

@Service
public class RecommendationService {

    private final FavoriteRepository favoriteRepository;
    private final BookRepository bookRepository;

    public RecommendationService(
            FavoriteRepository favoriteRepository,
            BookRepository bookRepository) {

        this.favoriteRepository = favoriteRepository;
        this.bookRepository = bookRepository;
    }

    // 회원이 가장 많이 찜한 카테고리 찾기
    public String getPreferredCategory(Long userId) {

        List<Favorite> favorites =
                favoriteRepository.findByUserId(userId);

        Map<String, Integer> categoryCount = new HashMap<>();

        for (Favorite favorite : favorites) {

            Book book = favorite.getBook();
            String category = book.getCategory();

            categoryCount.put(
                    category,
                    categoryCount.getOrDefault(category, 0) + 1
            );
        }

        if (categoryCount.isEmpty()) {
            return null;
        }

        return categoryCount.entrySet()
                .stream()
                .max(Map.Entry.comparingByValue())
                .get()
                .getKey();
    }

    // 회원의 선호 카테고리를 기준으로 도서 추천
    public List<Book> getRecommendedBooks(Long userId) {

        // 선호 카테고리 확인
        String category = getPreferredCategory(userId);

        // 찜한 도서가 없으면 빈 목록 반환
        if (category == null) {
            return List.of();
        }

        // 회원이 이미 찜한 도서 ID 조회
        List<Long> favoriteBookIds =
                favoriteRepository.findFavoriteBookIdsByUserId(userId);

        // 이미 찜한 도서를 제외한 추천 도서 조회
        if (favoriteBookIds.isEmpty()) {
            return bookRepository.findByCategory(category);
        }

        return bookRepository.findByCategoryAndIdNotIn(
                category,
                favoriteBookIds
        );
    }
}