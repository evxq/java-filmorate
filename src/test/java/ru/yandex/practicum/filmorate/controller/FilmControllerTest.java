package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.yandex.practicum.filmorate.dao.MpaDao;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.time.Month;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class FilmControllerTest {

    private final JdbcTemplate jdbcTemplate;
    private final MpaDao mpaDao;
    private final FilmController filmController;
    private final UserController userController;

    @BeforeEach
    void cleanDb() {
        jdbcTemplate.update("DELETE FROM film_likes");
        jdbcTemplate.update("DELETE FROM film_genre");
        jdbcTemplate.update("DELETE FROM films");
        jdbcTemplate.update("DELETE FROM users");
    }

    @Test
    void getAllFilms() {
        Film film1 = new Film("Терминатор 1", "Шварцнеггер плохой", LocalDate.of(1984, Month.JANUARY, 1), 100);
        film1.setMpa(mpaDao.getMpaById(3));
        filmController.addFilm(film1);
        Film film2 = new Film("Терминатор 2", "Шварцнеггер хороший", LocalDate.of(1990, Month.JANUARY, 1), 100);
        film2.setMpa(mpaDao.getMpaById(3));
        filmController.addFilm(film2);

        Assertions.assertEquals(2, filmController.getAllFilms().size());
    }

    @Test
    void addFilm_returnListSize() {
        Film film1 = new Film("Терминатор 1", "Шварцнеггер плохой", LocalDate.of(1984, Month.JANUARY, 1), 100);
        film1.setMpa(mpaDao.getMpaById(3));
        filmController.addFilm(film1);
        Film film2 = new Film("Терминатор 2", "Шварцнеггер хороший", LocalDate.of(1990, Month.JANUARY, 1), 100);
        film2.setMpa(mpaDao.getMpaById(3));
        filmController.addFilm(film2);

        Assertions.assertEquals(2, filmController.getAllFilms().size());
        Assertions.assertEquals(film1, filmController.getFilmById(film1.getId()));
        Assertions.assertEquals(film2, filmController.getFilmById(film2.getId()));
    }

    @Test
    void addFilm_nullFilm() {
        ValidationException nullFilm = assertThrows(
                ValidationException.class,
                () -> filmController.addFilm(null)
        );
        Assertions.assertEquals("Данные фильма не переданы", nullFilm.getMessage());
    }

    @Test
    void addFilm_wrongName() {
        Film film1 = new Film("", "Шварцнеггер плохой", LocalDate.of(1984, Month.JANUARY, 1), 100);
        film1.setMpa(mpaDao.getMpaById(3));

        ValidationException wrongName = assertThrows(
                ValidationException.class,
                () -> filmController.addFilm(film1)
        );
        Assertions.assertEquals("Не указано название фильма", wrongName.getMessage());

        Film film2 = new Film(null, "Шварцнеггер плохой", LocalDate.of(1984, Month.JANUARY, 1), 100);
        ValidationException nullName = assertThrows(
                ValidationException.class,
                () -> filmController.addFilm(film2)
        );
        Assertions.assertEquals("Не указано название фильма", nullName.getMessage());
    }

    @Test
    void addFilm_tooLongDescription() {
        String description = "a".repeat(201);
        Film film = new Film("Терминатор 1", description, LocalDate.of(1984, Month.JANUARY, 1), 100);
        film.setMpa(mpaDao.getMpaById(3));

        ValidationException film1 = assertThrows(
                ValidationException.class,
                () -> filmController.addFilm(film)
        );
        Assertions.assertEquals("Размер описания не должен превышать 200 символов", film1.getMessage());
    }

    @Test
    void addFilm_wrongRelease() {
        Film film = new Film("Терминатор 1", "Шварцнеггер плохой", LocalDate.of(1895, Month.DECEMBER, 27), 100);
        film.setMpa(mpaDao.getMpaById(3));

        ValidationException film1 = assertThrows(
                ValidationException.class,
                () -> filmController.addFilm(film)
        );
        Assertions.assertEquals("Дата релиза указана некорректна", film1.getMessage());
    }

    @Test
    void addFilm_wrongDuration() {
        Film film = new Film("Терминатор 1", "Шварцнеггер плохой", LocalDate.of(1984, Month.JANUARY, 1), -10);
        film.setMpa(mpaDao.getMpaById(3));

        ValidationException film1 = assertThrows(
                ValidationException.class,
                () -> filmController.addFilm(film)
        );
        Assertions.assertEquals("Продолжительность фильма указана некорректна", film1.getMessage());
    }

    @Test
    void updateFilm_returnUpdatedFilm() {
        Film film1 = new Film("Терминатор 1", "Шварцнеггер плохой", LocalDate.of(1984, Month.JANUARY, 1), 100);
        film1.setMpa(mpaDao.getMpaById(3));
        filmController.addFilm(film1);
        Film updFilm = new Film("Терминатор 1", "Шварцнеггер чоткий", LocalDate.of(1984, Month.JANUARY, 2), 105);
        updFilm.setMpa(mpaDao.getMpaById(4));
        updFilm.setId(film1.getId());
        filmController.updateFilm(updFilm);

        assertEquals(filmController.getFilmById(film1.getId()), updFilm);
    }

    @Test
    void updateFilm_nonExistedFilm() {
        NotFoundException nonUser = assertThrows(
                NotFoundException.class,
                () -> filmController.updateFilm(new Film("Терминатор 1", "Шварцнеггер плохой", LocalDate.of(1984, Month.JANUARY, 1), 100))
        );
        Assertions.assertEquals("Некорректные данные фильма", nonUser.getMessage());
    }

    @Test
    void getFilmById_returnFilm() {
        Film film1 = new Film("Терминатор 1", "Шварцнеггер плохой", LocalDate.of(1984, Month.JANUARY, 1), 100);
        film1.setMpa(mpaDao.getMpaById(3));
        filmController.addFilm(film1);
        Film film2 = new Film("Терминатор 2", "Шварцнеггер хороший", LocalDate.of(1990, Month.JANUARY, 1), 100);
        film2.setMpa(mpaDao.getMpaById(3));
        filmController.addFilm(film2);
        Film filmById = filmController.getFilmById(film2.getId());

        Assertions.assertEquals(filmById, film2);
    }

    @Test
    void getFilmById_nonExistedId() {
        NotFoundException nonFilm = assertThrows(
                NotFoundException.class,
                () -> filmController.getFilmById(50)
        );
        Assertions.assertEquals("Некорректный id фильмa", nonFilm.getMessage());
    }

    @Test
    void likeFilm_returnAmountOfLike() {
        User user1 = userController.createUser(new User("user1@ya.ru", "user1", LocalDate.of(2000, Month.JANUARY, 1)));
        User user2 = userController.createUser(new User("user2@ya.ru", "user2", LocalDate.of(2000, Month.JANUARY, 2)));
        Film film = new Film("Терминатор 1", "Шварцнеггер плохой", LocalDate.of(1984, Month.JANUARY, 1), 100);
        film.setMpa(mpaDao.getMpaById(3));
        filmController.addFilm(film);
        filmController.likeFilm(film.getId(), user1.getId());
        filmController.likeFilm(film.getId(), user2.getId());

        Assertions.assertEquals(2, filmController.getFilmById(film.getId()).getLikes().size());
    }

    @Test
    void likeFilm_wrongFilmId() {
        User user = userController.createUser(new User("user1@ya.ru", "user1", LocalDate.of(2000, Month.JANUARY, 1)));

        NotFoundException nonFilm = assertThrows(
                NotFoundException.class,
                () -> filmController.likeFilm(10, user.getId())
        );
        Assertions.assertEquals("Некорректный фильм или пользователь", nonFilm.getMessage());
    }

    @Test
    void likeFilm_wrongUserId() {
        Film film = new Film("Терминатор 1", "Шварцнеггер плохой", LocalDate.of(1984, Month.JANUARY, 1), 100);
        film.setMpa(mpaDao.getMpaById(3));
        filmController.addFilm(film);

        NotFoundException nonUser = assertThrows(
                NotFoundException.class,
                () -> filmController.likeFilm(film.getId(), 50)
        );
        Assertions.assertEquals("Некорректный фильм или пользователь", nonUser.getMessage());
    }

    @Test
    void revokeLikeToFilm_returnAmountOfLike() {
        User user1 = userController.createUser(new User("user1@ya.ru", "user1", LocalDate.of(2000, Month.JANUARY, 1)));
        User user2 = userController.createUser(new User("user2@ya.ru", "user2", LocalDate.of(2000, Month.JANUARY, 2)));
        Film film = new Film("Терминатор 1", "Шварцнеггер плохой", LocalDate.of(1984, Month.JANUARY, 1), 100);
        film.setMpa(mpaDao.getMpaById(3));
        filmController.addFilm(film);
        filmController.likeFilm(film.getId(), user1.getId());
        filmController.likeFilm(film.getId(), user2.getId());

        Assertions.assertEquals(2, filmController.getFilmById(film.getId()).getLikes().size());

        filmController.revokeLikeToFilm(film.getId(), user1.getId());
        filmController.revokeLikeToFilm(film.getId(), user2.getId());

        Assertions.assertEquals(0, filmController.getFilmById(film.getId()).getLikes().size());
    }

    @Test
    void getTopPopFilms_returnListOfLikes() {
        User user1 = userController.createUser(new User("user1@ya.ru", "user1", LocalDate.of(2000, Month.JANUARY, 1)));
        User user2 = userController.createUser(new User("user2@ya.ru", "user2", LocalDate.of(2001, Month.JANUARY, 1)));
        User user3 = userController.createUser(new User("user3@ya.ru", "user3", LocalDate.of(2002, Month.JANUARY, 1)));
        Film film1 = new Film("Терминатор 1", "Шварцнеггер плохой", LocalDate.of(1984, Month.JANUARY, 1), 100);
        film1.setMpa(mpaDao.getMpaById(3));
        filmController.addFilm(film1);
        Film film2 = new Film("Терминатор 2", "Шварцнеггер хороший", LocalDate.of(1990, Month.JANUARY, 1), 100);
        film2.setMpa(mpaDao.getMpaById(3));
        filmController.addFilm(film2);
        Film film3 = new Film("Терминатор 3", "Шварцнеггер никакой", LocalDate.of(2003, Month.JANUARY, 1), 100);
        film3.setMpa(mpaDao.getMpaById(3));
        filmController.addFilm(film3);
        Film film4 = new Film("Терминатор 4", "Какой Шварцнеггер?", LocalDate.of(2009, Month.JANUARY, 1), 100);
        film4.setMpa(mpaDao.getMpaById(3));
        filmController.addFilm(film4);
        filmController.likeFilm(film1.getId(), user1.getId());
        filmController.likeFilm(film1.getId(), user2.getId());
        filmController.likeFilm(film1.getId(), user3.getId());     // 3 лайка фильму 1
        filmController.likeFilm(film2.getId(), user1.getId());
        filmController.likeFilm(film2.getId(), user2.getId());     // 2 лайка фильму 2
        filmController.likeFilm(film3.getId(), user1.getId());     // 1 лайк фильму 3

        Assertions.assertEquals(3, filmController.getTopPopFilms(3).size());
        Assertions.assertEquals(3, filmController.getTopPopFilms(3).get(0).getLikes().size());
        Assertions.assertEquals(2, filmController.getTopPopFilms(3).get(1).getLikes().size());
        Assertions.assertEquals(1, filmController.getTopPopFilms(3).get(2).getLikes().size());
    }

    @Test
    void getTopPopFilms_emptyList_wrongCount() {
        Assertions.assertEquals(0, filmController.getTopPopFilms(5).size());

        ValidationException emptyList = assertThrows(
                ValidationException.class,
                () -> filmController.getTopPopFilms(-1)
        );
        Assertions.assertEquals("Размер списка не может быть меньше 1", emptyList.getMessage());
    }

}
