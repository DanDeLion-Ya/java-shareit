package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.Status;
import ru.practicum.shareit.booking.dto.BookingMiniDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;
    private final ItemMapper itemMapper;

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
    public ItemDto findById(Long itemId, Long userId) {
        log.info("Запрос на поиск предмета c Id = {}", itemId);
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Предмет с Id = " + itemId + " не найден"));

        ItemDto itemDto = itemMapper.toDto(item);

        List<CommentDto> comments = commentRepository.findByItem_Id(itemId).stream()
                .map(c -> new CommentDto(c.getId(), c.getText(), c.getAuthor().getName(), c.getCreated()))
                .toList();
        itemDto.setComments(comments);

        if (item.getOwner().getId().equals(userId)) {
            LocalDateTime now = LocalDateTime.now();

            bookingRepository.findLastBooking(itemId, Status.APPROVED, now)
                    .ifPresent(b -> itemDto.setLastBooking(new BookingMiniDto(b.getId(),
                            b.getBooker().getId())));

            bookingRepository.findNextBooking(itemId, Status.APPROVED, now)
                    .ifPresent(b -> itemDto.setNextBooking(new BookingMiniDto(b.getId(),
                            b.getBooker().getId())));
        }

        log.info("Предмет c Id = {} найден", itemId);
        return itemDto;
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
    public List<ItemDto> searchItems(String text) {
        log.info("Запрос на поиск предмета по тексту: {}", text);
        if (text == null || text.isBlank()) {
            return List.of();
        }
        return itemRepository.search(text).stream()
                .map(item -> itemMapper.toDto(item))
                .toList();
    }

    @Override
    public List<ItemOwnerDto> findItemsByOwnerId(Long userId) {
        log.info("Запрос на список предметов пользователя c Id = {}", userId);
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("Пользователь с Id = " + userId + " не найден");
        }

        List<Item> items = itemRepository.findByOwnerId(userId);
        LocalDateTime now = LocalDateTime.now();

        log.info("Получен список предметов пользователя с Id: {}", userId);
        return items.stream()
                .map(item -> {
                    ItemOwnerDto dto = new ItemOwnerDto();
                    dto.setId(item.getId());
                    dto.setName(item.getName());
                    dto.setDescription(item.getDescription());
                    dto.setAvailable(item.isAvailable());

                    bookingRepository.findLastBooking(item.getId(), Status.APPROVED, now)
                            .ifPresent(b -> dto.setLastBooking(new BookingMiniDto(b.getId(), b.getBooker().getId())));

                    bookingRepository.findNextBooking(item.getId(), Status.APPROVED, now)
                            .ifPresent(b -> dto.setNextBooking(new BookingMiniDto(b.getId(), b.getBooker().getId())));

                    List<CommentDto> comments = commentRepository.findByItem_Id(item.getId()).stream()
                            .map(c -> new CommentDto(c.getId(), c.getText(), c.getAuthor().getName(), c.getCreated()))
                            .toList();
                    dto.setComments(comments);

                    return dto;
                    })
                .toList();
    }

    @Override
    public CommentDto addComment(Long userId, Long itemId, CommentTextDto text) {
        log.info("Запрос на добавление отзыва о предмете c Id = {} пользователем с Id = {}", itemId, userId);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с Id = " + userId + " не найден"));
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Предмет с Id = " + itemId + " не найден"));
        List<Booking> bookings = bookingRepository.findCompletedBookings(itemId, userId, LocalDateTime.now());

        if (bookings.isEmpty()) {
            log.warn("Ни одной брони не найдено");
            throw new BadRequestException("Пользователь с Id = " + userId +
                    " не бронировал ранее вещь с Id = " + itemId);
        }

        Comment comment = Comment.builder()
                .text(text.getText())
                .author(user)
                .item(item)
                .created(LocalDateTime.now())
                .build();

        Comment savedComment = commentRepository.save(comment);
        CommentDto commentDto = new CommentDto();
        commentDto.setId(savedComment.getId());
        commentDto.setText(savedComment.getText());
        commentDto.setAuthorName(savedComment.getAuthor().getName());
        commentDto.setCreated(savedComment.getCreated());

        log.info("Добавление отзыва о предмете c Id = {} пользователем с Id = {}", itemId, userId);
        return commentDto;
    }
}
