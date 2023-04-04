package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.yandex.practicum.filmorate.dao.*;
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
public class FilmServiceTest {

    private final JdbcTemplate jdbcTemplate;
    private final MpaDao mpaDao;
    private final FilmService filmService;
    private final UserService userService;

    @BeforeEach
    void cleanDb() {
        jdbcTemplate.update("DELETE FROM film_mpa");
        jdbcTemplate.update("DELETE FROM film_likes");
        jdbcTemplate.update("DELETE FROM film_genre");
        jdbcTemplate.update("DELETE FROM friends");
        jdbcTemplate.update("DELETE FROM films");
        jdbcTemplate.update("DELETE FROM users");
    }

    @Test
    void getFilmById_returnFilm() {
        Film film1 = new Film("Терминатор 1", "Шварцнеггер плохой", LocalDate.of(1984, Month.JANUARY, 1), 100);
        film1.setMpa(mpaDao.getMpaById(3));
        filmService.addFilm(film1);
        Film film2 = new Film("Терминатор 2", "Шварцнеггер хороший", LocalDate.of(1990, Month.JANUARY, 1), 100);
        film2.setMpa(mpaDao.getMpaById(3));
        filmService.addFilm(film2);
        Film filmById = filmService.getFilmById(film2.getId());

        Assertions.assertEquals(filmById, film2);
    }

    @Test
    void getFilmById_nonExistedId() {
        NotFoundException nonFilm = assertThrows(
                NotFoundException.class,
                () -> filmService.getFilmById(50)
        );
        Assertions.assertEquals("Некорректный id фильмa", nonFilm.getMessage());
    }

    @Test
    void getAllFilms_returnListSize() {
        Film film1 = new Film("Терминатор 1", "Шварцнеггер плохой", LocalDate.of(1984, Month.JANUARY, 1), 100);
        film1.setMpa(mpaDao.getMpaById(3));
        filmService.addFilm(film1);
        Film film2 = new Film("Терминатор 2", "Шварцнеггер хороший", LocalDate.of(1990, Month.JANUARY, 1), 100);
        film2.setMpa(mpaDao.getMpaById(3));
        filmService.addFilm(film2);

        Assertions.assertEquals(2, filmService.getAllFilms().size());
    }

    @Test
    void addFilm_nullFilm() {
        ValidationException nullFilm = assertThrows(
                ValidationException.class,
                () -> filmService.addFilm(null)
        );
        Assertions.assertEquals("Данные фильма не переданы", nullFilm.getMessage());
    }

    @Test
    void addFilm_returnListSize() {
        Film film1 = new Film("Терминатор 1", "Шварцнеггер плохой", LocalDate.of(1984, Month.JANUARY, 1), 100);
        film1.setMpa(mpaDao.getMpaById(3));
        filmService.addFilm(film1);
        Film film2 = new Film("Терминатор 2", "Шварцнеггер хороший", LocalDate.of(1990, Month.JANUARY, 1), 100);
        film2.setMpa(mpaDao.getMpaById(3));
        filmService.addFilm(film2);

        Assertions.assertEquals(2, filmService.getAllFilms().size());
        Assertions.assertEquals(film1, filmService.getFilmById(film1.getId()));
        Assertions.assertEquals(film2, filmService.getFilmById(film2.getId()));
    }

    @Test
    void updateFilm_returnUpdatedFilm() {
        Film film1 = new Film("Терминатор 1", "Шварцнеггер плохой", LocalDate.of(1984, Month.JANUARY, 1), 100);
        film1.setMpa(mpaDao.getMpaById(3));
        filmService.addFilm(film1);
        Film updFilm = new Film("Терминатор 1", "Шварцнеггер чоткий", LocalDate.of(1984, Month.JANUARY, 2), 105);
        updFilm.setMpa(mpaDao.getMpaById(4));
        updFilm.setId(film1.getId());
        filmService.updateFilm(updFilm);

        assertEquals(filmService.getFilmById(film1.getId()), updFilm);
    }

    @Test
    void updateFilm_nonExistedFilm() {
        NotFoundException nonUser = assertThrows(
                NotFoundException.class,
                () -> filmService.updateFilm(new Film("Терминатор 1", "Шварцнеггер плохой", LocalDate.of(1984, Month.JANUARY, 1), 100))
        );
        Assertions.assertEquals("Некорректные данные фильма", nonUser.getMessage());
    }

    @Test
    void likeFilm_returnAmountOfLike() {
        User user1 = userService.createUser(new User("user1@ya.ru", "user1", LocalDate.of(2000, Month.JANUARY, 1)));
        User user2 = userService.createUser(new User("user2@ya.ru", "user2", LocalDate.of(2000, Month.JANUARY, 2)));
        Film film = new Film("Терминатор 1", "Шварцнеггер плохой", LocalDate.of(1984, Month.JANUARY, 1), 100);
        film.setMpa(mpaDao.getMpaById(3));
        filmService.addFilm(film);
        filmService.likeFilm(film.getId(), user1.getId());
        filmService.likeFilm(film.getId(), user2.getId());

        Assertions.assertEquals(2, filmService.getFilmById(film.getId()).getLikes().size());
    }

    @Test
    void likeFilm_wrongFilmId() {
        userService.createUser(new User("user1@ya.ru", "user1", LocalDate.of(2000, Month.JANUARY, 1)));

        NotFoundException nonFilm = assertThrows(
                NotFoundException.class,
                () -> filmService.likeFilm(1, 1)
        );
        Assertions.assertEquals("Некорректный фильм или пользователь", nonFilm.getMessage());
    }

    @Test
    void likeFilm_wrongUserId() {
        Film film = new Film("Терминатор 1", "Шварцнеггер плохой", LocalDate.of(1984, Month.JANUARY, 1), 100);
        film.setMpa(mpaDao.getMpaById(3));
        filmService.addFilm(film);

        NotFoundException nonUser = assertThrows(
                NotFoundException.class,
                () -> filmService.likeFilm(film.getId(), 50)
        );
        Assertions.assertEquals("Некорректный фильм или пользователь", nonUser.getMessage());
    }

    @Test
    void revokeLikeToFilm_returnAmountOfLike() {
        User user1 = userService.createUser(new User("user1@ya.ru", "user1", LocalDate.of(2000, Month.JANUARY, 1)));
        User user2 = userService.createUser(new User("user2@ya.ru", "user2", LocalDate.of(2000, Month.JANUARY, 2)));
        Film film = new Film("Терминатор 1", "Шварцнеггер плохой", LocalDate.of(1984, Month.JANUARY, 1), 100);
        film.setMpa(mpaDao.getMpaById(3));
        filmService.addFilm(film);
        filmService.likeFilm(film.getId(), user1.getId());
        filmService.likeFilm(film.getId(), user2.getId());
        filmService.revokeLikeToFilm(film.getId(), user1.getId());
        filmService.revokeLikeToFilm(film.getId(), user2.getId());

        Assertions.assertEquals(0, filmService.getFilmById(film.getId()).getLikes().size());
    }

    @Test
    void getTopPopFilms_returnListOfLikes() {
        User user1 = userService.createUser(new User("user1@ya.ru", "user1", LocalDate.of(2000, Month.JANUARY, 1)));
        User user2 = userService.createUser(new User("user2@ya.ru", "user2", LocalDate.of(2001, Month.JANUARY, 1)));
        User user3 = userService.createUser(new User("user3@ya.ru", "user3", LocalDate.of(2002, Month.JANUARY, 1)));
        Film film1 = new Film("Терминатор 1", "Шварцнеггер плохой", LocalDate.of(1984, Month.JANUARY, 1), 100);
        film1.setMpa(mpaDao.getMpaById(3));
        filmService.addFilm(film1);
        Film film2 = new Film("Терминатор 2", "Шварцнеггер хороший", LocalDate.of(1990, Month.JANUARY, 1), 100);
        film2.setMpa(mpaDao.getMpaById(3));
        filmService.addFilm(film2);
        Film film3 = new Film("Терминатор 3", "Шварцнеггер никакой", LocalDate.of(2003, Month.JANUARY, 1), 100);
        film3.setMpa(mpaDao.getMpaById(3));
        filmService.addFilm(film3);
        Film film4 = new Film("Терминатор 4", "Какой Шварцнеггер?", LocalDate.of(2009, Month.JANUARY, 1), 100);
        film4.setMpa(mpaDao.getMpaById(3));
        filmService.addFilm(film4);
        filmService.likeFilm(film1.getId(), user1.getId());
        filmService.likeFilm(film1.getId(), user2.getId());
        filmService.likeFilm(film1.getId(), user3.getId());     // 3 лайка фильму 1
        filmService.likeFilm(film2.getId(), user1.getId());
        filmService.likeFilm(film2.getId(), user2.getId());     // 2 лайка фильму 2
        filmService.likeFilm(film3.getId(), user1.getId());     // 1 лайк фильму 3

        Assertions.assertEquals(3, filmService.getTopPopFilms(3).size());
        Assertions.assertEquals(3, filmService.getTopPopFilms(3).get(0).getLikes().size());
        Assertions.assertEquals(2, filmService.getTopPopFilms(3).get(1).getLikes().size());
        Assertions.assertEquals(1, filmService.getTopPopFilms(3).get(2).getLikes().size());
    }

    @Test
    void getTopPopFilms_emptyList_wrongCount() {
        Assertions.assertEquals(0, filmService.getTopPopFilms(5).size());
    }

}
