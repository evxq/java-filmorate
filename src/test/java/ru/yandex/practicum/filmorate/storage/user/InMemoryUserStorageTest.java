package ru.yandex.practicum.filmorate.storage.user;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.controller.UserController;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import java.time.LocalDate;
import java.time.Month;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
public class InMemoryUserStorageTest {

    UserStorage userStorage = new InMemoryUserStorage();
    UserService userService = new UserService(userStorage);
    UserController userController = new UserController(userStorage, userService);

    @Test
    void getUserById_returnUser() {
        User user = userController.createUser(new User("user1@ya.ru", "user1", LocalDate.of(2000, Month.JANUARY, 1)));

        Assertions.assertEquals(user, userStorage.getUserById(1));
    }

    @Test
    void getUserById_wrongId() {
        NotFoundException nonuser = assertThrows(
                NotFoundException.class,
                () -> userStorage.getUserById(1)
        );
        Assertions.assertEquals("Такой пользователь отсутствует", nonuser.getMessage());
    }

    @Test
    void getAllUsers_returnAllUserListSize() {
        userController.createUser(new User("user1@ya.ru", "user1", LocalDate.of(2000, Month.JANUARY, 1)));
        userController.createUser(new User("user2@ya.ru", "user2", LocalDate.of(2000, Month.JANUARY, 2)));

        Assertions.assertEquals(2, userController.getAllUsers().size());
    }

    @Test
    void createUser_nullUser() {
        ValidationException nonuser = assertThrows(
                ValidationException.class,
                () -> userController.createUser(null)
        );
        Assertions.assertEquals("Данные пользователя не переданы", nonuser.getMessage());
    }

    @Test
    void createUser_returnAllId() {
        User user1 = userController.createUser(new User("user1@ya.ru", "user1", LocalDate.of(2000, Month.JANUARY, 1)));
        User user2 = userController.createUser(new User("user2@ya.ru", "user2", LocalDate.of(2001, Month.JANUARY, 1)));
        User user3 = userController.createUser(new User("user3@ya.ru", "user3", LocalDate.of(2002, Month.JANUARY, 1)));

        Assertions.assertEquals(1, user1.getId());
        Assertions.assertEquals(2, user2.getId());
        Assertions.assertEquals(3, user3.getId());
        Assertions.assertEquals("user1@ya.ru", user1.getEmail());
        Assertions.assertEquals("user1", user1.getLogin());
        Assertions.assertEquals("user1", user1.getName());
        Assertions.assertNotNull(user1.getBirthday());
    }

    @Test
    void createUser_wrongEmail() {
        ValidationException nonuser1 = assertThrows(
                ValidationException.class,
                () -> userController.createUser(new User("userya.ru", "user1", LocalDate.of(2000, Month.JANUARY, 1)))
        );
        Assertions.assertEquals("Адрес почты не указан или некорректен", nonuser1.getMessage());

        ValidationException nonuser2 = assertThrows(
                ValidationException.class,
                () -> userController.createUser(new User("", "user1", LocalDate.of(2000, Month.JANUARY, 1)))
        );
        Assertions.assertEquals("Адрес почты не указан или некорректен", nonuser2.getMessage());

        ValidationException nonuser3 = assertThrows(
                ValidationException.class,
                () -> userController.createUser(new User(null, "user1", LocalDate.of(2000, Month.JANUARY, 1)))
        );
        Assertions.assertEquals("Адрес почты не указан или некорректен", nonuser3.getMessage());
    }

    @Test
    void createUser_wrongLogin() {
        ValidationException nonuser1 = assertThrows(
                ValidationException.class,
                () -> userController.createUser(new User("user@ya.ru", "", LocalDate.of(2000, Month.JANUARY, 1)))
        );
        Assertions.assertEquals("Логин не указан или некорректен", nonuser1.getMessage());

        ValidationException nonuser2 = assertThrows(
                ValidationException.class,
                () -> userController.createUser(new User("user@ya.ru", null, LocalDate.of(2000, Month.JANUARY, 1)))
        );
        Assertions.assertEquals("Логин не указан или некорректен", nonuser2.getMessage());
    }

    @Test
    void createUser_wrongBirthday() {
        ValidationException nonuser1 = assertThrows(
                ValidationException.class,
                () -> userController.createUser(new User("user@ya.ru", "user1", LocalDate.of(2030, Month.JANUARY, 1)))
        );
        Assertions.assertEquals("Дата рождения некорректна", nonuser1.getMessage());
    }

    @Test
    void updateUser_returnUpdatedUser() {
        userController.createUser(new User("user1@ya.ru", "user1", LocalDate.of(2000, Month.JANUARY, 1)));
        User updUser = new User("userupd@ya.ru", "userUPD", LocalDate.of(2000, Month.JANUARY, 1));
        updUser.setId(1);
        userController.updateUser(updUser);

        assertEquals(userController.getUserById(1), updUser);
    }

    @Test
    void updateUser_emptyUserMap() {
        NotFoundException nonuser = assertThrows(
                NotFoundException.class,
                () -> userController.updateUser(new User("user@ya.ru", "user1", LocalDate.of(2000, Month.JANUARY, 1)))
        );
        Assertions.assertEquals("Такой пользователь отсутствует", nonuser.getMessage());
    }

}
