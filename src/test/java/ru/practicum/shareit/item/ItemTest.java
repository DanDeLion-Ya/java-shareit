package ru.practicum.shareit.item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemUpdateDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.InMemoryUserRepository;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import static org.junit.jupiter.api.Assertions.*;

public class ItemTest {
    private ItemRepository itemRepository;
    private UserRepository userRepository;
    private ItemService itemService;

    @BeforeEach
    public void setUp() {
        itemRepository = new InMemoryItemRepository();
        userRepository = new InMemoryUserRepository();
        itemService = new ItemServiceImpl(itemRepository, userRepository);
    }

    @Test
    public void testShouldSaveItem() {
        Item item = Item.builder()
                .name("Вещь")
                .description("Вещь как вещь!")
                .available(true)
                .build();

        User user = User.builder()
                .name("Джон Вещевуик")
                .email("veshewick@ya.ru")
                .build();
        userRepository.add(user);

        Item savedItem = itemService.saveItem(user.getId(), item);
        assertEquals(savedItem, itemService.findById(savedItem.getId()));
        assertEquals(user.getId(), savedItem.getOwner().getId());
    }

    @Test
    public void testShouldSaveItemOwnerNotFound() {
        Item item = Item.builder()
                .name("Вещь")
                .description("Вещь как вещь!")
                .available(true)
                .build();

        assertThrows(NotFoundException.class, () -> itemService.saveItem(100500, item));
    }

    @Test
    public void testShouldFindById() {
        Item item1 = Item.builder()
                .name("Вещь")
                .description("Вещь как вещь!")
                .available(true)
                .build();

        User user1 = User.builder()
                .name("Джоневуик")
                .email("veshick@ya.ru")
                .build();
        userRepository.add(user1);
        itemService.saveItem(user1.getId(), item1);

        assertEquals(item1, itemService.findById(item1.getId()));
    }

    @Test
    public void testShouldFindByIdNotFound() {
        Item item1 = Item.builder()
                .name("Вещь")
                .description("Вещь как вещь!")
                .available(true)
                .build();

        User user1 = User.builder()
                .name("Агент Смит")
                .email("agent@yandex.ru")
                .build();
        userRepository.add(user1);
        itemService.saveItem(user1.getId(), item1);

        assertThrows(NotFoundException.class, () -> itemService.findById(100500));
    }

    @Test
    public void testShouldUpdateItem() {
        Item item = Item.builder()
                .name("Вещь")
                .description("Вещь как вещь!")
                .available(true)
                .build();

        User user = User.builder()
                .name("Агент Смит")
                .email("agent@yandex.ru")
                .build();
        userRepository.add(user);
        itemService.saveItem(user.getId(), item);

        ItemUpdateDto itemUpdateDto = new ItemUpdateDto("Нечто", null, null);
        Item updatedItem = itemService.updateItem(user.getId(), item.getId(), itemUpdateDto);

        assertEquals("Нечто", updatedItem.getName());
        assertEquals("Вещь как вещь!", updatedItem.getDescription());
        assertTrue(updatedItem.isAvailable());
    }

    @Test
    public void testShouldUpdateItemNotFound() {
        Item item = Item.builder()
                .name("Вещь")
                .description("Вещь как вещь!")
                .available(true)
                .build();

        User user = User.builder()
                .name("Агент Смит")
                .email("agent@yandex.ru")
                .build();
        userRepository.add(user);
        itemService.saveItem(user.getId(), item);

        ItemUpdateDto itemUpdateDto = new ItemUpdateDto("Нечто", null, null);

        assertThrows(NotFoundException.class, () -> itemService.updateItem(user.getId(), 100500, itemUpdateDto));
    }

    @Test
    public void testShouldUpdateItemUserNotFound() {
        User user = User.builder()
                .name("Агент Смит")
                .email("agent@yandex.ru")
                .build();

        Item item = Item.builder()
                .name("Вещь")
                .description("Вещь как вещь!")
                .available(true)
                .build();
        userRepository.add(user);
        itemService.saveItem(user.getId(), item);

        ItemUpdateDto itemUpdateDto = new ItemUpdateDto("Нечто", null, null);

        assertThrows(NotFoundException.class, () -> itemService.updateItem(100500, item.getId(), itemUpdateDto));
    }

    @Test
    public void testShouldUpdateItemAnotherUserNotFound() {
        User user1 = User.builder()
                .name("Агент Смит")
                .email("agent@yandex.ru")
                .build();

        Item item1 = Item.builder()
                .name("Вещь")
                .description("Вещь как вещь!")
                .available(true)
                .build();
        userRepository.add(user1);
        itemService.saveItem(user1.getId(), item1);

        Item item2 = Item.builder()
                .name("Другая вещь")
                .description("Вообще не та вещь!")
                .available(true)
                .build();

        User user2 = User.builder()
                .name("Джоневуик")
                .email("veshick@ya.ru")
                .build();
        userRepository.add(user2);
        itemService.saveItem(user2.getId(), item2);

        ItemUpdateDto itemUpdateDto = new ItemUpdateDto("Нечто", null, null);

        assertThrows(NotFoundException.class, () -> itemService.updateItem(user2.getId(), item1.getId(), itemUpdateDto));
    }

    @Test
    public void testShouldSearchTextIsEmpty() {
        User user = User.builder()
                .name("Агент Смит")
                .email("agent@yandex.ru")
                .build();

        Item item = Item.builder()
                .name("Вещь")
                .description("Вещь как вещь!")
                .available(true)
                .build();
        userRepository.add(user);
        itemService.saveItem(user.getId(), item);

        assertTrue(itemService.searchItems("").isEmpty());
    }

    @Test
    public void testShouldSearchByName() {
        User user = User.builder()
                .name("Агент Смит")
                .email("agent@yandex.ru")
                .build();

        Item item = Item.builder()
                .name("Вещь")
                .description("Самая нужная штука!")
                .available(true)
                .build();
        userRepository.add(user);
        itemService.saveItem(user.getId(), item);

        assertEquals(1, itemService.searchItems("вещ").size());
    }

    @Test
    public void testShouldSearchByDescription() {
        User user = User.builder()
                .name("Агент Смит")
                .email("agent@yandex.ru")
                .build();

        Item item = Item.builder()
                .name("Полезная штука")
                .description("Вещь как вещь!")
                .available(true)
                .build();
        userRepository.add(user);
        itemService.saveItem(user.getId(), item);

        assertEquals(1, itemService.searchItems("вещь").size());
    }

    @Test
    public void testShouldSearchIsAvailable() {
        User user = User.builder()
                .name("Агент Смит")
                .email("agent@yandex.ru")
                .build();

        Item item = Item.builder()
                .name("Вещь")
                .description("Самая нужная штука!")
                .available(false)
                .build();
        userRepository.add(user);
        itemService.saveItem(user.getId(), item);

        assertEquals(0, itemService.searchItems("вещь").size());
    }

    @Test
    public void testShouldNotFindByText() {
        User user = User.builder()
                .name("Агент Смит")
                .email("agent@yandex.ru")
                .build();

        Item item = Item.builder()
                .name("Вещь")
                .description("Самая нужная штука!")
                .available(true)
                .build();
        userRepository.add(user);
        itemService.saveItem(user.getId(), item);

        assertEquals(0, itemService.searchItems("Нечто").size());
    }

    @Test
    public void testShouldReturnItemsByOwner() {
        User user = User.builder()
                .name("Агент Смит")
                .email("agent@yandex.ru")
                .build();

        Item item1 = Item.builder()
                .name("Компьютер")
                .description("2 ядра, 2 гига!")
                .available(true)
                .build();

        Item item2 = Item.builder()
                .name("Видеокарта")
                .description("Игровая!")
                .available(true)
                .build();
        userRepository.add(user);
        itemService.saveItem(user.getId(), item1);
        itemService.saveItem(user.getId(), item2);

        assertEquals(2, itemService.findItemsByOwnerId(user.getId()).size());
    }

    @Test
    public void testShouldItemsByOwnerNotFound() {
        User user = User.builder()
                .name("Агент Смит")
                .email("agent@yandex.ru")
                .build();

        Item item1 = Item.builder()
                .name("Компьютер")
                .description("2 ядра, 2 гига!")
                .available(true)
                .build();

        Item item2 = Item.builder()
                .name("Видеокарта")
                .description("Игровая!")
                .available(true)
                .build();
        userRepository.add(user);
        itemService.saveItem(user.getId(), item1);
        itemService.saveItem(user.getId(), item2);

        assertThrows(NotFoundException.class, () -> itemService.findItemsByOwnerId(100500));
    }
}
