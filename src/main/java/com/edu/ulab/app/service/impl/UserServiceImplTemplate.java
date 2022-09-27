package com.edu.ulab.app.service.impl;

import com.edu.ulab.app.dto.UserDto;
import com.edu.ulab.app.entity.Person;
import com.edu.ulab.app.exception.NotFoundUserWithSuchIdException;
import com.edu.ulab.app.mapper.UserMapper;
import com.edu.ulab.app.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Service;

import java.sql.PreparedStatement;
import java.util.Objects;

@Slf4j
@Service
public class UserServiceImplTemplate implements UserService {
    private final JdbcTemplate jdbcTemplate;

    private final UserMapper userMapper;

    private final RowMapper<Person> personRowMapper = ((rs, rowNum) -> {
        Person person = new Person();
        person.setId(rs.getLong(1));
        person.setFullName(rs.getString(2));
        person.setTitle(rs.getString(3));
        person.setAge(rs.getInt(4));
        return person;
    });
    public UserServiceImplTemplate(JdbcTemplate jdbcTemplate, UserMapper userMapper) {
        this.jdbcTemplate = jdbcTemplate;
        this.userMapper = userMapper;
    }

    @Override
    public UserDto createUser(UserDto userDto) {

        final String INSERT_SQL = "INSERT INTO PERSON(FULL_NAME, TITLE, AGE) VALUES (?,?,?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(
                connection -> {
                    PreparedStatement ps = connection.prepareStatement(INSERT_SQL, new String[]{"id"});
                    ps.setString(1, userDto.getFullName());
                    ps.setString(2, userDto.getTitle());
                    ps.setLong(3, userDto.getAge());
                    return ps;
                }, keyHolder);

        userDto.setId(Objects.requireNonNull(keyHolder.getKey()).longValue());
        return userDto;
    }

    @Override
    public UserDto updateUser(UserDto userDto) {
        if (userDto.getId() == null) {
            log.info("if id - null create new User");
            return createUser(userDto);
        } else {
            final String SQL_UPDATE = "UPDATE PERSON SET full_name=?, title=?, age=? WHERE id=?";
            Long id = userDto.getId();
            jdbcTemplate.update(SQL_UPDATE, userDto.getFullName(), userDto.getTitle(), userDto.getAge(), id);
            return getUserById(id);
        }
    }

    @Override
    public UserDto getUserById(Long id) {
        final String GET_SQL = "SELECT * FROM PERSON WHERE person.id = ?";
        Person user = jdbcTemplate.query(GET_SQL, personRowMapper, id)
                .stream()
                .findAny()
                .orElseThrow(()-> new NotFoundUserWithSuchIdException(id));
        log.info("Got user from query: {}", user);
        return userMapper.userToUserDto(user);
    }

    @Override
    public UserDto deleteUserById(Long id) {
        final String DELETE_SQL = "DELETE FROM PERSON WHERE person.id = ?";
        UserDto deletedUser = getUserById(id);
        log.info("Got user deleted user: {}", deletedUser);
        jdbcTemplate.update(DELETE_SQL, id);
        log.info("Delete user by id: {}", id);
        return deletedUser;
    }
}
