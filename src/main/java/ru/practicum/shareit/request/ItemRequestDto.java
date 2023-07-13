package ru.practicum.shareit.request;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;

@Data
@Builder
public class ItemRequestDto {
    private Long id;
    @NotBlank
    private String description;
    private Long requesterID;
    private LocalDateTime created;
}
