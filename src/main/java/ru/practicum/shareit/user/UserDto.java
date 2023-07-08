package ru.practicum.shareit.user;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Builder
@Data
public class UserDto {
    private long id;
    @NotEmpty
    @NotBlank
    private String name;
    @Email
    @NotBlank
    @NotNull
    private String email;
}