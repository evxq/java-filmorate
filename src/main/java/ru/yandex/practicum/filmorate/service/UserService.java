package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.FriendsDao;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserService {

    private final FriendsDao friendsDao;
    private final UserStorage userStorage;

    public User getUserById(Integer id) {
        User user = userStorage.getUserById(id);
        if (user == null) {
            String errorMessage = "Такой пользователь отсутствует";
            log.debug(errorMessage);
            throw new NotFoundException(errorMessage);
        }
        return user;
    }

    public List<User> getAllUsers() {
        log.debug("Вызов списка всех пользователей");
        return userStorage.getAllUsers();
    }

    public User createUser(User user) {
        if (user == null) {
            String errorMessage = "Данные пользователя не переданы";
            log.debug(errorMessage);
            throw new ValidationException(errorMessage);
        }
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
            log.debug("Пользователю {} присвоено имя как логин", user.getLogin());
        }
        User newUser = userStorage.createUser(user);
        log.debug("Добавлен новый пользователь {}, присвоен id = {}", newUser.getLogin(), newUser.getId());
        return newUser;
    }

    public User updateUser(User user) {
        User updUser = userStorage.updateUser(user);
        if (updUser == null) {
            String errorMessage = "Такой пользователь отсутствует";
            log.debug(errorMessage);
            throw new NotFoundException(errorMessage);
        } else {
            log.debug("Обновлены данные пользователя {}, id = {}", updUser.getLogin(), updUser.getId());
            return updUser;
        }
    }

    public void addToFriends(Integer userId, Integer friendId) {
        friendsDao.addToFriends(userId, friendId);
        log.info("Пользователь id = {} добавил друга id = {}", userId, friendId);
    }

    public void deleteFromFriends(Integer userId, Integer friendId) {
        friendsDao.deleteFromFriends(userId, friendId);
        log.debug("Пользователь id = {} удалил друга id = {}", userId, friendId);
    }

    public List<User> getUserFriendList(Integer userId) {
        log.debug("Получен список друзей для пользователя id = {}", userId);
        return friendsDao.getUserFriendList(userId);
    }

    public List<User> getCommonFriends(Integer userId, Integer otherUserId) {
        log.debug("Получен список общих друзей для пользователей id = {} и id = {}", userId, otherUserId);
        return friendsDao.getCommonFriends(userId, otherUserId);
    }

}
