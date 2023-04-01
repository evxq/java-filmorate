package ru.yandex.practicum.filmorate.controller;

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
public class UserControllerTest {

    private final JdbcTemplate jdbcTemplate;
    private final UserController userController;

    @BeforeEach
    void cleanDb() {
        jdbcTemplate.update("DELETE FROM friends");
        jdbcTemplate.update("DELETE FROM users");
    }

    @Test
    void getAllUsers_returnAllUserListSize() {
        userController.createUser(new User("user1@ya.ru", "user1", LocalDate.of(2000, Month.JANUARY, 1)));
        userController.createUser(new User("user2@ya.ru", "user2", LocalDate.of(2000, Month.JANUARY, 2)));

        Assertions.assertEquals(2, userController.getAllUsers().size());
    }

    @Test
    void createUser_nullUser() {
        ValidationException nonUser = assertThrows(
                ValidationException.class,
                () -> userController.createUser(null)
        );
        Assertions.assertEquals("Данные пользователя не переданы", nonUser.getMessage());
    }

    @Test
    void createUser_returnAllId() {
        User user1 = userController.createUser(new User("user1@ya.ru", "user1", LocalDate.of(2000, Month.JANUARY, 1)));
        User user2 = userController.createUser(new User("user2@ya.ru", "user2", LocalDate.of(2001, Month.JANUARY, 1)));

        Assertions.assertEquals(2, userController.getAllUsers().size());
        Assertions.assertEquals(user1, userController.getUserById(user1.getId()));
        Assertions.assertEquals(user2, userController.getUserById(user2.getId()));
    }

    @Test
    void createUser_wrongEmail() {
        ValidationException nonUser1 = assertThrows(
                ValidationException.class,
                () -> userController.createUser(new User("userya.ru", "user1", LocalDate.of(2000, Month.JANUARY, 1)))
        );
        Assertions.assertEquals("Адрес почты не указан или некорректен", nonUser1.getMessage());

        ValidationException nonUser2 = assertThrows(
                ValidationException.class,
                () -> userController.createUser(new User("", "user1", LocalDate.of(2000, Month.JANUARY, 1)))
        );
        Assertions.assertEquals("Адрес почты не указан или некорректен", nonUser2.getMessage());

        ValidationException nonUser3 = assertThrows(
                ValidationException.class,
                () -> userController.createUser(new User(null, "user1", LocalDate.of(2000, Month.JANUARY, 1)))
        );
        Assertions.assertEquals("Адрес почты не указан или некорректен", nonUser3.getMessage());
    }

    @Test
    void createUser_wrongLogin() {
        ValidationException nonUser1 = assertThrows(
                ValidationException.class,
                () -> userController.createUser(new User("user@ya.ru", "", LocalDate.of(2000, Month.JANUARY, 1)))
        );
        Assertions.assertEquals("Логин не указан или некорректен", nonUser1.getMessage());

        ValidationException nonUser2 = assertThrows(
                ValidationException.class,
                () -> userController.createUser(new User("user@ya.ru", null, LocalDate.of(2000, Month.JANUARY, 1)))
        );
        Assertions.assertEquals("Логин не указан или некорректен", nonUser2.getMessage());
    }

    @Test
    void createUser_wrongBirthday() {
        ValidationException nonUser1 = assertThrows(
                ValidationException.class,
                () -> userController.createUser(new User("user@ya.ru", "user1", LocalDate.of(2030, Month.JANUARY, 1)))
        );
        Assertions.assertEquals("Дата рождения некорректна", nonUser1.getMessage());
    }

    @Test
    void updateUser_returnUpdatedUser() {
        User user = userController.createUser(new User("user1@ya.ru", "user1", LocalDate.of(2000, Month.JANUARY, 1)));
        User updUser = new User("userupd@ya.ru", "userUPD", LocalDate.of(2000, Month.JANUARY, 10));
        updUser.setId(user.getId());
        userController.updateUser(updUser);

        assertEquals(userController.getUserById(user.getId()), updUser);
    }

    @Test
    void updateUser_emptyUserMap() {
        NotFoundException nonUser = assertThrows(
                NotFoundException.class,
                () -> userController.updateUser(new User("user@ya.ru", "user1", LocalDate.of(2000, Month.JANUARY, 1)))
        );
        Assertions.assertEquals("Некорректный пользователь", nonUser.getMessage());
    }

    @Test
    void getUserById_returnUser() {
        User user = userController.createUser(new User("user1@ya.ru", "user1", LocalDate.of(2000, Month.JANUARY, 1)));

        Assertions.assertEquals(user, userController.getUserById(user.getId()));
    }

    @Test
    void getUserById_wrongId() {
        NotFoundException nonUser = assertThrows(
                NotFoundException.class,
                () -> userController.getUserById(50)
        );
        Assertions.assertEquals("Некорректный пользователь", nonUser.getMessage());
    }

    @Test
    void addToFriends_returnFriendListSize() {
        User user1 = userController.createUser(new User("user1@ya.ru", "user1", LocalDate.of(2000, Month.JANUARY, 1)));
        User user2 = userController.createUser(new User("user2@ya.ru", "user2", LocalDate.of(2001, Month.JANUARY, 1)));
        User user3 = userController.createUser(new User("user3@ya.ru", "user3", LocalDate.of(2002, Month.JANUARY, 1)));
        userController.addToFriends(user1.getId(), user2.getId());
        userController.addToFriends(user1.getId(), user3.getId());

        Assertions.assertEquals(2, userController.getUserFriendList(user1.getId()).size());
        Assertions.assertEquals(0, userController.getUserFriendList(user2.getId()).size());
        Assertions.assertEquals(0, userController.getUserFriendList(user3.getId()).size());
    }

    @Test
    void addToFriends_nonExistUserId() {
        NotFoundException nonUser = assertThrows(
                NotFoundException.class,
                () -> userController.addToFriends(50, 100)
        );
        Assertions.assertEquals("При добавлении друга вызван некорректный пользователь", nonUser.getMessage());
    }

    @Test
    void deleteFromFriends_returnFriendListSize() {
        User user1 = userController.createUser(new User("user1@ya.ru", "user1", LocalDate.of(2000, Month.JANUARY, 1)));
        User user2 = userController.createUser(new User("user2@ya.ru", "user2", LocalDate.of(2001, Month.JANUARY, 1)));
        User user3 = userController.createUser(new User("user3@ya.ru", "user3", LocalDate.of(2002, Month.JANUARY, 1)));
        userController.addToFriends(user1.getId(), user2.getId());
        userController.addToFriends(user1.getId(), user3.getId());
        userController.deleteFromFriends(user1.getId(), user2.getId());

        Assertions.assertEquals(1, userController.getUserFriendList(user1.getId()).size());
    }

    @Test
    void getUserFriends_returnUserFriendsListSize() {
        User user1 = userController.createUser(new User("user1@ya.ru", "user1", LocalDate.of(2000, Month.JANUARY, 1)));
        User user2 = userController.createUser(new User("user2@ya.ru", "user2", LocalDate.of(2001, Month.JANUARY, 1)));
        User user3 = userController.createUser(new User("user3@ya.ru", "user3", LocalDate.of(2002, Month.JANUARY, 1)));
        userController.addToFriends(user1.getId(), user2.getId());
        userController.addToFriends(user1.getId(), user3.getId());

        Assertions.assertEquals(2, userController.getUserFriendList(user1.getId()).size());
        Assertions.assertEquals(user2, userController.getUserFriendList(user1.getId()).get(0));
        Assertions.assertEquals(user3, userController.getUserFriendList(user1.getId()).get(1));
    }

    @Test
    void getCommonFriends_returnCommonFriendsListSize() {
        User user1 = userController.createUser(new User("user1@ya.ru", "user1", LocalDate.of(2000, Month.JANUARY, 1)));
        User user2 = userController.createUser(new User("user2@ya.ru", "user2", LocalDate.of(2001, Month.JANUARY, 1)));
        User user3 = userController.createUser(new User("user3@ya.ru", "user3", LocalDate.of(2002, Month.JANUARY, 1)));
        User user4 = userController.createUser(new User("user4@ya.ru", "user4", LocalDate.of(2003, Month.JANUARY, 1)));
        userController.addToFriends(user1.getId(), user2.getId());
        userController.addToFriends(user1.getId(), user3.getId());
        userController.addToFriends(user4.getId(), user2.getId());
        userController.addToFriends(user4.getId(), user3.getId());

        Assertions.assertEquals(2, userController.getCommonFriends(user1.getId(), user4.getId()).size());
        Assertions.assertEquals(user2, userController.getCommonFriends(user1.getId(), user4.getId()).get(0));
        Assertions.assertEquals(user3, userController.getCommonFriends(user1.getId(), user4.getId()).get(1));
    }

}
