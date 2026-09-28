package ru.practicum.shareit.user;

import ru.practicum.shareit.user.dto.UserUpdateDto;
import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface UserService {
    User saveUser(User user);

    User findById(long userId);

    List<User> findAllUsers();

    User updateUser(long userId , UserUpdateDto userUpdateDto);

    void deleteUser(long userId);
}
