package ru.yandex.practicum.filmorate.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.controller.UserController;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.film.InMemoryFilmStorage;
import ru.yandex.practicum.filmorate.storage.user.InMemoryUserStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.time.LocalDate;
import java.time.Month;

import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
public class FilmServiceTest {

    FilmStorage filmStorage = new InMemoryFilmStorage();
    UserStorage userStorage = new InMemoryUserStorage();
    FilmService filmService = new FilmService(filmStorage, userStorage);
    FilmController filmController = new FilmController(filmStorage, filmService);

    UserService userService = new UserService(userStorage);
    UserController userController = new UserController(userStorage, userService);

    @Test
    void getFilmById_returnFilm() {
        filmController.addFilm(new Film("Терминатор 1", "Шварцнеггер плохой", LocalDate.of(1984, Month.JANUARY, 1), 100));
        filmController.addFilm(new Film("Терминатор 2", "Шварцнеггер хороший", LocalDate.of(1990, Month.JANUARY, 1), 100));
        Film filmById = filmController.getFilmById(2);

        Assertions.assertEquals(2, filmById.getId());
        Assertions.assertEquals("Терминатор 2", filmById.getName());
        Assertions.assertEquals("Шварцнеггер хороший", filmById.getDescription());
    }

    @Test
    void getFilmById_nonExistedId() {
        NotFoundException nonfilm = assertThrows(
                NotFoundException.class,
                () -> filmController.getFilmById(2)
        );
        Assertions.assertEquals("Такой фильм отсутствует", nonfilm.getMessage());
    }

    @Test
    void likeFilm_returnAmountOfLike() {
        userController.createUser(new User("user1@ya.ru", "user1", LocalDate.of(2000, Month.JANUARY, 1)));
        userController.createUser(new User("user2@ya.ru", "user2", LocalDate.of(2000, Month.JANUARY, 2)));
        filmController.addFilm(new Film("Терминатор 1", "Шварцнеггер плохой", LocalDate.of(1984, Month.JANUARY, 1), 100));
        filmController.likeFilm(1, 1);
        filmController.likeFilm(1, 2);

        Assertions.assertEquals(2, filmController.getFilmById(1).getLikes().size());
    }

    @Test
    void likeFilm_wrongFilmId() {
        userController.createUser(new User("user1@ya.ru", "user1", LocalDate.of(2000, Month.JANUARY, 1)));

        NotFoundException nonFilm = assertThrows(
                NotFoundException.class,
                () -> filmController.likeFilm(1, 1)
        );
        Assertions.assertEquals("Фильм id=1 не найден", nonFilm.getMessage());
    }

    @Test
    void likeFilm_wrongUserId() {
        filmController.addFilm(new Film("Терминатор 1", "Шварцнеггер плохой", LocalDate.of(1984, Month.JANUARY, 1), 100));

        NotFoundException nonUser = assertThrows(
                NotFoundException.class,
                () -> filmController.likeFilm(1, 1)
        );
        Assertions.assertEquals("Пользователь id=1 не найден", nonUser.getMessage());
    }

    @Test
    void revokeLikeToFilm_returnAmountOfLike() {
        userController.createUser(new User("user1@ya.ru", "user1", LocalDate.of(2000, Month.JANUARY, 1)));
        userController.createUser(new User("user2@ya.ru", "user2", LocalDate.of(2000, Month.JANUARY, 2)));
        filmController.addFilm(new Film("Терминатор 1", "Шварцнеггер плохой", LocalDate.of(1984, Month.JANUARY, 1), 100));
        filmController.likeFilm(1, 1);
        filmController.likeFilm(1, 2);

        filmController.revokeLikeToFilm(1, 1);
        filmController.revokeLikeToFilm(1, 2);

        Assertions.assertEquals(0, filmController.getFilmById(1).getLikes().size());
    }

    @Test
    void revokeLikeToFilm_nonExistedLike() {
        userController.createUser(new User("user1@ya.ru", "user1", LocalDate.of(2000, Month.JANUARY, 1)));
        filmController.addFilm(new Film("Терминатор 1", "Шварцнеггер плохой", LocalDate.of(1984, Month.JANUARY, 1), 100));

        ValidationException nonLike = assertThrows(
                ValidationException.class,
                () -> filmController.revokeLikeToFilm(1, 1)
        );
        Assertions.assertEquals("Фильм id=1 не получал лайк от пользователя id=1", nonLike.getMessage());
    }

    @Test
    void getTopPopFilms_returnListOfLikes() {
        userController.createUser(new User("user1@ya.ru", "user1", LocalDate.of(2000, Month.JANUARY, 1)));
        userController.createUser(new User("user2@ya.ru", "user2", LocalDate.of(2001, Month.JANUARY, 1)));
        userController.createUser(new User("user3@ya.ru", "user3", LocalDate.of(2002, Month.JANUARY, 1)));
        filmController.addFilm(new Film("Терминатор 1", "Шварцнеггер плохой", LocalDate.of(1984, Month.JANUARY, 1), 100));
        filmController.addFilm(new Film("Терминатор 2", "Шварцнеггер хороший", LocalDate.of(1990, Month.JANUARY, 1), 100));
        filmController.addFilm(new Film("Терминатор 3", "Шварцнеггер никакой", LocalDate.of(2003, Month.JANUARY, 1), 100));
        filmController.addFilm(new Film("Терминатор 4", "Какой Шварцнеггер?", LocalDate.of(2009, Month.JANUARY, 1), 100));

        filmController.likeFilm(1, 1);
        filmController.likeFilm(1, 2);
        filmController.likeFilm(1, 3);     // 3 лайка фильму 1
        filmController.likeFilm(2, 1);
        filmController.likeFilm(2, 2);     // 2 лайка фильму 2
        filmController.likeFilm(3, 1);     // 1 лайк фильму 3

        Assertions.assertEquals(3, filmController.getTopPopFilms(3).size());
        Assertions.assertEquals(3, filmController.getTopPopFilms(3).get(0).getLikes().size());
        Assertions.assertEquals(2, filmController.getTopPopFilms(3).get(1).getLikes().size());
        Assertions.assertEquals(1, filmController.getTopPopFilms(3).get(2).getLikes().size());
    }

    @Test
    void getTopPopFilms_emptyList_wrongCount() {
        ValidationException emptyList0 = assertThrows(
                ValidationException.class,
                () -> filmController.getTopPopFilms(1)
        );
        Assertions.assertEquals("Список фильмов пуст", emptyList0.getMessage());

        filmController.addFilm(new Film("Терминатор 1", "Шварцнеггер плохой", LocalDate.of(1984, Month.JANUARY, 1), 100));

        ValidationException emptyList = assertThrows(
                ValidationException.class,
                () -> filmController.getTopPopFilms(-1)
        );
        Assertions.assertEquals("Размер списка не может быть меньше 1", emptyList.getMessage());
    }

}
