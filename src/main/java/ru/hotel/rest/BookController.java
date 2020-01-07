package ru.hotel.rest;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class BookController {
    public BookController() {
    }

    @GetMapping("/")
    public String listBook() {
        return "index";
    }

    @GetMapping("/book/{id}")
    public String editBook() {
        return "book";
    }
}
