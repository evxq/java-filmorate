package ru.yandex.practicum.filmorate.storage.user;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.yandex.practicum.filmorate.controller.UserController;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.time.Month;

import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class UserDbStorageTest {

    private final JdbcTemplate jdbcTemplate;
    private final UserStorage userStorage;
    private final UserController userController;

    @BeforeEach
    void cleanDb() {
        jdbcTemplate.update("DELETE FROM friends");
        jdbcTemplate.update("DELETE FROM users");
    }

    @Test
    void getUserById_returnUser() {
        User user = new User("user1@ya.ru", "user1", LocalDate.of(2000, Month.JANUARY, 1));
        userController.createUser(user);

        Assertions.assertEquals(user, userStorage.getUserById(user.getId()));
    }

    @Test
    void getUserById_wrongUserId() {
        NotFoundException wrongUser = assertThrows(
                NotFoundException.class,
                () -> userStorage.getUserById(50)
        );
        Assertions.assertEquals("Некорректный пользователь", wrongUser.getMessage());
    }

    @Test
    void getAllUsers_returnListSizeAndUsers() {
        User user1 = new User("user1@ya.ru", "user1", LocalDate.of(2000, Month.JANUARY, 1));
        User user2 = new User("user2@ya.ru", "user2", LocalDate.of(2001, Month.JANUARY, 1));
        userController.createUser(user1);
        userController.createUser(user2);

        Assertions.assertEquals(2, userStorage.getAllUsers().size());
        Assertions.assertEquals(user1, userStorage.getAllUsers().get(0));
        Assertions.assertEquals(user2, userStorage.getAllUsers().get(1));
    }

    @Test
    void getAllFilms_noFilms() {
        Assertions.assertEquals(0, userStorage.getAllUsers().size());
    }

    @Test
    void createUser_returnUser() {
        User user1 = new User("user1@ya.ru", "user1", LocalDate.of(2000, Month.JANUARY, 1));
        userController.createUser(user1);

        Assertions.assertEquals(user1, userStorage.getUserById(user1.getId()));
    }

    @Test
    void updateUser_returnEqualUser() {
        User user1 = new User("user1@ya.ru", "user1", LocalDate.of(2000, Month.JANUARY, 1));
        userController.createUser(user1);

        User userUpd = new User("userUpd@ya.ru", "userVasya", LocalDate.of(2000, Month.JANUARY, 10));
        userUpd.setId(user1.getId());
        userStorage.updateUser(userUpd);

        Assertions.assertEquals(userUpd, userStorage.getUserById(userUpd.getId()));
    }

}
