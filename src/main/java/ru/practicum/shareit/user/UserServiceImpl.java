package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
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
        log.info("Запрос на добавление нового пользователя с email: {}", user.getEmail());
        userRepository.findByEmail(user.getEmail())
                .ifPresent(u -> {
                    log.warn("Данный адрес эл.почты уже используется");
                    throw new ConflictException("Пользователь с таким email уже зарегистрирован");
                });
        User savedUser = userRepository.save(user);
        log.info("Добавление нового пользователя с Id: {}", savedUser.getId());
        return savedUser;
    }

    @Override
    public User findById(Long userId) {
        log.info("Запрос на поиск пользователя c Id = {}", userId);
        User result = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с Id = " + userId + " не найден."));
        log.info("Пользователь c Id = {} найден", userId);
        return result;
    }

    @Override
    public List<User> findAllUsers() {
        log.info("Получение списка доступных пользователей");
        return userRepository.findAll();
    }

    @Override
    public User updateUser(Long userId, UserUpdateDto updateDto) {
        log.info("Запрос на обновление данных пользователя c Id = {}", userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Невозможно обновить данные несуществующего пользователя"));

        if (updateDto.getEmail() != null) {
            userRepository.findByEmail(updateDto.getEmail())
                    .filter(u -> u.getId() != userId)
                    .ifPresent(u -> {
                        log.warn("Данный адрес эл.почты уже используется");
                        throw new ConflictException("Пользователь с таким email уже зарегистрирован");
            });
            user.setEmail(updateDto.getEmail());
            log.info("Адрес эл.почты пользователя с Id = {} обновлено на {}", userId, user.getEmail());
        }

        if (updateDto.getName() != null) {
            user.setName(updateDto.getName());
            log.info("Имя пользователя с Id = {} обновлено на {}", userId, user.getName());
        }
        return userRepository.save(user);
    }

    @Override
    public void deleteUser(Long userId) {
        log.info("Запрос на удаление пользователя c Id = {}", userId);
        if (!userRepository.existsById(userId)) {
            log.warn("Пользователя c Id = {} не существует", userId);
            throw new NotFoundException("Данного пользователя не существует");
        }
        log.info("Удаление пользователя c Id = {}", userId);
        userRepository.deleteById(userId);
    }
}
