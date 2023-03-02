package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserService {

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
        log.debug("Общее количество пользователей: {}", userStorage.getAllUsers().size());
        return userStorage.getAllUsers();
    }

    public User createUser(User user) {
        int newId = userStorage.getUserId() + 1;
        userStorage.setUserId(newId);
        user.setId(newId);
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
            log.debug("Имя присвоено как логин");
        }
        userStorage.createUser(user);
        log.debug("Добавлен новый пользователь {}, присвоен id={}", user.getLogin(), user.getId());
        return user;
    }

    public User updateUser(User user) {
        User updUser = userStorage.updateUser(user);
        if (updUser == null) {
            String errorMessage = "Такой пользователь отсутствует";
            log.debug(errorMessage);
            throw new NotFoundException(errorMessage);
        } else {
            log.debug("Обновлен пользователь: {}", user.getLogin());
            return updUser;
        }
    }

    public void addToFriends(Integer userId, Integer friendId) {
        User user = getUserById(userId);
        User friend = getUserById(friendId);
        user.getFriends().add(friendId);
        friend.getFriends().add(userId);
        log.debug("Пользователь id={} добавил друга id={}", userId, friendId);
    }

    public void deleteFromFriends(Integer userId, Integer friendId) {
        User user = getUserById(userId);
        User friend = getUserById(friendId);
          if (!user.getFriends().contains(friendId)) {
              log.debug("Пользователь id={} не является другом пользователя id={}", userId, friendId);
              throw new ValidationException(String.format("Пользователь id=%d не является другом пользователя id=%d", userId, friendId));
        }
        user.getFriends().remove(friendId);
        friend.getFriends().remove(userId);
        log.debug("Пользователь id={} удалил друга id={}", userId, friendId);
    }

    public List<User> getUserFriends(Integer userId) {
        User user = getUserById(userId);
        ArrayList<User> userFriends = new ArrayList<>();
        Set<Integer> userFriendsIds = user.getFriends();
        for (Integer friendId: userFriendsIds) {
            userFriends.add(getUserById(friendId));
        }
        log.debug("Получен список друзей для пользователя id={}", userId);
        return userFriends;
    }

    public List<User> getCommonFriends(Integer userId, Integer otherUserId) {
        User user = getUserById(userId);
        User otherUser = getUserById(otherUserId);
        ArrayList<User> commonFriends = new ArrayList<>();
        Set<Integer> userFriends = user.getFriends();
        Set<Integer> otherUserFriends = otherUser.getFriends();
        for (Integer friendId: userFriends) {
            if (otherUserFriends.contains(friendId)) {
                commonFriends.add(getUserById(friendId));
            }
        }
        log.debug("Получен список общих друзей для пользователей id={} и id={}", userId, otherUserId);
        return commonFriends;
    }

}
