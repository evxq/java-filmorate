package ru.yandex.practicum.filmorate.storage.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Component
@Slf4j
public class InMemoryUserStorage implements UserStorage {

    private int userId;
    private HashMap<Integer, User> userMap = new HashMap<>();

    @Override
    public User getUserById(Integer id) {
        if (!userMap.containsKey(id)) {
            String errorMessage = "Такой пользователь отсутствует";
            log.debug(errorMessage);
            throw new NotFoundException(errorMessage);
        }
        return userMap.get(id);
    }

    @Override
    public List<User> getAllUsers() {
        log.debug("Общее количество пользователей: {}", userMap.size());
        return new ArrayList<>(userMap.values());
    }

    @Override
    public User createUser(User user) {
        validateUser(user);
        userId++;
        user.setId(userId);
        userMap.put(userId, user);
        log.debug("Добавлен новый пользователь {}", user.getLogin());
        return user;
    }

    @Override
    public User updateUser(User user) {
        if (!userMap.containsKey(user.getId()) ) {
            String errorMessage = "Такой пользователь отсутствует";
            log.debug(errorMessage);
            throw new NotFoundException(errorMessage);
        } else {
            validateUser(user);
            userMap.put(user.getId(), user);
            log.debug("Обновлен пользователь: {}", user.getLogin());
            return user;
        }
    }

    @Override
    public void validateUser(User user) {
        String errorMessage;
        if (user == null) {
            errorMessage = "Данные пользователя не переданы";
            log.debug(errorMessage);
            throw new ValidationException(errorMessage);
        }

        if (user.getEmail() == null || user.getEmail().isBlank() || !user.getEmail().contains("@")) {
            errorMessage = "Адрес почты не указан или некорректен";
            log.debug(errorMessage);
            throw new ValidationException(errorMessage);
        }
        if (user.getLogin() == null || user.getLogin().isBlank()) {
            errorMessage = "Логин не указан или некорректен";
            log.debug(errorMessage);
            throw new ValidationException(errorMessage);
        }
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
            log.debug("Имя присвоено как логин");
        }
        if (user.getBirthday().isAfter(LocalDate.now())) {
            errorMessage = "Дата рождения некорректна";
            log.debug(errorMessage);
            throw new ValidationException(errorMessage);
        }
    }

}
