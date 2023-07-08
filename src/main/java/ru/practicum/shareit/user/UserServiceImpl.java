package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.error.model.EntityNotFoundException;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    public UserDto add(UserDto userDto) {
        User userToSave = userMapper.fromDto(userDto);
        User userAdded = userRepository.save(userToSave);
        System.out.println(userAdded.getEmail() + " " + userAdded.getId());
        return userMapper.toDto(userAdded);
    }

    @Override
    public UserDto patch(long id, UserDto userDto) {
        final User user = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Пользователь не найден c id=" + id));

        final Optional<String> userEmailOptional = Optional.ofNullable(userDto.getEmail());
        final Optional<String> userNameOptional = Optional.ofNullable(userDto.getName());

        userEmailOptional.ifPresent(email -> {
            if (!email.isBlank()) {
                user.setEmail(email);
            }
        });

        userNameOptional.ifPresent(name -> {
            if (!name.isBlank()) {
                user.setName(name);
            }
        });

        User savedUser = userRepository.save(user);
        return userMapper.toDto(savedUser);
    }

    @Override
    public UserDto findById(long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Пользователь не найден c id=" + id));
        return userMapper.toDto(user);
    }

    @Override
    public List<UserDto> getAll() {
        List<User> userList = userRepository.findAll();
        return userList.stream().map(UserMapper::toDto).collect(Collectors.toList());
    }

    @Override
    public void delete(long id) {
        userRepository.deleteById(id);
    }

    @Override
    public boolean isContains(long id) {
        return userRepository.existsById(id);
    }


    // TODO этот метод для "своих" (мб спец дто делать надо)
    @Override
    public User findUser(long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Пользователь не найден c id=" + id));
    }
}
