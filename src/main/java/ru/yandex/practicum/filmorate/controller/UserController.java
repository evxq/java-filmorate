package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final UserService userService;

    @GetMapping
    public List<User> getAllUsers() {
        return userService.getAllUsers();
    }

    @PostMapping
    public User createUser(@RequestBody User user) {
        validateUser(user);
        return userService.createUser(user);
    }

    @PutMapping
    public User updateUser(@RequestBody User user) {
        validateUser(user);
        return userService.updateUser(user);
    }

    @GetMapping("/{id}")                                                  // получить данные пользователя по id
    public User getUserById(@PathVariable Integer id) {
        return userService.getUserById(id);
    }

    @PutMapping("/{id}/friends/{friendId}")                               // добавление в друзья
    public void addToFriends(@PathVariable Integer id, @PathVariable Integer friendId) {
        userService.addToFriends(id, friendId);
    }

    @DeleteMapping("/{id}/friends/{friendId}")                             // удаление из друзей
    public void deleteFromFriends(@PathVariable Integer id, @PathVariable Integer friendId) {
        userService.deleteFromFriends(id, friendId);
    }

    @GetMapping("/{id}/friends")                                           // список друзей пользователя
    public List<User> getUserFriendList(@PathVariable Integer id) {
        return userService.getUserFriendList(id);
    }

    @GetMapping("/{id}/friends/common/{otherId}")
    // список друзей, общих с другим пользователем
    public List<User> getCommonFriends(@PathVariable Integer id, @PathVariable Integer otherId) {
        return userService.getCommonFriends(id, otherId);
    }

    private void validateUser(User user) {
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
        if (user.getBirthday().isAfter(LocalDate.now())) {
            errorMessage = "Дата рождения некорректна";
            log.debug(errorMessage);
            throw new ValidationException(errorMessage);
        }
    }

}
