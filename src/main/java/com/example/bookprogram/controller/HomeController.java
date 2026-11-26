package com.example.bookprogram.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

// 웹페이지 처리
@Controller
public class HomeController {
    // 홈페이지 주소로 접속 했을 때 실행
    @GetMapping("/")
    public String home() {
        return "index";
    }
}