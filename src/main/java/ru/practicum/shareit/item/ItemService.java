package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.ItemUpdateDto;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemService {
    Item saveItem(long userId, Item item);

    Item findById(long itemId);

    Item updateItem(long userId, long itemId, ItemUpdateDto updateDto);

    List<Item> searchItems(String text);

    List<Item> findItemsByOwnerId(long userId);
}
