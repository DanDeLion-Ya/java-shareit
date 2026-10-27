package ru.practicum.shareit.user;

import ru.practicum.shareit.user.dto.UserUpdateDto;
import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface UserService {
    User saveUser(User user);

    User findById(Long userId);

    List<User> findAllUsers();

    User updateUser(Long userId, UserUpdateDto userUpdateDto);

    void deleteUser(Long userId);
}
