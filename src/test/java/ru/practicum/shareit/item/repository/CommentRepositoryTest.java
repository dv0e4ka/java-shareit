package ru.practicum.shareit.item.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

@DataJpaTest
class CommentRepositoryTest {

    @Autowired
    CommentRepository commentRepository;
    @Autowired
    UserRepository userRepository;
    @Autowired
    ItemRepository itemRepository;

    User user;
    Item item;
    Comment comment;

    @BeforeEach
    void setUp() {
        user = User.builder().name("name").email("name@mail.ru").build();
        item = Item.builder().name("name").description("description").available(true).owner(user).build();
        comment = Comment.builder().text(" ").user(user).item(item).build();

        userRepository.save(user);
        itemRepository.save(item);
        commentRepository.save(comment);
    }

    @Test
    void findAllByItemId() {
        List<Comment> commentList = commentRepository.findAllByItemId(item.getId());
        Comment actual = commentList.get(0);

        assertThat(actual.getId(), notNullValue());
        assertThat(actual.getText(), equalTo(comment.getText()));
        assertThat(commentList.size(), equalTo(1));
    }
}