package ru.practicum.shareit.item;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
@Builder
public class ItemDto {
    private Long id;
    @NotEmpty
    @NotBlank
    private String name;
    @NotBlank
    private String description;
    private long owner;
    @NotNull
    private Boolean available;

}
