package com.rmsoft.library.repository;

import com.rmsoft.library.entity.Book;
import com.rmsoft.library.entity.BookHistory;
import com.rmsoft.library.entity.BookHistoryId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BookHistoryRepository  extends JpaRepository<BookHistory, Long> {
    public BookHistory findByBookNoAndHistorySq(Long bookNo, Long historySq);

    public List<BookHistory> findAllByBookNoOrderByHistorySqDesc(Long bookNo);
}
