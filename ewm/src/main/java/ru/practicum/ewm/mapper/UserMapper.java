package ru.practicum.ewm.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.ewm.dto.UserDto;
import ru.practicum.ewm.dto.UserShortDto;
import ru.practicum.ewm.entity.User;

import java.util.List;

@UtilityClass
public class UserMapper {

    public UserDto toUserDto(User user) {
        if (user == null) {
            return null;
        }
        return UserDto.builder()
                .id(user.getId())
                .email(user.getEmail())
                .name(user.getName())
                .build();
    }

    public User toUser(UserDto userDto) {
        if (userDto == null) {
            return null;
        }
        return User.builder()
                .id(userDto.getId())
                .email(userDto.getEmail())
                .name(userDto.getName())
                .build();
    }

    public User toUser(UserShortDto userDto) {
        if (userDto == null) {
            return null;
        }
        return User.builder()
                .id(userDto.getId())
                .name(userDto.getName())
                .build();
    }

    public UserShortDto toUserShortDto(User user) {
        if (user == null) {
            return null;
        }
        return UserShortDto.builder()
                .id(user.getId())
                .name(user.getName())
                .build();
    }

    public List<UserDto> toListUserDto(final List<User> users) {
        return users.stream()
                .map(UserMapper::toUserDto)
                .toList();
    }

}

