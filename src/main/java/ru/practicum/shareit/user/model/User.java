package ru.practicum.shareit.user.model;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

/**
 * TODO Sprint add-controllers.
 */
@Data
@Builder
public class User {
    private long id;
    @NotEmpty
    @NotBlank
    private String name;
    @Email
    @NotBlank
    @NotNull
    private String email;

    public long setId(long id) {
        this.id = id;
        return id;
    }
}
