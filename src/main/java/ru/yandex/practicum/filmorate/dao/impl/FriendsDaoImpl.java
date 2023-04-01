package ru.yandex.practicum.filmorate.dao.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dao.FriendsDao;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

@Component
@Primary
@Slf4j
public class FriendsDaoImpl implements FriendsDao {

    private final JdbcTemplate jdbcTemplate;

    public FriendsDaoImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void addFriendSetToUser(Integer userId, Set<Integer> friends) {
        for (Integer friend : friends) {
            String sqlQuery = "INSERT INTO friends (user_id, friend_id)" +
                    "VALUES (?, ?)";
            try {
                jdbcTemplate.update(sqlQuery, userId, friend);
                log.debug("Пользователю id = {} присвоен друг id = {}", userId, friend);
            } catch (DataIntegrityViolationException e) {
                throw new NotFoundException("Некорректный id пользователя");
            }
        }
    }

    @Override
    public Collection<Integer> getUserFriendIdSet(Integer userId) {
        String sqlQuery = "SELECT friend_id FROM friends WHERE user_id = ?";
        log.debug("Вызван список друзей пользователя id = {}", userId);

        return jdbcTemplate.query(sqlQuery, (rs, rowNUm) -> getFriendId(rs), userId);
    }

    private Integer getFriendId(ResultSet rs) throws SQLException {
        return rs.getInt("friend_id");
    }

    @Override
    public void addToFriends(Integer userId, Integer friendId) {
        try {
            String sqlQuery = "INSERT INTO friends (user_id, friend_id)" +
                    "VALUES (?, ?)";
            jdbcTemplate.update(sqlQuery, userId, friendId);
        } catch (DataAccessException e) {
            log.warn("При добавлении друга вызван некорректный пользователь");
            throw new NotFoundException("При добавлении друга вызван некорректный пользователь");
        }
    }

    @Override
    public void deleteFromFriends(Integer userId, Integer friendId) {
        String sqlQuery = "DELETE FROM friends WHERE user_id = ? AND friend_id = ?";
        jdbcTemplate.update(sqlQuery, userId, friendId);
    }

    @Override
    public List<User> getUserFriendList(Integer userId) {
        String sqlQuery = "SELECT * FROM users WHERE user_id IN " +
                "(SELECT friend_id FROM friends WHERE user_id = ?)";
        return jdbcTemplate.query(sqlQuery, this::makeUser, userId);
    }

    @Override
    public List<User> getCommonFriends(Integer userId, Integer otherUserId) {
        List<User> common = new ArrayList<>();
        for (User user : getUserFriendList(userId)) {
            if (getUserFriendList(otherUserId).contains(user)) {
                common.add(user);
            }
        }
        return common;
    }

    private User makeUser(ResultSet rs, int rowNum) throws SQLException {
        Integer userId = rs.getInt("user_id");
        String email = rs.getString("email");
        String login = rs.getString("login");
        String name = rs.getString("name");
        LocalDate birthday = rs.getDate("birthday").toLocalDate();
        User user = new User(email, login, birthday);
        user.setId(userId);
        user.setName(name);
        user.getFriends().addAll(getUserFriendIdSet(userId));

        return user;
    }

}
