
package com.example.bookprogram.service;

import com.example.bookprogram.entity.Review;
import com.example.bookprogram.entity.User;
import com.example.bookprogram.repository.ReviewRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class ReviewService {

    private final ReviewRepository reviewRepository;

    public ReviewService(ReviewRepository reviewRepository) {
        this.reviewRepository = reviewRepository;
    }

    // 특정 도서의 리뷰 조회
    public List<Review> getReviewsByBookId(Long bookId) {
        return reviewRepository.findByBookId(bookId);
    }

    // 리뷰 등록
    public Review saveReview(Review review) {
        return reviewRepository.save(review);
    }

    // 특정 도서의 평균 평점 계산
    public double getAverageRating(Long bookId) {

        Double average = reviewRepository.findAverageRatingByBookId(bookId);

        if (average == null) {
            return 0.0;
        }

        return average;
    }

    // 특정 도서의 리뷰 개수 조회
    public long getReviewCount(Long bookId) {
        return reviewRepository.countByBookId(bookId);
    }

    // 특정 회원이 작성한 리뷰 개수
    public long getUserReviewCount(Long userId) {
        return reviewRepository.findByUserId(userId).size();
    }

    // 특정 회원이 작성한 리뷰 목록 조회
    public List<Review> getReviewsByUserId(Long userId) {
        return reviewRepository.findByUserId(userId);
    }

    // 특정 회원이 작성한 리뷰의 평균 평점
    public double getUserAverageRating(Long userId) {
        Double average =
                reviewRepository.findAverageRatingByUserId(userId);

        if (average == null) {
            return 0.0;
        }

        return average;
    }

    // 리뷰 작성자 확인 후 삭제
    public boolean deleteReview(Long reviewId, User loginUser) {

        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new IllegalArgumentException("해당 리뷰가 없습니다."));

        // 리뷰 작성자와 로그인 회원이 같은지 확인
        if (!review.getUser().getId().equals(loginUser.getId())) {
            return false;
        }

        reviewRepository.delete(review);
        return true;
    }
}
