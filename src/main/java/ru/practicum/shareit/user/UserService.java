package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.error.model.EmailDuplicatedFound;
import ru.practicum.shareit.error.model.EntityNotFoundException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.model.UserDto;
import ru.practicum.shareit.user.model.UserMapper;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserDao userDao;
    private final UserMapper userMapper;

    public UserDto add(UserDto userDto) {
        User user = userMapper.fromDto(userDto);
        if (userDao.isEmailDuplicate(user.getEmail())) {
            throw new EmailDuplicatedFound("при создании нового пользователя дублируется email" + user.getEmail());
        }
        return userMapper.toDto(userDao.add(user));
    }

    public UserDto update(long id, UserDto userDto) {
        if (!userDao.isContains(id)) {
            throw new EntityNotFoundException("Пользователь не найден c id=" + id);
        }
        User user = userMapper.fromDto(userDto);
        return userMapper.toDto(userDao.update(id, user));
    }

    public UserDto get(long id) {
        if (!userDao.isContains(id)) {
            throw new EntityNotFoundException("Пользователь не найден c id=" + id);
        }
        return userMapper.toDto(userDao.get(id));
    }

    public List<UserDto> getAll() {
        return userDao.getAll().stream().map(userMapper::toDto).collect(Collectors.toList());
    }

    public void delete(long id) {
        userDao.delete(id);
    }

    public boolean isContains(long id) {
        return userDao.isContains(id);
    }
}
