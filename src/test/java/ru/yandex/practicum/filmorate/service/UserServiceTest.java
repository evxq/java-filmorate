package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.time.Month;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class UserServiceTest {

    private final JdbcTemplate jdbcTemplate;
    private final UserService userService;

    @BeforeEach
    void cleanDb() {
        jdbcTemplate.update("DELETE FROM friends");
        jdbcTemplate.update("DELETE FROM users");
    }

    @Test
    void getUserById_returnUser() {
        User user = userService.createUser(new User("user1@ya.ru", "user1", LocalDate.of(2000, Month.JANUARY, 1)));

        Assertions.assertEquals(user, userService.getUserById(user.getId()));
    }

    @Test
    void getUserById_wrongId() {
        NotFoundException nonUser = assertThrows(
                NotFoundException.class,
                () -> userService.getUserById(50)
        );
        Assertions.assertEquals("Некорректный пользователь", nonUser.getMessage());
    }

    @Test
    void getAllUsers_returnAllUserListSize() {
        userService.createUser(new User("user1@ya.ru", "user1", LocalDate.of(2000, Month.JANUARY, 1)));
        userService.createUser(new User("user2@ya.ru", "user2", LocalDate.of(2000, Month.JANUARY, 2)));

        Assertions.assertEquals(2, userService.getAllUsers().size());
    }

    @Test
    void createUser_nullUser() {
        ValidationException nonUser = assertThrows(
                ValidationException.class,
                () -> userService.createUser(null)
        );
        Assertions.assertEquals("Данные пользователя не переданы", nonUser.getMessage());
    }

    @Test
    void createUser_returnAllId() {
        User user1 = userService.createUser(new User("user1@ya.ru", "user1", LocalDate.of(2000, Month.JANUARY, 1)));
        User user2 = userService.createUser(new User("user2@ya.ru", "user2", LocalDate.of(2001, Month.JANUARY, 1)));

        Assertions.assertEquals(2, userService.getAllUsers().size());
        Assertions.assertEquals(user1, userService.getUserById(user1.getId()));
        Assertions.assertEquals(user2, userService.getUserById(user2.getId()));
    }

    @Test
    void updateUser_returnUpdatedUser() {
        User user = userService.createUser(new User("user1@ya.ru", "user1", LocalDate.of(2000, Month.JANUARY, 1)));
        User updUser = new User("userupd@ya.ru", "userUPD", LocalDate.of(2000, Month.JANUARY, 10));
        updUser.setId(user.getId());
        userService.updateUser(updUser);

        assertEquals(userService.getUserById(user.getId()), updUser);
    }

    @Test
    void updateUser_emptyUserMap() {
        NotFoundException nonUser = assertThrows(
                NotFoundException.class,
                () -> userService.updateUser(new User("user@ya.ru", "user1", LocalDate.of(2000, Month.JANUARY, 1)))
        );
        Assertions.assertEquals("Некорректный пользователь", nonUser.getMessage());
    }

    @Test
    void addToFriends_returnFriendListSize() {
        User user1 = userService.createUser(new User("user1@ya.ru", "user1", LocalDate.of(2000, Month.JANUARY, 1)));
        User user2 = userService.createUser(new User("user2@ya.ru", "user2", LocalDate.of(2001, Month.JANUARY, 1)));
        User user3 = userService.createUser(new User("user3@ya.ru", "user3", LocalDate.of(2002, Month.JANUARY, 1)));
        userService.addToFriends(user1.getId(), user2.getId());
        userService.addToFriends(user1.getId(), user3.getId());

        Assertions.assertEquals(2, userService.getUserFriendList(user1.getId()).size());
        Assertions.assertEquals(0, userService.getUserFriendList(user2.getId()).size());
        Assertions.assertEquals(0, userService.getUserFriendList(user3.getId()).size());
    }

    @Test
    void addToFriends_nonExistUserId() {
        NotFoundException nonUser = assertThrows(
                NotFoundException.class,
                () -> userService.addToFriends(50, 100)
        );
        Assertions.assertEquals("При добавлении друга вызван некорректный пользователь", nonUser.getMessage());
    }

    @Test
    void deleteFromFriends_returnFriendListSize() {
        User user1 = userService.createUser(new User("user1@ya.ru", "user1", LocalDate.of(2000, Month.JANUARY, 1)));
        User user2 = userService.createUser(new User("user2@ya.ru", "user2", LocalDate.of(2001, Month.JANUARY, 1)));
        User user3 = userService.createUser(new User("user3@ya.ru", "user3", LocalDate.of(2002, Month.JANUARY, 1)));
        userService.addToFriends(user1.getId(), user2.getId());
        userService.addToFriends(user1.getId(), user3.getId());
        userService.deleteFromFriends(user1.getId(), user2.getId());

        Assertions.assertEquals(1, userService.getUserFriendList(user1.getId()).size());
    }

    @Test
    void getUserFriends_returnUserFriendsListSize() {
        User user1 = userService.createUser(new User("user1@ya.ru", "user1", LocalDate.of(2000, Month.JANUARY, 1)));
        User user2 = userService.createUser(new User("user2@ya.ru", "user2", LocalDate.of(2001, Month.JANUARY, 1)));
        User user3 = userService.createUser(new User("user3@ya.ru", "user3", LocalDate.of(2002, Month.JANUARY, 1)));
        userService.addToFriends(user1.getId(), user2.getId());
        userService.addToFriends(user1.getId(), user3.getId());

        Assertions.assertEquals(2, userService.getUserFriendList(user1.getId()).size());
        Assertions.assertEquals(user2, userService.getUserFriendList(user1.getId()).get(0));
        Assertions.assertEquals(user3, userService.getUserFriendList(user1.getId()).get(1));
    }

    @Test
    void getCommonFriends_returnCommonFriendsListSize() {
        User user1 = userService.createUser(new User("user1@ya.ru", "user1", LocalDate.of(2000, Month.JANUARY, 1)));
        User user2 = userService.createUser(new User("user2@ya.ru", "user2", LocalDate.of(2001, Month.JANUARY, 1)));
        User user3 = userService.createUser(new User("user3@ya.ru", "user3", LocalDate.of(2002, Month.JANUARY, 1)));
        User user4 = userService.createUser(new User("user4@ya.ru", "user4", LocalDate.of(2003, Month.JANUARY, 1)));
        userService.addToFriends(user1.getId(), user2.getId());
        userService.addToFriends(user1.getId(), user3.getId());
        userService.addToFriends(user4.getId(), user2.getId());
        userService.addToFriends(user4.getId(), user3.getId());

        Assertions.assertEquals(2, userService.getCommonFriends(user1.getId(), user4.getId()).size());
        Assertions.assertEquals(user2, userService.getCommonFriends(user1.getId(), user4.getId()).get(0));
        Assertions.assertEquals(user3, userService.getCommonFriends(user1.getId(), user4.getId()).get(1));
    }

}
