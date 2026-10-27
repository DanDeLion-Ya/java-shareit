package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemService {
    Item saveItem(Long userId, Item item);

    ItemDto findById(Long itemId, Long userId);

    Item updateItem(Long userId, Long itemId, ItemUpdateDto updateDto);

    List<ItemDto> searchItems(String text);

    List<ItemOwnerDto> findItemsByOwnerId(Long userId);

    CommentDto addComment(Long userId, Long itemId, CommentTextDto text);
}
