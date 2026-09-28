package ru.practicum.shareit.item;

import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemRepository {
    Item add(Item item);

    Item findById(long itemId);

    List<Item> findAll();

    Item update(Item item);

    List<Item> search(String text);

    List<Item> findItemsByOwnerId(long ownerId);
}
