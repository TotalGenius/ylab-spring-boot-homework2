package com.edu.ulab.app.service;


import com.edu.ulab.app.dto.BookDto;

import java.util.List;

public interface BookService {
    BookDto createBook(BookDto bookDto);

    BookDto updateBook(BookDto bookDto);

    BookDto getBookById(Long id);
    List<BookDto> getBooksByUserId(Long userId);


    List<BookDto> deleteBooksByUserId(Long userId);
}
