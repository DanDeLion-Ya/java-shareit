package ru.practicum.shareit.item;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemUpdateDto;
import ru.practicum.shareit.item.model.Item;

import java.util.ArrayList;
import java.util.List;

/**
 * TODO Sprint add-controllers.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/items")
public class ItemController {
    private final ItemService itemService;
    private final ItemMapper itemMapper;

    @PostMapping
    public ItemDto saveNewItem(@RequestHeader("X-Sharer-User-Id") long userId,
                               @Valid @RequestBody ItemDto itemDto) {

        Item item = itemMapper.toItem(itemDto);
        Item newItem = itemService.saveItem(userId, item);
        return itemMapper.toDto(newItem);
    }

    @PatchMapping("/{itemId}")
    public ItemDto updateItem(@RequestHeader("X-Sharer-User-Id") long userId,
                              @PathVariable long itemId,
                              @RequestBody ItemUpdateDto itemUpdateDto) {

        Item item = itemService.updateItem(userId, itemId, itemUpdateDto);
        ItemDto itemDto = itemMapper.toDto(item);
        return itemDto;
    }

    @GetMapping("/search")
    public List<ItemDto> searchItems(@RequestParam String text) {
        List<Item> listItems = itemService.searchItems(text);
        return getListItemsDto(listItems);
    }

    @GetMapping("/{itemId}")
    public ItemDto getItemById(@PathVariable long itemId) {
        Item item = itemService.findById(itemId);
        return itemMapper.toDto(item);
    }

    @GetMapping
    public List<ItemDto> getItemsByOwnerId(@RequestHeader("X-Sharer-User-Id") long userId) {
        List<Item> listItems = itemService.findItemsByOwnerId(userId);
        return getListItemsDto(listItems);
    }

    private List<ItemDto> getListItemsDto(List<Item> listItems) {
        List<ItemDto> listItemsDto = new ArrayList<>();
        for (Item item : listItems) {
            ItemDto itemDto = itemMapper.toDto(item);
            listItemsDto.add(itemDto);
        }
        return listItemsDto;
    }
}
