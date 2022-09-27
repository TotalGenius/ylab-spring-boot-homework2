package com.edu.ulab.app.service.impl;

import com.edu.ulab.app.dto.BookDto;
import com.edu.ulab.app.entity.Book;
import com.edu.ulab.app.exception.NotFoundBookWithSuchIdException;
import com.edu.ulab.app.mapper.BookMapper;
import com.edu.ulab.app.service.BookService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Slf4j
@Service
public class BookServiceImplTemplate implements BookService {

    private final JdbcTemplate jdbcTemplate;

    private final BookMapper bookMapper;

    private final RowMapper<Book> bookRowMapper = ((rs, rowNum) -> {
        Book book = new Book();
        book.setId(rs.getLong(1));
        book.setTitle(rs.getString(2));
        book.setAuthor(rs.getString(3));
        book.setPageCount(4);
        book.setUserId(rs.getLong(5));
        return book;
    });

    public BookServiceImplTemplate(JdbcTemplate jdbcTemplate, BookMapper bookMapper) {
        this.jdbcTemplate = jdbcTemplate;
        this.bookMapper = bookMapper;
    }

    @Override
    public BookDto createBook(BookDto bookDto) {
        final String INSERT_SQL = "INSERT INTO BOOK(TITLE, AUTHOR, PAGE_COUNT, USER_ID) VALUES (?,?,?,?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(
                new PreparedStatementCreator() {
                    public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
                        PreparedStatement ps =
                                connection.prepareStatement(INSERT_SQL, new String[]{"id"});
                        ps.setString(1, bookDto.getTitle());
                        ps.setString(2, bookDto.getAuthor());
                        ps.setLong(3, bookDto.getPageCount());
                        ps.setLong(4, bookDto.getUserId());
                        return ps;
                    }
                },
                keyHolder);

        bookDto.setId(Objects.requireNonNull(keyHolder.getKey()).longValue());
        return bookDto;
    }

    @Override
    public BookDto updateBook(BookDto bookDto) {
        if (bookDto.getId() == null) {
            log.info("if id - null create new User");
            return createBook(bookDto);
        } else {
            final String SQL_UPDATE = "UPDATE BOOK SET title=?, author=?, page_count=?, user_id=? WHERE id=?";
            Long id = bookDto.getId();
            jdbcTemplate.update(SQL_UPDATE, bookDto.getTitle(), bookDto.getAuthor(), bookDto.getPageCount(), bookDto.getUserId(), id);
            return getBookById(id);
        }
    }

    @Override
    public BookDto getBookById(Long id) {
        final String GET_SQL = "SELECT * FROM BOOK WHERE book.id = ?";
        Book book = jdbcTemplate.query(GET_SQL, bookRowMapper, id)
                .stream()
                .findAny()
                .orElseThrow(() -> new NotFoundBookWithSuchIdException(id));
        log.info("Got user from query: {}", book);
        return bookMapper.bookToBookDto(book);
    }

    @Override
    public List<BookDto> getBooksByUserId(Long userId) {
        final String GET_SQL = "SELECT * FROM BOOK WHERE book.user_id = ?";
        List<Book> books = jdbcTemplate.query(GET_SQL, bookRowMapper, userId);
        if (books == null) books = Collections.emptyList();
        log.info("Got user from query: {}", books);
        return books.stream()
                .map(book -> bookMapper.bookToBookDto(book))
                .toList();
    }

    @Override
    public List<BookDto> deleteBooksByUserId(Long userId) {
        final String DELETE_SQL = "DELETE * FROM BOOK WHERE book.user_id = ?";
        List<BookDto> deletedBookDtos = getBooksByUserId(userId);
        log.info("Got deleted books: {}", deletedBookDtos);
        jdbcTemplate.update(DELETE_SQL, userId);
        log.info("Delete user by id: {}", userId);
        return deletedBookDtos;
    }

}
