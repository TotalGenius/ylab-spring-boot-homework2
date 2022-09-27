package com.edu.ulab.app.service.impl;

import com.edu.ulab.app.dto.UserDto;
import com.edu.ulab.app.entity.Person;
import com.edu.ulab.app.exception.NotFoundUserWithSuchIdException;
import com.edu.ulab.app.mapper.UserMapper;
import com.edu.ulab.app.repository.UserRepository;
import com.edu.ulab.app.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public UserServiceImpl(UserRepository userRepository,
                           UserMapper userMapper) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
    }

    @Override
    public UserDto createUser(UserDto userDto) {
        Person user = userMapper.userDtoToUser(userDto);
        log.info("Mapped user: {}", user);
        Person savedUser = userRepository.save(user);
        log.info("Saved user: {}", savedUser);
        return userMapper.userToUserDto(savedUser);
    }

    @Override
    public UserDto updateUser(UserDto userDto) {
        Person user = userMapper.userDtoToUser(userDto);
        log.info("Mapped user: {}", user);
        Person updatedUser = userRepository.save(user);
        log.info("Got updated user: {}", updatedUser);
        return userMapper.userToUserDto(updatedUser);
    }

    @Override
    public UserDto getUserById(Long id) {
        Person user = userRepository.findById(id).orElseThrow(()->new NotFoundUserWithSuchIdException(id));
        log.info("Got user from DB: {}", user);
        return userMapper.userToUserDto(user);
    }

    @Override
    public UserDto deleteUserById(Long id) {
        Person deletedUser = userRepository.findById(id).orElseThrow(()->new NotFoundUserWithSuchIdException(id));
        log.info("Got user that need to be deleted: {}", deletedUser);
        userRepository.deleteById(id);
        log.info("Deleted user with id:{}", id);
        return userMapper.userToUserDto(deletedUser);
    }
}
