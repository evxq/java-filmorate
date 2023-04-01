package ru.yandex.practicum.filmorate.dao;

import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.List;
import java.util.Set;

public interface FriendsDao {

    void addFriendSetToUser(Integer userId, Set<Integer> friends);

    Collection<Integer> getUserFriendIdSet(Integer userId);

    void addToFriends(Integer userId, Integer friendId);

    void deleteFromFriends(Integer userId, Integer friendId);

    List<User> getUserFriendList(Integer userId);

    List<User> getCommonFriends(Integer userId, Integer otherUserId);

}
