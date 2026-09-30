package ru.practicum.shareit.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.ConflictException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UserUpdateDto;
import ru.practicum.shareit.user.model.User;

import static org.junit.jupiter.api.Assertions.*;


public class UserTest {
    private UserRepository userRepository;
    private UserService userService;

    @BeforeEach
    public void setUp() {
        userRepository = new InMemoryUserRepository();
        userService = new UserServiceImpl(userRepository);
    }

    @Test
    public void testShouldSaveUser() {
        User user = User.builder()
                .name("Агент Смит")
                .email("agent@yandex.ru")
                .build();

        userService.saveUser(user);
        assertEquals(1, userService.findAllUsers().size());
    }

    @Test
    public void testShouldSaveUserWithDuplicate() {
        User user1 = User.builder()
                .name("Агент Смит")
                .email("agent@yandex.ru")
                .build();

        User user2 = User.builder()
                .name("Агент007")
                .email("agent@yandex.ru")
                .build();

        userService.saveUser(user1);
        assertThrows(ConflictException.class, () -> userService.saveUser(user1));
    }

    @Test
    public void testShouldFindById() {
        User user = User.builder()
                .name("Агент Смит")
                .email("agent@yandex.ru")
                .build();

        userService.saveUser(user);
        assertEquals(user.getId(), userService.findById(user.getId()).getId());
    }

    @Test
    public void testShouldFindByIdNotFound() {
        User user1 = User.builder()
                .name("Агент Смит")
                .email("agent@yandex.ru")
                .build();


        userService.saveUser(user1);
        assertThrows(NotFoundException.class, () -> userService.findById(100500));
    }

    @Test
    public void testShouldUpdateUser() {
        User user1 = User.builder()
                .name("Агент Смит")
                .email("agent@yandex.ru")
                .build();
        userService.saveUser(user1);

        UserUpdateDto updateDto = new UserUpdateDto(null, "agent007@yandex.ru");
        User updatedUser = userService.updateUser(user1.getId(), updateDto);

        assertEquals("Агент Смит", updatedUser.getName());
        assertEquals("agent007@yandex.ru", updatedUser.getEmail());
    }

    @Test
    public void testShouldUpdateUserWithDuplicate() {
        User user1 = User.builder()
                .name("Агент Смит")
                .email("agent@yandex.ru")
                .build();

        User user2 = User.builder()
                .name("Агент007")
                .email("agent007@yandex.ru")
                .build();

        userService.saveUser(user1);
        userService.saveUser(user2);

        UserUpdateDto updateDto = new UserUpdateDto("Агент Смит", "agent007@yandex.ru");

        assertThrows(ConflictException.class, () -> userService.updateUser(user1.getId(), updateDto));
    }

    @Test
    public void testShouldUpdateUserNotFound() {
        User user = User.builder()
                .name("Агент Смит")
                .email("agent@yandex.ru")
                .build();

        userService.saveUser(user);

        UserUpdateDto updateDto = new UserUpdateDto("Нео", "neo@yandex.ru");

        assertThrows(NotFoundException.class, () -> userService.updateUser(100500, updateDto));
    }

    @Test
    public void testShouldUpdateUserBadRequest() {
        User user = User.builder()
                .name("Агент Смит")
                .email("agent@yandex.ru")
                .build();

        userService.saveUser(user);

        UserUpdateDto updateDto = new UserUpdateDto("Нео", "neo@yandex.ru");

        assertThrows(BadRequestException.class, () -> userService.updateUser(0, updateDto));
    }

    @Test
    public void testShouldDeleteUser() {
        User user1 = User.builder()
                .name("Агент Смит")
                .email("agent@yandex.ru")
                .build();
        userService.saveUser(user1);

        User user2 = User.builder()
                .name("Агент007")
                .email("agent007@yandex.ru")
                .build();
        userService.saveUser(user2);

        userService.deleteUser(user1.getId());

        assertEquals(1, userService.findAllUsers().size());
        assertEquals(user2.getId(), userService.findById(user2.getId()).getId());
    }

    @Test
    public void testShouldDeleteUserNotFound() {
        User user1 = User.builder()
                .name("Агент Смит")
                .email("agent@yandex.ru")
                .build();
        userService.saveUser(user1);

        User user2 = User.builder()
                .name("Агент007")
                .email("agent007@yandex.ru")
                .build();
        userService.saveUser(user2);

        assertThrows(NotFoundException.class, () -> userService.deleteUser(100500));
    }

    @Test
    public void testShouldDeleteUserBadRequest() {
        User user1 = User.builder()
                .name("Агент Смит")
                .email("agent@yandex.ru")
                .build();
        userService.saveUser(user1);

        User user2 = User.builder()
                .name("Агент007")
                .email("agent007@yandex.ru")
                .build();
        userService.saveUser(user2);

        assertThrows(BadRequestException.class, () -> userService.deleteUser(0));
    }

    @Test
    public void testShouldReturnAllUsers() {
        User user1 = User.builder()
                .name("Агент Смит")
                .email("agent@yandex.ru")
                .build();
        userService.saveUser(user1);

        User user2 = User.builder()
                .name("Агент007")
                .email("agent007@yandex.ru")
                .build();
        userService.saveUser(user2);

        assertEquals(2, userService.findAllUsers().size());
    }
}
