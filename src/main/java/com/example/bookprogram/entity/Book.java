
package com.example.bookprogram.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Entity
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "도서 제목을 입력해 주세요.")
    @Size(max = 100, message = "도서 제목은 100자 이하로 입력해 주세요.")
    private String title;

    @NotBlank(message = "저자를 입력해 주세요.")
    @Size(max = 50, message = "저자는 50자 이하로 입력해 주세요.")
    private String author;

    @NotBlank(message = "출판사를 입력해 주세요.")
    @Size(max = 50, message = "출판사는 50자 이하로 입력해 주세요.")
    private String publisher;

    @NotNull(message = "가격을 입력해 주세요.")
    @Min(value = 0, message = "가격은 0원 이상이어야 합니다.")
    private Integer price;

    @NotBlank(message = "카테고리를 선택해 주세요.")
    private String category;

    public Book() {
    }

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getAuthor() {
        return author;
    }

    public String getPublisher() {
        return publisher;
    }

    public Integer getPrice() {
        return price;
    }

    public String getCategory() {
        return category;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public void setPrice(Integer price) {
        this.price = price;
    }

    public void setCategory(String category) {
        this.category = category;
    }
}
