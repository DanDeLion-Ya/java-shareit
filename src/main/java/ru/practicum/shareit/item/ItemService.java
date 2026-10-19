package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.ItemUpdateDto;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemService {
    Item saveItem(Long userId, Item item);

    Item findById(Long itemId);

    Item updateItem(Long userId, Long itemId, ItemUpdateDto updateDto);

    List<Item> searchItems(String text);

    List<Item> findItemsByOwnerId(Long userId);
}
