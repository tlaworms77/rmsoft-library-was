package com.rmsoft.library.controller;

import com.rmsoft.library.dto.BorrowDto;
import com.rmsoft.library.dto.ResponseDto;
import com.rmsoft.library.entity.Book;
import com.rmsoft.library.service.BookService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/book")
@RequiredArgsConstructor
public class BookController {

    private final BookService bookService;

//    @AuthenticationPrincipal String userId
    @GetMapping("/list")
    public ResponseDto<List<Book>> getList() {
        return bookService.list();
    }

    @PostMapping("/add")
    public ResponseDto<?> add(Book bookDto, MultipartFile file) {
        return bookService.add(bookDto, file);
    }

    @PostMapping("/update")
    public ResponseDto<?> update(Book bookDto, MultipartFile file) {
        return bookService.update(bookDto, file);
    }


    @PostMapping("/borrow")
    public ResponseDto<?> borrow(@RequestBody BorrowDto borrowDto) {
        return bookService.borrow(borrowDto);
    }

    @PostMapping("/return")
    public ResponseDto<?> returnBook(@RequestBody BorrowDto borrowDto) {
        return bookService.returnBook(borrowDto);
    }

    @PostMapping("/borrowList")
    public ResponseDto<?> borrowList(@RequestBody BorrowDto borrowDto) {
        return bookService.borrowList(borrowDto);
    }

}
