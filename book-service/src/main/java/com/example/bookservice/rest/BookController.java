package com.example.bookservice.rest;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
@RequestMapping("/api")
public class BookController {

    @GetMapping("/books")
    public String getBooks() {
        return "test book";
    }

    @GetMapping("/books-secured")
    public String getBooksSecured() {
        return "test book secured";
    }

    @GetMapping("/books-half-secured")
    public String getBooksHalfSecured() {
        return "books half secured";
    }

    @GetMapping("/me")
    public String me(Principal principal) {
        return principal.getName();
    }
}
