package ru.practicum.shareit.item.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.booking.dto.BookingMiniDto;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ItemOwnerDto {
    private Long id;

    @NotBlank
    private String name;

    @NotBlank
    private String description;

    @NotNull
    private Boolean available;

    private BookingMiniDto lastBooking;

    private BookingMiniDto nextBooking;

    private List<CommentDto> comments;
}
