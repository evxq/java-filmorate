package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class UserService {

    private final UserStorage userStorage;

    @Autowired
    public UserService(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public void addToFriends(Integer userId, Integer friendId) {
        validateUserById(userId);
        validateUserById(friendId);
        userStorage.getUserById(userId).addFriend(friendId);
        userStorage.getUserById(friendId).addFriend(userId);
        log.debug("Пользователь id={} добавил друга id={}", userId, friendId);
    }

    public void deleteFromFriends(Integer userId, Integer friendId) {
        validateUserById(userId);
        validateUserById(friendId);
        if (!userStorage.getUserById(userId).getFriends().contains(friendId)) {
            log.debug("Пользователь id={} не является другом пользователя id={}", userId, friendId);
            throw new ValidationException(String.format("Пользователь id=%d не является другом пользователя id=%d", userId, friendId));
        }
        userStorage.getUserById(userId).deleteFriend(friendId);
        userStorage.getUserById(friendId).deleteFriend(userId);
        log.debug("Пользователь id={} удалил друга id={}", userId, friendId);
    }

    public List<User> getUserFriends(Integer userId) {
        validateUserById(userId);
        ArrayList<User> userFriends = new ArrayList<>();
        for (Integer friendId: userStorage.getUserById(userId).getFriends()) {
            userFriends.add(userStorage.getUserById(friendId));
        }
        log.debug("Получен список друзей для пользователя id={}", userId);
        return userFriends;
    }

    public List<User> getCommonFriends(Integer userId, Integer otherUserId) {
        validateUserById(userId);
        validateUserById(otherUserId);
        ArrayList<User> commonFriends = new ArrayList<>();
        for (Integer friendId: userStorage.getUserById(userId).getFriends()) {
            if (userStorage.getUserById(otherUserId).getFriends().contains(friendId)) {
                commonFriends.add(userStorage.getUserById(friendId));
            }
        }
        log.debug("Получен список общих друзей для пользователей id={} и id={}", userId, otherUserId);
        return commonFriends;
    }

    private void validateUserById(int id) {
        try {
            userStorage.getUserById(id);
        } catch (NotFoundException n) {
            log.debug("Пользователь id={} не найден", id);
            throw new NotFoundException(String.format("Пользователь id=%d не найден", id));
        }
    }

}
