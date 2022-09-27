package com.edu.ulab.app.service.impl;

import com.edu.ulab.app.dto.BookDto;
import com.edu.ulab.app.entity.Book;
import com.edu.ulab.app.exception.NotFoundBookWithSuchIdException;
import com.edu.ulab.app.mapper.BookMapper;
import com.edu.ulab.app.repository.BookRepository;
import com.edu.ulab.app.service.BookService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Slf4j
@Service
public class BookServiceImpl implements BookService {

    private final BookRepository bookRepository;

    private final BookMapper bookMapper;

    public BookServiceImpl(BookRepository bookRepository,
                           BookMapper bookMapper) {
        this.bookRepository = bookRepository;
        this.bookMapper = bookMapper;
    }

    @Override
    public BookDto createBook(BookDto bookDto) {
        Book book = bookMapper.bookDtoToBook(bookDto);
        log.info("Mapped book: {}", book);
        Book savedBook = bookRepository.save(book);
        log.info("Saved book: {}", savedBook);
        return bookMapper.bookToBookDto(savedBook);
    }

    @Override
    public BookDto updateBook(BookDto bookDto) {
        Book book = bookMapper.bookDtoToBook(bookDto);
        log.info("Mapped book: {}", book);
        Book updatedBook = bookRepository.save(book);
        log.info("Got updated book: {}", updatedBook);
        return bookMapper.bookToBookDto(book);
    }

    @Override
    public BookDto getBookById(Long id) {
        Book book = bookRepository.findById(id).orElseThrow(()-> new NotFoundBookWithSuchIdException(id));
        return bookMapper.bookToBookDto(book);
    }

    @Override
    public List<BookDto> getBooksByUserId(Long userId) {
        List<Book> books = bookRepository.findAllByUserId(userId).orElse(Collections.emptyList());
        log.info("Got books by userID: {}", books);
        return books
                .stream()
                .map(bookMapper::bookToBookDto)
                .toList();
    }



    @Override
    public List<BookDto> deleteBooksByUserId(Long userId) {
        List<Book> deletedBookList=bookRepository.deleteAllByUserId(userId).orElse(Collections.emptyList());
        log.info("Got deleted books by userId: {}", deletedBookList);
        return deletedBookList.stream()
                .map(bookMapper::bookToBookDto)
                .toList();
    }
}
