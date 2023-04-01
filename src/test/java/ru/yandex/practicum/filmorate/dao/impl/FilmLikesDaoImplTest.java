package ru.yandex.practicum.filmorate.dao.impl;

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
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.time.Month;


@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class FilmLikesDaoImplTest {

    private final JdbcTemplate jdbcTemplate;
    private final FilmLikesDao filmLikesDao;
    private final MpaDao mpaDao;
    private final FilmController filmController;
    private final UserController userController;
    Film film;
    User user;

    @BeforeEach
    void createFilmAndUser() {
        jdbcTemplate.update("DELETE FROM film_likes");
        jdbcTemplate.update("DELETE FROM film_mpa");
        jdbcTemplate.update("DELETE FROM film_genre");
        jdbcTemplate.update("DELETE FROM films");
        film = new Film("Терминатор 1", "Шварцнеггер плохой", LocalDate.of(1984, Month.JANUARY, 1), 100);
        film.setMpa(mpaDao.getMpaById(1));
        filmController.addFilm(film);
        user = userController.createUser(new User("user1@ya.ru", "user1", LocalDate.of(2000, Month.JANUARY, 1)));
    }

    @Test
    void likeFilm_returnLikesSize() {
        filmLikesDao.likeFilm(film.getId(), user.getId());

        Assertions.assertEquals(1, filmController.getFilmById(film.getId()).getLikes().size());
    }

    @Test
    void getFilmLikes_returnLikesSize() {
        filmLikesDao.likeFilm(film.getId(), user.getId());

        Assertions.assertEquals(1, filmLikesDao.getFilmLikes(film.getId()).size());
    }

    @Test
    void getFilmLikes_noLikes() {
        Assertions.assertEquals(0, filmLikesDao.getFilmLikes(film.getId()).size());
    }

    @Test
    void revokeLikeToFilm_returnLikesSize() {
        filmLikesDao.likeFilm(film.getId(), user.getId());
        filmLikesDao.revokeLikeToFilm(film.getId(), user.getId());

        Assertions.assertEquals(0, filmController.getFilmById(film.getId()).getLikes().size());
    }

}
