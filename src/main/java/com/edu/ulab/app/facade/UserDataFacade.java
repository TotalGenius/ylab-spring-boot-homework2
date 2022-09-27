package com.edu.ulab.app.facade;

import com.edu.ulab.app.dto.BookDto;
import com.edu.ulab.app.dto.UserDto;
import com.edu.ulab.app.exception.IncorrectUserIdException;
import com.edu.ulab.app.exception.NotFoundUserInRequestException;
import com.edu.ulab.app.mapper.BookMapper;
import com.edu.ulab.app.mapper.UserMapper;

import com.edu.ulab.app.service.impl.BookServiceImplTemplate;
import com.edu.ulab.app.service.impl.UserServiceImplTemplate;
import com.edu.ulab.app.web.request.BookRequest;
import com.edu.ulab.app.web.request.UserBookRequest;
import com.edu.ulab.app.web.response.BookResponse;
import com.edu.ulab.app.web.response.UserBookResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Slf4j
@Component
public class UserDataFacade {
    private final UserServiceImplTemplate userService;
    private final BookServiceImplTemplate bookService;
    private final UserMapper userMapper;
    private final BookMapper bookMapper;

    public UserDataFacade(UserServiceImplTemplate userService,
                          BookServiceImplTemplate bookService,
                          UserMapper userMapper,
                          BookMapper bookMapper) {
        this.userService = userService;
        this.bookService = bookService;
        this.userMapper = userMapper;
        this.bookMapper = bookMapper;
    }

    public UserBookResponse createUserWithBooks(UserBookRequest userBookRequest) {
        log.info("Got user book create request: {}", userBookRequest);
        UserDto userDto = Optional.ofNullable(userMapper.userRequestToUserDto(userBookRequest.getUserRequest())).orElseThrow(() -> new NotFoundUserInRequestException());
        log.info("Mapped user request: {}", userDto);

        UserDto createdUser = userService.createUser(userDto);
        log.info("Got created user: {}", createdUser);

        Optional<List<BookRequest>> booksId = Optional.ofNullable(userBookRequest.getBookRequests());

        List<BookResponse> bookResponses = booksId.orElse(Collections.emptyList())
                .stream()
                .filter(Objects::nonNull)
                .map(bookMapper::bookRequestToBookDto)
                .peek(bookDto -> bookDto.setUserId(createdUser.getId()))
                .peek(mappedBookDto -> log.info("mapped book: {}", mappedBookDto))
                .map(bookService::createBook)
                .peek(createdBook -> log.info("Created book: {}", createdBook))
                .map(bookMapper::bookDtoToBookResponse)
                .toList();
        log.info("Collected book responses: {}", bookResponses);

        return UserBookResponse.builder()
                .userResponse(userMapper.userDtoToUserResponse(createdUser))
                .bookResponses(bookResponses)
                .build();
    }

    public UserBookResponse updateUserWithBooks(UserBookRequest userBookRequest) {

        log.info("Got user book update request: {}", userBookRequest);
        UserDto userDto = Optional.ofNullable(userMapper.userRequestToUserDto(userBookRequest.getUserRequest())).orElseThrow(() -> new NotFoundUserInRequestException());
        log.info("Mapped user dto: {}", userDto);

        if (Long.valueOf(0).equals(userDto.getId())) {
            log.error("Incorrect user Id:", userDto.getId());
            throw new IncorrectUserIdException(userDto.getId());
        } else {
            UserDto updatedUser = userService.updateUser(userDto);
            log.info("Mapped updated user DTO: {}", updatedUser);

            Optional<List<BookRequest>> bookRequests = Optional.ofNullable(userBookRequest.getBookRequests());
            log.info("Collected book requests: {}", bookRequests);
            List<BookResponse> bookResponses = bookRequests.orElse(Collections.emptyList()).stream()
                    .filter(Objects::nonNull)
                    .map(bookMapper::bookRequestToBookDto)
                    .peek(bookDto -> log.info("Mapped book dto: {}", bookDto))
                    .peek(bookDto -> bookDto.setUserId(userDto.getId()))
                    .peek(bookService::updateBook)
                    .peek(bookDto -> log.info("Got book updated dto: {}", bookDto))
                    .map(bookDto -> bookMapper.bookDtoToBookResponse(bookDto))
                    .peek(bookResponse -> log.info("Got updated book response: {}", bookResponse))
                    .toList();

            return UserBookResponse.builder()
                    .userResponse(userMapper.userDtoToUserResponse(updatedUser))
                    .bookResponses(bookResponses)
                    .build();
        }
    }

    public UserBookResponse getUserWithBooks(Long userId) {

        log.error("Incorrect user id {}", userId);
        if (Long.valueOf(0)==userId) throw new IncorrectUserIdException(userId);
        log.info("Got user id: {}", userId);
        UserDto userDto = userService.getUserById(userId);
        log.info("Got user dto: {}", userDto);
        List<BookResponse> bookResponses = bookService.getBooksByUserId(userId)
                .stream()
                .peek(bookDto -> log.info("Got books dto by userId: {}",bookDto))
                .map(bookMapper::bookDtoToBookResponse)
                .peek(bookResponse -> log.info("Got book response: {}", bookResponse))
                .toList();

        return UserBookResponse.builder()
                .userResponse(userMapper.userDtoToUserResponse(userDto))
                .bookResponses(bookResponses)
                .build();
    }

    public UserBookResponse deleteUserWithBooks(Long userId) {
        log.error("Incorrect user id {}", userId);
        if (Long.valueOf(0)==userId) throw new IncorrectUserIdException(userId);
        log.info("Got user id that need to be deleted with books: {}", userId);
        UserDto deletedUser = userService.deleteUserById(userId);
        log.info("Got user dto of deleted user: {}", deletedUser);
        List<BookResponse> deletedBooks = bookService.deleteBooksByUserId(userId)
                .stream()
                .peek(bookDto -> log.info("Got deleted book: {}", bookDto))
                .map(bookMapper::bookDtoToBookResponse)
                .peek(bookResponse -> log.info("Got deleted book response: {}", bookResponse))
                .toList();
        return UserBookResponse.builder()
                .userResponse(userMapper.userDtoToUserResponse(deletedUser))
                .bookResponses(deletedBooks)
                .build();
    }
}
