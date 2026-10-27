package ru.practicum.shareit.item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
public class ItemRepositoryTest {

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private UserRepository userRepository;

    private User owner;

    @BeforeEach
    void setUp() {
        owner = new User();
        owner.setName("Mr.Owner");
        owner.setEmail("mrowner@test.com");
        owner = userRepository.save(owner);

        createItem("ЯндексФон Я2", "Смартфон/телефон");
        createItem("ГрушеФон 100500", "Больше чем смартфон(по мнению многих)");
        createItem("Xstaion", "Игровая приставка/консоль");
    }

    private void createItem(String name, String description) {
        Item item = new Item();
        item.setName(name);
        item.setDescription(description);
        item.setOwner(owner);
        item.setAvailable(true);

        itemRepository.save(item);
    }

    @Test
    void findByName() {
        List<Item> result = itemRepository.search("грушефон");

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo("ГрушеФон 100500");
    }

    @Test
    void findByDescription() {
        List<Item> result = itemRepository.search("приставка");

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo("Xstaion");
    }

    @Test
    void returnEmptyWhenNoMatch() {
        List<Item> result = itemRepository.search("комод");

        assertThat(result).isEmpty();
    }
}
