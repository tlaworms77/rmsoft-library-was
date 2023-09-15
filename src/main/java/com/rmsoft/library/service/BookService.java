package com.rmsoft.library.service;

import com.rmsoft.library.dto.BorrowDto;
import com.rmsoft.library.dto.ResponseDto;
import com.rmsoft.library.entity.Book;
import com.rmsoft.library.entity.BookHistory;
import com.rmsoft.library.entity.User;
import com.rmsoft.library.repository.BookHistoryRepository;
import com.rmsoft.library.repository.BookRepository;
import com.rmsoft.library.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class BookService {

    private final UserRepository userRepository;
    private final BookRepository bookRepository;
    private final BookHistoryRepository bookHistoryRepository;

    public ResponseDto<List<Book>> list() {
        try {
            List<Book> bookList = new ArrayList<>();
            bookList = bookRepository.findAll();

            return ResponseDto.setSuccess("도서목록 조회", bookList);
        } catch(Exception e) {
            return ResponseDto.setFailed("DataBase Error!");
        }
    }

    @Transactional
    public ResponseDto<?> add(Book book, MultipartFile file) {
        try {
            String bookTitle = book.getBookTitle();
            String bookAuthor = book.getBookAuthor();
            String bookPublish = book.getBookPublish();

            // 입력값 검증
            if(!StringUtils.hasText(bookTitle)) return ResponseDto.setFailed("도서제목을 입력해주세요.");
            if(!StringUtils.hasText(bookAuthor)) return ResponseDto.setFailed("저자를 입력해주세요.");
            if(!StringUtils.hasText(bookPublish)) return ResponseDto.setFailed("출판사를 입력해주세요.");

            //도서 중복 확인
            boolean isExists = bookRepository.existsByBookTitleAndBookAuthorAndBookPublish(book.getBookTitle(), book.getBookAuthor(), book.getBookPublish());
            if(isExists) return ResponseDto.setFailed("이미 존재하는 도서입니다.");
            book.setBorrowYn("대출가능");

            String fileUploadPath = null;
            if(file != null) {
                //파일저장
                fileUploadPath = System.getProperty("user.dir") + "/src/main/resources/static/outputs";
                UUID uuid = UUID.randomUUID();
                String fileName = uuid + "_" + file.getOriginalFilename(); //파일이름 재정의
                File saveFile = new File(fileUploadPath, fileName); //파일경로, 파일명
                file.transferTo(saveFile);

                //Book Entity에서 저장
                book.setFileName(fileName);
                book.setFilePath("/outputs/" + fileName);
            }

            Book saveBook = bookRepository.save(book);
            return ResponseDto.setSuccess("도서등록되었습니다.", saveBook);

        } catch(Exception e) {
            return ResponseDto.setFailed("DataBase Error!");
        }
    }

    @Transactional
    public ResponseDto<?> update(Book book, MultipartFile file) {
        try {
            Book originBook = bookRepository.findByBookNo(book.getBookNo());
            if(StringUtils.hasText(originBook.getFileName()) && file == null) {
                // 오리진 파일 삭제
                String filePath = originBook.getFilePath();
                String fileDeletePath = System.getProperty("user.dir") + "/src/main/resources/static" + filePath;
                Path deleteFilePath = Paths.get(fileDeletePath);
                Files.deleteIfExists(deleteFilePath);

                // Book Entity 파일 제거
                book.setFileName(null);
                book.setFilePath(null);
            } else if(StringUtils.hasText(originBook.getFileName()) && file != null) {
                // 오리진 파일 삭제
                String filePath = originBook.getFilePath();
                String fileDeletePath = System.getProperty("user.dir") + "/src/main/resources/static" + filePath;
                Path deleteFilePath = Paths.get(fileDeletePath);
                Files.deleteIfExists(deleteFilePath);

                String fileUploadPath = null;
                //파일저장
                fileUploadPath = System.getProperty("user.dir") + "/src/main/resources/static/outputs";
                UUID uuid = UUID.randomUUID();
                String fileName = uuid + "_" + file.getOriginalFilename(); //파일이름 재정의
                File saveFile = new File(fileUploadPath, fileName); //파일경로, 파일명
                file.transferTo(saveFile);

                //Book Entity에서 저장
                book.setFileName(fileName);
                book.setFilePath("/outputs/" + fileName);
            } else if(file != null) {
                String fileUploadPath = null;
                //파일저장
                fileUploadPath = System.getProperty("user.dir") + "/src/main/resources/static/outputs";
                UUID uuid = UUID.randomUUID();
                String fileName = uuid + "_" + file.getOriginalFilename(); //파일이름 재정의
                File saveFile = new File(fileUploadPath, fileName); //파일경로, 파일명
                file.transferTo(saveFile);

                //Book Entity에서 저장
                book.setFileName(fileName);
                book.setFilePath("/outputs/" + fileName);
            }

            Book saveBook = bookRepository.save(book);

            return ResponseDto.setSuccess("도서등록되었습니다.", saveBook);
        } catch(Exception e) {
            return ResponseDto.setFailed(e.getMessage());
        }
    }

    @Transactional
    public ResponseDto<?> borrow(BorrowDto borrowDto) {
        try {
            String borrowUserId = borrowDto.getUserId();
            User borrowUser = userRepository.findByUserId(borrowUserId);
            if(borrowUser == null) return ResponseDto.setFailed("존재하지않는 회원입니다.");

            Long bookNo = borrowDto.getBookNo();
            Book book = bookRepository.findByBookNo(bookNo);
            if(book == null) return ResponseDto.setFailed("도서가 존재하지 않습니다.");

            if(book.getBorrowYn().equals("대출중")) {
                return ResponseDto.setFailed("이미 대출중입니다.");
            }

            BookHistory bookHistory = new BookHistory();
            bookHistory.setBookNo(bookNo);
            bookHistory.setBorrowUserId(borrowUser.getUserId());
            bookHistory.setBorrowUserName(borrowUser.getUserName());
            bookHistory.setReturnDt(null);
            BookHistory saveBook = bookHistoryRepository.save(bookHistory);

            book.setHistorySq(saveBook.getHistorySq());
            book.setBorrowYn("대출중");
            bookRepository.save(book);

            return ResponseDto.setSuccess("대출처리되었습니다.", bookHistory);
        } catch(Exception e) {
            return ResponseDto.setFailed("DataBase Error!");
        }
    }

    @Transactional
    public ResponseDto<?> returnBook(BorrowDto borrowDto) {
        try {
            Long bookNo = borrowDto.getBookNo();
            Book book = bookRepository.findByBookNo(bookNo);
            if(book == null) return ResponseDto.setFailed("도서가 존재하지 않습니다.");

            if(book.getBorrowYn().equals("대출가능")) {
                return ResponseDto.setFailed("이미 반납된 도서입니다.");
            }

            BookHistory bookHistory = bookHistoryRepository.findByBookNoAndHistorySq(bookNo, book.getHistorySq());
            bookHistory.setReturnDt(new Date());
            bookHistoryRepository.save(bookHistory);

            book.setBorrowYn("대출가능");
            book.setHistorySq(null);
            bookRepository.save(book);

            return ResponseDto.setSuccess("반납되었습니다.", bookHistory);
        } catch(Exception e) {
            return ResponseDto.setFailed("DataBase Error!");
        }
    }

    public ResponseDto<?> borrowList(BorrowDto borrowDto) {
        try {
            Long bookNo = borrowDto.getBookNo();
            List<BookHistory> list = bookHistoryRepository.findAllByBookNoOrderByHistorySqDesc(bookNo);
            if(list.isEmpty()) return ResponseDto.setFailed("대출이력이 존재하지 않습니다.");

            return ResponseDto.setSuccess("대출이력현황을 조회하였습니다.", list);
        } catch(Exception e) {
            return ResponseDto.setFailed("DataBase Error!");
        }
    }

}
