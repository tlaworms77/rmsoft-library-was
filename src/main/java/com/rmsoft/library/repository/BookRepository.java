package com.rmsoft.library.repository;

import com.rmsoft.library.entity.Book;
import com.rmsoft.library.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookRepository extends JpaRepository<Book, Long> {
    public boolean existsByBookTitleAndBookAuthorAndBookPublish(String bookTitle, String bookAuthor, String bookPublish);
    public Book findByBookNo(Long BookNo);
}
