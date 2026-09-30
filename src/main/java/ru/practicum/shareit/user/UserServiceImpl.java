package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.ConflictException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UserUpdateDto;
import ru.practicum.shareit.user.model.User;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Override
    public User saveUser(User user) {
        log.info("Запрос на добавление нового пользователя с Id: {}", user.getId());
        for (User oldUser : userRepository.findAll()) {
            if (user.getEmail().equals(oldUser.getEmail())) {
                log.warn("Данный адрес эл.почты уже используется");
                throw new ConflictException("Пользователь с таким email уже зарегистрирован");
            }
        }
        log.info("Добавление нового пользователя с Id: {}", user.getId());
        return userRepository.add(user);
    }

    @Override
    public User findById(long userId) {
        log.info("Запрос на поиск пользователя c Id = {}", userId);
        User result = userRepository.findById(userId);
        if (result == null) {
            log.warn("Пользователь c Id = {} не найден", userId);
            throw new NotFoundException("Пользователь с Id = " + userId + " не найден.");
        }
        log.info("Пользователь c Id = {} найден", userId);
        return result;
    }

    @Override
    public List<User> findAllUsers() {
        log.info("Получение списка доступных пользователей");
        return userRepository.findAll();
    }

    @Override
    public User updateUser(long userId, UserUpdateDto updateDto) {
        log.info("Запрос на обновление данных пользователя c Id = {}", userId);
        if (userId == 0) {
            log.warn("Невозможно обновить пользователя: id не указан");
            throw new BadRequestException("Невозможно обновить пользователя: id не указан");
        }

        User user = userRepository.findById(userId);
        if (user == null) {
            log.warn("Пользователь c Id = {} не найден", userId);
            throw new NotFoundException("Невозможно обновить данные несуществующего пользователя");
        }

        if (updateDto.getEmail() != null) {
            for (User oldUser : userRepository.findAll()) {
                if (updateDto.getEmail().equals(oldUser.getEmail()) && userId != oldUser.getId()) {
                    log.warn("Данный адрес эл.почты уже используется");
                    throw new ConflictException("Пользователь с таким email уже зарегистрирован");
                }
            }
        }

        if (updateDto.getName() != null) {
            user.setName(updateDto.getName());
            log.info("Имя пользователя с Id = {} обновлено на {}", userId, user.getName());
        }
        if (updateDto.getEmail() != null) {
            user.setEmail(updateDto.getEmail());
        }
        log.info("Адрес эл.почты пользователя с Id = {} обновлено на {}", userId, user.getEmail());
        return userRepository.update(user);
    }

    @Override
    public void deleteUser(long userId) {
        log.info("Запрос на удаление пользователя c Id = {}", userId);
        if (userId == 0) {
            log.warn("Пользователь c Id = {} не найден. Данные указаны не верно", userId);
            throw new BadRequestException("Данные указаны не верно");
        }
        if (userRepository.findById(userId) == null) {
            log.warn("Пользователя c Id = {} не существует", userId);
            throw new NotFoundException("Данного пользователя не существует");
        }

        log.info("Удаление пользователя c Id = {}", userId);
        userRepository.delete(userId);
    }
}
