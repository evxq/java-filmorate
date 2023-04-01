package ru.yandex.practicum.filmorate.storage.film;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.controller.UserController;
import ru.yandex.practicum.filmorate.dao.FilmLikesDao;
import ru.yandex.practicum.filmorate.dao.MpaDao;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.time.Month;

import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class FilmDbStorageTest {

    private final JdbcTemplate jdbcTemplate;
    private final FilmStorage filmStorage;
    private final MpaDao mpaDao;
    private final FilmLikesDao filmLikesDao;
    private final FilmController filmController;
    private final UserController userController;

    @BeforeEach
    void cleanDb() {
        jdbcTemplate.update("DELETE FROM film_mpa");
        jdbcTemplate.update("DELETE FROM film_likes");
        jdbcTemplate.update("DELETE FROM film_genre");
        jdbcTemplate.update("DELETE FROM films");
    }

    @Test
    void getFilmById_returnFilm() {
        Film film1 = new Film("Терминатор 1", "Шварцнеггер плохой", LocalDate.of(1984, Month.JANUARY, 1), 100);
        film1.setMpa(mpaDao.getMpaById(3));
        filmController.addFilm(film1);

        Assertions.assertEquals(film1, filmStorage.getFilmById(film1.getId()));
    }

    @Test
    void getFilmById_wrongFilmId() {
        NotFoundException wrongFilm = assertThrows(
                NotFoundException.class,
                () -> filmStorage.getFilmById(50)
        );
        Assertions.assertEquals("Некорректный id фильмa", wrongFilm.getMessage());
    }

    @Test
    void getAllFilms_returnListSizeAndFilms() {
        Film film1 = new Film("Терминатор 1", "Шварцнеггер плохой", LocalDate.of(1984, Month.JANUARY, 1), 100);
        Film film2 = new Film("Терминатор 2", "Шварцнеггер хороший", LocalDate.of(1990, Month.JANUARY, 1), 100);
        film1.setMpa(mpaDao.getMpaById(3));
        film2.setMpa(mpaDao.getMpaById(3));
        filmController.addFilm(film1);
        filmController.addFilm(film2);

        Assertions.assertEquals(2, filmStorage.getAllFilms().size());
        Assertions.assertEquals(film1, filmStorage.getAllFilms().get(0));
        Assertions.assertEquals(film2, filmStorage.getAllFilms().get(1));
    }

    @Test
    void getAllFilms_noFilms() {
        Assertions.assertEquals(0, filmStorage.getAllFilms().size());
    }

    @Test
    void getTopPopFilms_returnTopSize() {
        User user = userController.createUser(new User("user1@ya.ru", "user1", LocalDate.of(2000, Month.JANUARY, 1)));
        Film film1 = new Film("Терминатор 1", "Шварцнеггер плохой", LocalDate.of(1984, Month.JANUARY, 1), 100);
        Film film2 = new Film("Терминатор 2", "Шварцнеггер хороший", LocalDate.of(1990, Month.JANUARY, 1), 100);
        film1.setMpa(mpaDao.getMpaById(3));
        film2.setMpa(mpaDao.getMpaById(3));
        filmController.addFilm(film1);
        filmController.addFilm(film2);
        filmLikesDao.likeFilm(film1.getId(), user.getId());
        filmLikesDao.likeFilm(film2.getId(), user.getId());

        Assertions.assertEquals(2, filmStorage.getTopPopFilms(5).size());
        Assertions.assertEquals(film1.getId(), filmStorage.getTopPopFilms(5).get(0).getId());
        Assertions.assertEquals(film2.getId(), filmStorage.getTopPopFilms(5).get(1).getId());
    }

    @Test
    void getTopPopFilms_noTopFilms() {
        Assertions.assertEquals(0, filmStorage.getAllFilms().size());
    }

    @Test
    void addFilm_returnFilm() {
        Film film = new Film("Терминатор 1", "Шварцнеггер плохой", LocalDate.of(1984, Month.JANUARY, 1), 100);
        film.setMpa(mpaDao.getMpaById(3));
        filmStorage.addFilm(film);

        Assertions.assertEquals(film, filmStorage.getFilmById(film.getId()));
    }

    @Test
    void updateFilm_returnEqualFilm() {
        Film film = new Film("Терминатор 1", "Шварцнеггер плохой", LocalDate.of(1984, Month.JANUARY, 1), 100);
        film.setMpa(mpaDao.getMpaById(3));
        filmStorage.addFilm(film);

        Film filmUpd = new Film("Терминатор 1", "Шварцнеггер крутой", LocalDate.of(1984, Month.JANUARY, 10), 105);
        filmUpd.setId(film.getId());
        filmUpd.setMpa(mpaDao.getMpaById(4));
        filmStorage.updateFilm(filmUpd);

        Assertions.assertEquals(filmUpd, filmStorage.getFilmById(filmUpd.getId()));
    }

}
