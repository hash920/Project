
package com.example.bookprogram.service;

import com.example.bookprogram.entity.Book;
import com.example.bookprogram.entity.Favorite;
import com.example.bookprogram.entity.User;
import com.example.bookprogram.repository.BookRepository;
import com.example.bookprogram.repository.FavoriteRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class FavoriteService {

    private final FavoriteRepository favoriteRepository;
    private final BookRepository bookRepository;

    public FavoriteService(
            FavoriteRepository favoriteRepository,
            BookRepository bookRepository) {
        this.favoriteRepository = favoriteRepository;
        this.bookRepository = bookRepository;
    }

    // 회원의 찜 목록 조회
    public List<Favorite> getFavorites(Long userId) {
        return favoriteRepository.findByUserId(userId);
    }

    // 찜하기
    public void addFavorite(User user, Long bookId) {

        // 이미 찜한 도서인지 확인
        if (favoriteRepository.existsByUserIdAndBookId(user.getId(), bookId)) {
            return;
        }

        // 도서 조회
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new IllegalArgumentException("해당 도서가 없습니다."));

        // 찜 기록 생성
        Favorite favorite = new Favorite();
        favorite.setUser(user);
        favorite.setBook(book);

        favoriteRepository.save(favorite);
    }

    // 찜 취소
    public void removeFavorite(User user, Long bookId) {

        favoriteRepository.findByUserIdAndBookId(user.getId(), bookId)
                .ifPresent(favoriteRepository::delete);
    }

    // 해당 도서의 찜 여부 확인
    public boolean isFavorite(Long userId, Long bookId) {
        return favoriteRepository.existsByUserIdAndBookId(userId, bookId);
    }
}
