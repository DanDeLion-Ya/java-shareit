package ru.practicum.shareit.user;

import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface UserRepository {
    User add(User user);

    User findById(long userId);

    List<User> findAll();

    User update(User user);

    void delete(long userId);
}
