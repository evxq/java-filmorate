package ru.yandex.practicum.filmorate.dao.impl;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.yandex.practicum.filmorate.dao.FriendsDao;
import ru.yandex.practicum.filmorate.dao.UserDbDao;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.time.Month;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class FriendsDaoImplTest {

    private final JdbcTemplate jdbcTemplate;
    private final FriendsDao friendsDao;
    private final UserDbDao userDbDao;
    User user1;
    User user2;
    User user3;

    @BeforeEach
    void createUsers() {
        jdbcTemplate.update("DELETE FROM friends");
        jdbcTemplate.update("DELETE FROM users");
        user1 = userDbDao.createUser(new User("user1@ya.ru", "user1", LocalDate.of(2000, Month.JANUARY, 1)));
        user2 = userDbDao.createUser(new User("user2@ya.ru", "user2", LocalDate.of(2001, Month.JANUARY, 1)));
        user3 = userDbDao.createUser(new User("user3@ya.ru", "user3", LocalDate.of(2002, Month.JANUARY, 1)));
    }

    @Test
    void addFriendSetToUser_returnSetSize() {
        friendsDao.addFriendSetToUser(user1.getId(), Set.of(user2.getId(), user3.getId()));

        Assertions.assertEquals(2, friendsDao.getUserFriendList(user1.getId()).size());
    }

    @Test
    void addFriendSetToUser_wrongUserId() {
        NotFoundException wrongUser = assertThrows(
                NotFoundException.class,
                () -> friendsDao.addFriendSetToUser(100, Set.of(user2.getId(), user3.getId()))
        );
        Assertions.assertEquals("Некорректный id пользователя", wrongUser.getMessage());
    }

    @Test
    void getUserFriendIdSet_returnSetSize() {
        friendsDao.addFriendSetToUser(user1.getId(), Set.of(user2.getId(), user3.getId()));

        Assertions.assertEquals(2, friendsDao.getUserFriendList(user1.getId()).size());
    }

    @Test
    void getUserFriendIdSet_noFriends() {
        Assertions.assertEquals(0, friendsDao.getUserFriendList(user1.getId()).size());
    }

    @Test
    void getUserFriendIdSet_wrongUserId() {
        Assertions.assertEquals(0, friendsDao.getUserFriendList(50).size());
    }

    @Test
    void addToFriends_returnListSize() {
        friendsDao.addToFriends(user1.getId(), user2.getId());
        friendsDao.addToFriends(user1.getId(), user3.getId());

        Assertions.assertEquals(2, friendsDao.getUserFriendList(user1.getId()).size());
        Assertions.assertEquals(user2.getId(), friendsDao.getUserFriendList(user1.getId()).get(0).getId());
        Assertions.assertEquals(user3.getId(), friendsDao.getUserFriendList(user1.getId()).get(1).getId());
    }

    @Test
    void addToFriends_wrongUserOrFriendId() {
        NotFoundException wrongUserId = assertThrows(
                NotFoundException.class,
                () -> friendsDao.addToFriends(100, user2.getId())
        );
        Assertions.assertEquals("При добавлении друга вызван некорректный пользователь", wrongUserId.getMessage());

        NotFoundException wrongFriendId = assertThrows(
                NotFoundException.class,
                () -> friendsDao.addToFriends(user1.getId(), 100)
        );
        Assertions.assertEquals("При добавлении друга вызван некорректный пользователь", wrongFriendId.getMessage());
    }

    @Test
    void deleteFromFriends_returnListSize() {
        friendsDao.addToFriends(user1.getId(), user2.getId());
        friendsDao.addToFriends(user1.getId(), user3.getId());
        friendsDao.deleteFromFriends(user1.getId(), user2.getId());

        Assertions.assertEquals(1, friendsDao.getUserFriendList(user1.getId()).size());
        Assertions.assertEquals(user3.getId(), friendsDao.getUserFriendList(user1.getId()).get(0).getId());
    }

    @Test
    void deleteFromFriends_wrongUserOrFriendId() {
        friendsDao.addToFriends(user1.getId(), user2.getId());
        friendsDao.deleteFromFriends(user1.getId(), 20);
        friendsDao.deleteFromFriends(50, 100);
    }

    @Test
    void getUserFriendList_returnListSize() {
        friendsDao.addToFriends(user1.getId(), user2.getId());
        friendsDao.addToFriends(user1.getId(), user3.getId());

        Assertions.assertEquals(2, friendsDao.getUserFriendList(user1.getId()).size());
        Assertions.assertEquals(user2.getId(), friendsDao.getUserFriendList(user1.getId()).get(0).getId());
        Assertions.assertEquals(user3.getId(), friendsDao.getUserFriendList(user1.getId()).get(1).getId());
    }

    @Test
    void getUserFriendList_noFriends() {
        Assertions.assertEquals(0, friendsDao.getUserFriendList(user1.getId()).size());
    }

    @Test
    void getUserFriendList_wrongUserId() {
        Assertions.assertEquals(0, friendsDao.getUserFriendList(50).size());
    }

    @Test
    void getCommonFriends_returnListSize() {
        friendsDao.addToFriends(user1.getId(), user2.getId());
        friendsDao.addToFriends(user1.getId(), user3.getId());
        friendsDao.addToFriends(user2.getId(), user3.getId());

        Assertions.assertEquals(1, friendsDao.getCommonFriends(user1.getId(), user2.getId()).size());
        Assertions.assertEquals(user3.getId(), friendsDao.getCommonFriends(user1.getId(), user2.getId()).get(0).getId());
    }

    @Test
    void getCommonFriends_wrongUserOrFriendId() {
        friendsDao.addToFriends(user1.getId(), user2.getId());
        friendsDao.addToFriends(user1.getId(), user3.getId());
        friendsDao.addToFriends(user2.getId(), user3.getId());

        Assertions.assertEquals(0, friendsDao.getCommonFriends(user1.getId(), 50).size());
        Assertions.assertEquals(0, friendsDao.getCommonFriends(50, user1.getId()).size());
    }

}
