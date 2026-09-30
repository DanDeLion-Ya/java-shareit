package ru.practicum.shareit.user;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserUpdateDto;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.List;

/**
 * TODO Sprint add-controllers.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/users")
public class UserController {
    private final UserService userService;
    private final UserMapper userMapper;

    @GetMapping("/{id}")
    public UserDto getUserById(@PathVariable long id) {
        User user = userService.findById(id);
        return userMapper.toDto(user);
    }

    @GetMapping
    public List<UserDto> getAllUsers() {
        List<User> userList = userService.findAllUsers();
        List<UserDto> userDtoList = new ArrayList<>();
        for (User user : userList) {
            UserDto userDto = userMapper.toDto(user);
            userDtoList.add(userDto);
        }
        return userDtoList;
    }

    @PostMapping
    public UserDto saveNewUser(@Valid @RequestBody UserDto userDto) {
        User user = userMapper.toUser(userDto);
        User savedUser = userService.saveUser(user);
        return userMapper.toDto(savedUser);
    }

    @PatchMapping("/{userId}")
    public UserDto updateUser(@PathVariable long userId,@Valid @RequestBody UserUpdateDto updateDto) {
        User updatedUser = userService.updateUser(userId, updateDto);
        return userMapper.toDto(updatedUser);
    }

    @DeleteMapping("/{userId}")
    public void deleteUser(@PathVariable long userId) {
        userService.deleteUser(userId);
    }
}
