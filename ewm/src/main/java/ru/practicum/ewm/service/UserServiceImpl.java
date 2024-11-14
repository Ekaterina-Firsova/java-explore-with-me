package ru.practicum.ewm.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import ru.practicum.ewm.dto.UserDto;
import ru.practicum.ewm.entity.User;
import ru.practicum.ewm.exception.ConflictException;
import ru.practicum.ewm.exception.NotFoundException;
import ru.practicum.ewm.mapper.CategoryMapper;
import ru.practicum.ewm.mapper.UserMapper;
import ru.practicum.ewm.repository.UserRepository;

import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {

    private final UserRepository repository;

    @Override
    public List<UserDto> getUsers(List<Long> ids, int from, int size) {

        List<User> users;

        if (ids == null || ids.isEmpty()) {
            // Получаем всех пользователей из базы данных
            users = repository.findAll(Sort.by("id"));
        } else {
            // Получаем пользователей по указанным ID
            users = repository.findByIdIn(ids, Sort.by("id"));
        }

        // Применяем смещение и ограничение
        int toIndex = Math.min(from + size, users.size());
        if (from > users.size()) {
            return Collections.emptyList(); // Если смещение больше размера списка, возвращаем пустой список
        }

        List<User> pagedUsers = users.subList(from, toIndex);

        // Конвертируем список User в UserDto и возвращаем
        return UserMapper.toListUserDto(pagedUsers);
    }

    @Transactional
    @Override
    public UserDto createUser(UserDto newUser) {
         if (repository.existsByEmail(newUser.getEmail())) {
            throw new ConflictException("Email already in use");
        }

        return UserMapper.toUserDto(repository.save(UserMapper.toUser(newUser)));
    }

    @Transactional
    @Override
    public void deleteUser(Long userId) {
        if (repository.existsById(userId)) {
            repository.deleteById(userId);
        } else {
            throw new EntityNotFoundException("User with id=" + userId + " was not found");
        }
    }

    @Override
    public User findById(Long userId) {
        return repository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User with id=" + userId + " was not found"));
    }
}
