package ru.practicum.shareit.item;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;

@Builder
@Data
public class CommentDto {
    private long id;

    @NotBlank
    private String text;
    private String authorName;
    private LocalDateTime created;
}
