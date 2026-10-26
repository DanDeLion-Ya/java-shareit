package ru.practicum.shareit.item;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

@Repository
public interface ItemRepository extends JpaRepository<Item, Long> {
    List<Item> findByOwnerId(Long ownerId);

@Query("SELECT it FROM Item AS it WHERE (LOWER(it.name) LIKE LOWER(CONCAT('%', ?1, '%')) " +
        "OR LOWER(it.description) LIKE LOWER(CONCAT('%', ?1, '%'))) " +
        "AND it.available = TRUE")
    List<Item> search(String text);
}