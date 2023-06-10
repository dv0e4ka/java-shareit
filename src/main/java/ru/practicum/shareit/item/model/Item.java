package ru.practicum.shareit.item.model;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

/**
 * TODO Sprint add-controllers.
 */

@Data
@Builder
public class Item {
    private Long id;
    @NotEmpty
    @NotBlank
    private String name;
    @NotBlank
    private String description;
    private long owner;
    @NotNull
    private Boolean available;


    public long setId(long id) {
        this.id = id;
        return id;
    }
}
