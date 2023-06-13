package ru.practicum.shareit.user;

import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface UserDao {

    public User add(User user);

    public User update(long id, User user);

    public User get(long id);

    public List<User> getAll();

    public void delete(long id);

    public boolean isContains(long id);

    public boolean isEmailDuplicate(String email);
}
