package ru.practicum.shareit.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.error.model.EmailDuplicatedFound;
import ru.practicum.shareit.error.model.EntityNotFoundException;
import ru.practicum.shareit.user.model.User;

import java.util.List;

@Service
public class UserService {

    private final UserDao userDao;

    @Autowired
    public UserService(UserDao userDao) {
        this.userDao = userDao;
    }

    public User add(User user) {
        if (userDao.isEmailDuplicate(user.getEmail())) {
            throw new EmailDuplicatedFound("при создании нового пользователя дублируется email" + user.getEmail());
        }
        return userDao.add(user);
    }

    public User update(long id, User user) {
        if (!userDao.isContains(id)) {
            throw new EntityNotFoundException("Пользователь не найден c id=" + id);
        }
        return userDao.update(id, user);
    }

    public User get(long id) {
        if (!userDao.isContains(id)) {
            throw new EntityNotFoundException("Пользователь не найден c id=" + id);
        }
        return userDao.get(id);
    }

    public List<User> getAll() {
        return userDao.getAll();
    }

    public void delete(long id) {
        userDao.delete(id);
    }

    public boolean isContains(long id) {
        return userDao.isContains(id);
    }
}
