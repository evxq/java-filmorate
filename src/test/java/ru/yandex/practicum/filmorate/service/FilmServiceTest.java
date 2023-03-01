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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
public class FilmServiceTest {

    FilmStorage filmStorage = new InMemoryFilmStorage();
    UserStorage userStorage = new InMemoryUserStorage();
    UserService userService = new UserService(userStorage);
    FilmService filmService = new FilmService(filmStorage, userService);
    FilmController filmController = new FilmController(filmService);
    UserController userController = new UserController(userService);

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
    void getAllFilms_returnAllFilmsListSize() {
        filmController.addFilm(new Film("Терминатор 1", "Шварцнеггер плохой", LocalDate.of(1984, Month.JANUARY, 1), 100));
        filmController.addFilm(new Film("Терминатор 2", "Шварцнеггер хороший", LocalDate.of(1990, Month.JANUARY, 1), 100));

        Assertions.assertEquals(2, filmController.getAllFilms().size());
    }

    @Test
    void addFilm_nullFilm() {
        ValidationException nonuser = assertThrows(
                ValidationException.class,
                () -> filmController.addFilm(null)
        );
        Assertions.assertEquals("Данные фильма не переданы", nonuser.getMessage());
    }

    @Test
    void addFilm_returnAllId() {
        Film film1 = filmController.addFilm(new Film("Терминатор 1", "Шварцнеггер плохой", LocalDate.of(1984, Month.JANUARY, 1), 100));
        Film film2 = filmController.addFilm(new Film("Терминатор 2", "Шварцнеггер хороший", LocalDate.of(1990, Month.JANUARY, 1), 100));
        Film film3 = filmController.addFilm(new Film("Терминатор 3", "Шварцнеггер никакой", LocalDate.of(2003, Month.JANUARY, 1), 100));

        Assertions.assertEquals(1, film1.getId());
        Assertions.assertEquals(2, film2.getId());
        Assertions.assertEquals(3, film3.getId());
        Assertions.assertEquals("Терминатор 1", film1.getName());
        Assertions.assertEquals("Шварцнеггер плохой", film1.getDescription());
        Assertions.assertNotNull(film1.getReleaseDate());
        Assertions.assertNotNull(film1.getDuration());
    }

    @Test
    void addFilm_wrongName() {
        ValidationException film1 = assertThrows(
                ValidationException.class,
                () -> filmController.addFilm(new Film("", "Шварцнеггер плохой", LocalDate.of(1984, Month.JANUARY, 1), 100))
        );
        Assertions.assertEquals("Не указано название фильма", film1.getMessage());

        ValidationException film2 = assertThrows(
                ValidationException.class,
                () -> filmController.addFilm(new Film(null, "Шварцнеггер плохой", LocalDate.of(1984, Month.JANUARY, 1), 100))
        );
        Assertions.assertEquals("Не указано название фильма", film2.getMessage());
    }

    @Test
    void addFilm_tooLongDescription() {
        String description = "a".repeat(201);

        ValidationException film1 = assertThrows(
                ValidationException.class,
                () -> filmController.addFilm(new Film("Терминатор 1", description, LocalDate.of(1984, Month.JANUARY, 1), 100))
        );
        Assertions.assertEquals("Размер описания не должен превышать 200 символов", film1.getMessage());
    }

    @Test
    void addFilm_wrongRelease() {
        ValidationException film1 = assertThrows(
                ValidationException.class,
                () -> filmController.addFilm(new Film("Терминатор 1", "Шварцнеггер плохой", LocalDate.of(1895, Month.DECEMBER, 27), 100))
        );
        Assertions.assertEquals("Дата релиза указана некорректна", film1.getMessage());
    }

    @Test
    void addFilm_wrongDuration() {
        ValidationException film1 = assertThrows(
                ValidationException.class,
                () -> filmController.addFilm(new Film("Терминатор 1", "Шварцнеггер плохой", LocalDate.of(1984, Month.JANUARY, 1), -10))
        );
        Assertions.assertEquals("Продолжительность фильма указана некорректна", film1.getMessage());
    }

    @Test
    void updateFilm_returnUpdatedFilm() {
        filmController.addFilm(new Film("Терминатор 1", "Шварцнеггер плохой", LocalDate.of(1984, Month.JANUARY, 1), 100));
        Film updFilm = new Film("Терминатор 1", "Шварцнеггер чоткий", LocalDate.of(1984, Month.JANUARY, 1), 100);
        updFilm.setId(1);
        filmController.updateFilm(updFilm);

        assertEquals(filmController.getFilmById(1), updFilm);
    }

    @Test
    void updateFilm_emptyFilmMap() {
        NotFoundException nonuser0 = assertThrows(
                NotFoundException.class,
                () -> filmController.updateFilm(new Film("Терминатор 1", "Шварцнеггер плохой", LocalDate.of(1984, Month.JANUARY, 1), 100))
        );
        Assertions.assertEquals("Такой фильм отсутствует", nonuser0.getMessage());
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
        Assertions.assertEquals("Такой фильм отсутствует", nonFilm.getMessage());
    }

    @Test
    void likeFilm_wrongUserId() {
        filmController.addFilm(new Film("Терминатор 1", "Шварцнеггер плохой", LocalDate.of(1984, Month.JANUARY, 1), 100));
        NotFoundException nonUser = assertThrows(
                NotFoundException.class,
                () -> filmController.likeFilm(1, 1)
        );
        Assertions.assertEquals("Такой пользователь отсутствует", nonUser.getMessage());
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
        filmController.revokeLikeToFilm(1, 1);
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
        Assertions.assertEquals(0, filmController.getTopPopFilms(5).size());

        filmController.addFilm(new Film("Терминатор 1", "Шварцнеггер плохой", LocalDate.of(1984, Month.JANUARY, 1), 100));

        ValidationException emptyList = assertThrows(
                ValidationException.class,
                () -> filmController.getTopPopFilms(-1)
        );
        Assertions.assertEquals("Размер списка не может быть меньше 1", emptyList.getMessage());
    }

}
