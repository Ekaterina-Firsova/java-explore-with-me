package ru.practicum.ewm.service;

import jakarta.validation.constraints.Min;

import ru.practicum.ewm.dto.UserDto;
import ru.practicum.ewm.entity.User;

import java.util.List;
import java.util.Optional;

public interface UserService {

    List<UserDto> getUsers(List<Long> ids, @Min(0) int from, @Min(1) int size);

    UserDto createUser(UserDto newUser);

    void deleteUser(Long userId);

    User findById(Long userId);
}
