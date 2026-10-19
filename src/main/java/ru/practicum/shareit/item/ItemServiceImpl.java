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
    public Item saveItem(Long userId, Item item) {
        log.info("Запрос на добавление нового предмета");
        User owner = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));

        item.setOwner(owner);
        Item savedItem = itemRepository.save(item);
        log.info("Добавление пользователем с Id {}, нового предмета с Id: {}", userId, savedItem.getId());
        return savedItem;
    }

    @Override
    public Item findById(Long itemId) {
        log.info("Запрос на поиск предмета c Id = {}", itemId);
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Предмет с Id = " + itemId + " не найден"));
        log.info("Предмет c Id = {} найден", itemId);
        return item;
    }

    @Override
    public Item updateItem(Long userId, Long itemId, ItemUpdateDto updateDto) {
        log.info("Запрос на обновление данных о предмете c Id = {}", itemId);
        Item updatedItem = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Предмет с Id = " + itemId + " не найден."));
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("Пользователь с Id = " + userId + " не найден.");
        }
        if (!updatedItem.getOwner().getId().equals(userId)) {
            log.warn("Пользователь c Id = {} не является владельцем предмета", userId);
            throw new NotFoundException("Пользователь c Id = " + userId + " не является владельцем предмета");
        }
        if (updateDto.getName() != null) {
            log.info("Название предмета с Id = {} обновлено", itemId);
            updatedItem.setName(updateDto.getName());
        }
        if (updateDto.getDescription() != null) {
            log.info("Описание предмета с Id = {} обновлено", itemId);
            updatedItem.setDescription(updateDto.getDescription());
        }
        if (updateDto.getAvailable() != null) {
            log.info("Информация о доступности предмета с Id = {} обновлена", itemId);
            updatedItem.setAvailable(updateDto.getAvailable());
        }
        itemRepository.save(updatedItem);
        return updatedItem;
    }

    @Override
    public List<Item> searchItems(String text) {
        log.info("Запрос на поиск предмета по тексту: {}", text);
        if (text == null || text.isBlank()) {
            return List.of();
        }
        return itemRepository.search(text);
    }

    @Override
    public List<Item> findItemsByOwnerId(Long userId) {
        log.info("Запрос на список предметов пользователя c Id = {}", userId);
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("Пользователь с Id = " + userId + " не найден");
        }
        log.info("Получен список предметов пользователя с Id: {}", userId);
        return itemRepository.findByOwnerId(userId);
    }
}
