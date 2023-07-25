package ru.practicum.shareit.user;


import java.util.List;

public interface UserService {

    public UserDto add(UserDto userDto);

    public UserDto patch(long id, UserDto userDto);

    public UserDto findById(long id);

    public List<UserDto> getAll();

    public void delete(long id);

    public boolean isContains(long id);

//    public User findUser(long id);
}
