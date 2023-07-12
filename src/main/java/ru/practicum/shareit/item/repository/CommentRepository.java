package ru.practicum.shareit.item.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.item.model.Comment;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    public List<Comment> findAllByItemId(long id);

    public List<Comment> findAllByItemIdIn(List<Long> id);

    public List<Comment> findAllByUserId(long id);
}
