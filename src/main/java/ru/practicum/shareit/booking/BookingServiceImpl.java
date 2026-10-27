package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.ForbiddenException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {
    private final BookingMapper bookingMapper;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final BookingRepository bookingRepository;

    @Override
    public BookingDto createBooking(Long userId, BookingCreateDto bookingCreateDto) {
        log.info("Запрос на создание бронирования пользователем с Id = {}", userId);
        User booker = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с Id = " + userId + " не найден"));
        Item item = itemRepository.findById(bookingCreateDto.getItemId())
                .orElseThrow(() -> new NotFoundException("Вещь с Id = " + bookingCreateDto.getItemId() + " не найдена"));

        if (item.getOwner().getId().equals(userId)) {
            log.warn("Владелец пытается забронировать свою же вещь");
            throw new NotFoundException("Владелец не может бронировать свою вещь");
        }

        if (!item.isAvailable()) {
            log.warn("Данная вещь недоступна");
            throw new BadRequestException("Вещь недоступна для бронирования");
        }

        if (bookingCreateDto.getStart().isBefore(LocalDateTime.now())) {
            log.warn("Указана дата начала брони до текущего момента времени");
            throw new BadRequestException("Дата начала не может быть раньше текущего момента времени");
        }

        if (bookingCreateDto.getStart().isAfter(bookingCreateDto.getEnd())) {
            log.warn("Дата начала брони указана после даты конца брони");
            throw new BadRequestException("Дата начала должна быть раньше окончания");
        }

        Booking booking = Booking.builder()
                .start(bookingCreateDto.getStart())
                .end(bookingCreateDto.getEnd())
                .item(item)
                .booker(booker)
                .status(Status.WAITING)
                .build();

        Booking savedBooking = bookingRepository.save(booking);
        log.info("Создано бронирование с Id = {}", savedBooking.getId());
        return bookingMapper.toDto(savedBooking);
    }

    @Override
    public BookingDto approveBooking(Long userId, Long bookingId, Boolean approved) {
        log.info("Запрос на одобрение бронирования с Id = {} пользователем с Id = {}", bookingId, userId);

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Бронирование c Id = " + bookingId + " не найдено"));

        if (!booking.getItem().getOwner().getId().equals(userId)) {
            log.warn("Пользователь пытается изменить статус брони");
            throw new ForbiddenException("Статус брони хочет изменить не владелец");
        }

        User booker = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с Id = " + userId + " не найден"));

        if (booking.getStatus() != Status.WAITING) {
            log.warn("Статус бронирования уже обработан");
            throw new BadRequestException("Статус бронирования уже обработан");
        }

        if (approved) {
            booking.setStatus(Status.APPROVED);
            log.info("Статус бронирования изменён на 'ОДОБРЕН'");
        } else {
            booking.setStatus(Status.REJECTED);
            log.info("Статус бронирования изменён на 'ОТКЛОНЁН'");
        }

        Booking savedBooking = bookingRepository.save(booking);
        log.info("Статус бронирования изменён на {}", savedBooking.getStatus());
        return bookingMapper.toDto(savedBooking);
    }

    @Override
    public BookingDto getBookingById(Long userId, Long bookingId) {
        log.info("Запрос на получение брони с Id = {} пользователем с Id = {}", bookingId, userId);
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Бронирование c Id = " + bookingId + " не найдено"));

        if (!booking.getItem().getOwner().getId().equals(userId) &&
                !booking.getBooker().getId().equals(userId)) {
            log.warn("Информацию о бронировании может только владелец вещи, либо автор брони");
            throw new NotFoundException("Информацию о бронировании запрашивает не владелец и не автор брони");
        }
        return bookingMapper.toDto(booking);
    }

    @Override
    public List<BookingDto> getAllBookingsByUser(Long userId, State state) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("Пользователь с Id = " + userId + " не найден");
        }

        if (state == null) {
            state = State.ALL;
        }

        Sort sort = Sort.by(Sort.Direction.DESC, "start");
        LocalDateTime now = LocalDateTime.now();

        List<Booking> bookings = new ArrayList<>();
        switch (state) {
            case ALL:
                bookings = bookingRepository.findByBooker_Id(userId, sort);
                break;
            case CURRENT:
                bookings = bookingRepository.findCurrentBookingsByBooker(userId, now, sort);
                break;
            case PAST:
                bookings = bookingRepository.findByBooker_IdAndEndIsBefore(userId, now, sort);
                break;
            case FUTURE:
                bookings = bookingRepository.findByBooker_IdAndStartIsAfter(userId, now, sort);
                break;
            case WAITING:
                bookings = bookingRepository.findByBooker_IdAndStatus(userId, Status.WAITING, sort);
                break;
            case REJECTED:
                bookings = bookingRepository.findByBooker_IdAndStatus(userId, Status.REJECTED, sort);
                break;
        }

        return bookings.stream()
                .map(booking -> bookingMapper.toDto(booking))
                .toList();
    }

    @Override
    public List<BookingDto> getAllBookingsByOwner(Long ownerId, State state) {
        if (!userRepository.existsById(ownerId)) {
            throw new NotFoundException("Пользователь с Id = " + ownerId + " не найден");
        }

        if (state == null) {
            state = State.ALL;
        }

        Sort sort = Sort.by(Sort.Direction.DESC, "start");
        LocalDateTime now = LocalDateTime.now();

        List<Booking> bookings = new ArrayList<>();
        switch (state) {
            case ALL:
                bookings = bookingRepository.findByItem_Owner_Id(ownerId, sort);
                break;
            case CURRENT:
                bookings = bookingRepository.findCurrentBookingsByOwner(ownerId, now, sort);
                break;
            case PAST:
                bookings = bookingRepository.findByItem_Owner_IdAndEndIsBefore(ownerId, now, sort);
                break;
            case FUTURE:
                bookings = bookingRepository.findByItem_Owner_IdAndStartIsAfter(ownerId, now, sort);
                break;
            case WAITING:
                bookings = bookingRepository.findByItem_Owner_IdAndStatus(ownerId, Status.WAITING, sort);
                break;
            case REJECTED:
                bookings = bookingRepository.findByItem_Owner_IdAndStatus(ownerId, Status.REJECTED, sort);
                break;
        }

        return bookings.stream()
                .map(booking -> bookingMapper.toDto(booking))
                .toList();
    }
}
