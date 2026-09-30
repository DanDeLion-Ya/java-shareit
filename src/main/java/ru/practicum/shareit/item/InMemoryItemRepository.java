package ru.practicum.shareit.item;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class InMemoryItemRepository implements ItemRepository {
    private final Map<Long, Item> items = new HashMap<>();
    private long count = 1L;

    @Override
    public Item add(Item item) {
        long newId = count++;
        item.setId(newId);
        items.put(item.getId(), item);
        return item;
    }

    @Override
    public Item findById(long itemId) {
        return items.get(itemId);
    }

    @Override
    public List<Item> findAll() {
        return new ArrayList<>(items.values());
    }

    @Override
    public Item update(Item item) {
        if (item.getId() == 0 || !items.containsKey(item.getId())) {
            return null;
        }
        items.put(item.getId(), item);
        return item;
    }

    @Override
    public List<Item> search(String text) {
        List<Item> itemList = new ArrayList<>();
        if (text == null || text.isBlank()) {
            return itemList;
        }
        String query = text.toLowerCase().trim();

        for (Item item : items.values()) {
            if (((item.getName() != null && item.getName().toLowerCase().contains(query)) ||
                    (item.getDescription() != null && item.getDescription().toLowerCase().contains(query))) &&
                    item.isAvailable()) {
                itemList.add(item);
            }
        }
        return itemList;
    }

    @Override
    public List<Item> findItemsByOwnerId(long ownerId) {
        List<Item> itemList = new ArrayList<>();
        for (Item item : items.values()) {
            if (item.getOwner().getId() == ownerId) {
                itemList.add(item);
            }
        }
        return itemList;
    }
}
