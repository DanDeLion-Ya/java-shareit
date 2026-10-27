package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemUpdateDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.UserRepository;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    @Override
    public Item saveItem(long userId, Item item) {
        log.info("Запрос на добавление нового предмета");
        User owner = userRepository.findById(userId);
        if (owner == null) {
            log.warn("Пользователя не существует");
            throw new NotFoundException("Пользователь не найден");
        }
        log.info("Добавление пользователем с Id {}, нового предмета с Id: {}", userId, item.getId());
        item.setOwner(owner);
        return itemRepository.add(item);
    }

    @Override
    public Item findById(long itemId) {
        log.info("Запрос на поиск предмета c Id = {}", itemId);
        Item item = itemRepository.findById(itemId);
        if (item == null) {
            log.warn("Предмет c Id = {} не найден", itemId);
            throw new NotFoundException("Предмет с Id = " + itemId + " не найден");
        }
        log.info("Предмет c Id = {} найден", itemId);
        return item;
    }

    @Override
    public Item updateItem(long userId, long itemId, ItemUpdateDto updateDto) {
        log.info("Запрос на обновление данных о предмете c Id = {}", itemId);
        Item updatedItem = itemRepository.findById(itemId);
        User owner = userRepository.findById(userId);
        if (owner == null) {
            log.warn("Пользователь(владелец предмета) c Id = {} не найден", userId);
            throw new NotFoundException("Владелец предмета с Id = " + userId + " не найден");
        }
        if (updatedItem == null) {
            log.warn("Предмет c Id = {} не найден", itemId);
            throw new NotFoundException("Предмет с Id = " + itemId + " не найден.");
        }
        if (updatedItem.getOwner().getId() != userId) {
            log.warn("Пользователь не является владельцем предмета");
            throw new NotFoundException("Пользователь не является владельцем предмета");
        }
        if (updateDto.getName() != null) {
            log.info("Название предмета обновлено");
            updatedItem.setName(updateDto.getName());
        }
        if (updateDto.getDescription() != null) {
            log.info("Описание предмета обновлено");
            updatedItem.setDescription(updateDto.getDescription());
        }
        if (updateDto.getAvailable() != null) {
            log.info("Информация о доступности предмета обновлена");
            updatedItem.setAvailable(updateDto.getAvailable());
        }
        itemRepository.update(updatedItem);
        return updatedItem;
    }

    @Override
    public List<Item> searchItems(String text) {
        log.info("Запрос на поиск предмета по тексту: {}", text);
        return itemRepository.search(text);
    }

    @Override
    public List<Item> findItemsByOwnerId(long userId) {
        log.info("Запрос на список предметов пользователя c Id = {}", userId);
        User user = userRepository.findById(userId);
        if (user == null) {
            log.warn("Пользователь c Id = {} не найден", userId);
            throw new NotFoundException("Пользователь с Id = " + userId + " не найден");
        }
        log.info("Получен список предметов пользователя с Id: {}", userId);
        return itemRepository.findItemsByOwnerId(userId);
    }
}
