package ru.practicum.shareit.user;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.error.model.EmailDuplicatedFound;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class UserDaoImplInMemory implements UserDao {
    private final Map<Long, User> userMap = new HashMap<>();
    private final Map<String, Long> emailMap = new HashMap<>();
    private long idCount = 0;

    public User add(User user) {
        long id = user.setId(++idCount);
        userMap.put(id, user);
        emailMap.put(user.getEmail(), id);
        return userMap.get(id);
    }

    public User update(long id, User user) {
        User oldUser = userMap.get(id);
        String oldName = oldUser.getName();
        String oldEmail = oldUser.getEmail();
        String newEmail = user.getEmail();
        if (newEmail != null) {
            if (emailMap.containsKey(newEmail)) {
                if (emailMap.get(newEmail) != id) {
                    throw new EmailDuplicatedFound("при обновлении пользователя дублируется email");
                }
            }
            emailMap.remove(oldEmail);
            emailMap.put(user.getEmail(), id);
        } else {
            user.setEmail(oldEmail);
        }
        if (user.getName() == null) {
            user.setName(oldName);
        }
        user.setId(id);
        userMap.put(id, user);
        return userMap.get(id);
    }

    public User get(long id) {
        return userMap.get(id);
    }

    @Override
    public List<User> getAll() {
        return new ArrayList<>(userMap.values());
    }

    public void delete(long id) {
        emailMap.remove(userMap.get(id).getEmail());
        userMap.remove(id);
    }

    public boolean isContains(long id) {
        return userMap.containsKey(id);
    }

    public boolean isEmailDuplicate(String email) {
        return emailMap.containsKey(email);
    }
}
