package ru.yandex.practicum.filmorate.storage.user;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

@Component
public class InMemoryUserStorage implements UserStorage {

    private int userId;
    private HashMap<Integer, User> userMap = new HashMap<>();

    @Override
    public User getUserById(Integer id) {
        return userMap.get(id);
    }

    @Override
    public List<User> getAllUsers() {
        return new ArrayList<>(userMap.values());
    }

    @Override
    public User createUser(User user) {
        userId++;
        user.setId(userId);
        userMap.put(userId, user);
        return user;
    }

    @Override
    public User updateUser(User user) {
        if (!userMap.containsKey(user.getId())) {
            return null;
        } else {
            userMap.put(user.getId(), user);
            return userMap.get(user.getId());
        }
    }

    public void addToFriends(Integer userId, Integer friendId) {
        User user = getUserById(userId);
        User friend = getUserById(friendId);
        user.getFriends().add(friendId);
        friend.getFriends().add(userId);
    }

    public void deleteFromFriends(Integer userId, Integer friendId) {
        User user = getUserById(userId);
        User friend = getUserById(friendId);
        if (!user.getFriends().contains(friendId)) {
            throw new ValidationException(String.format("Пользователь id =%d не является другом пользователя id =%d", userId, friendId));
        }
        user.getFriends().remove(friendId);
        friend.getFriends().remove(userId);
    }

    public List<User> getUserFriendList(Integer userId) {
        User user = getUserById(userId);
        ArrayList<User> userFriends = new ArrayList<>();
        Set<Integer> userFriendsIds = user.getFriends();
        for (Integer friendId : userFriendsIds) {
            userFriends.add(getUserById(friendId));
        }
        return userFriends;
    }

    public List<User> getCommonFriends(Integer userId, Integer otherUserId) {
        User user = getUserById(userId);
        User otherUser = getUserById(otherUserId);
        ArrayList<User> commonFriends = new ArrayList<>();
        Set<Integer> userFriends = user.getFriends();
        Set<Integer> otherUserFriends = otherUser.getFriends();
        for (Integer friendId : userFriends) {
            if (otherUserFriends.contains(friendId)) {
                commonFriends.add(getUserById(friendId));
            }
        }
        return commonFriends;
    }

}
