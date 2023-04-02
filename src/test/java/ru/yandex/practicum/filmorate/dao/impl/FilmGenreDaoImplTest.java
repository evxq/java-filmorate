package ru.yandex.practicum.filmorate.dao.impl;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.dao.FilmDbDao;
import ru.yandex.practicum.filmorate.dao.FilmGenreDao;
import ru.yandex.practicum.filmorate.dao.GenresDao;
import ru.yandex.practicum.filmorate.dao.MpaDao;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.time.Month;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class FilmGenreDaoImplTest {

    private final FilmDbDao filmDbDao;
    private final FilmGenreDao filmGenreDao;
    private final GenresDao genresDao;
    private final MpaDao mpaDao;
    Film film;

    @BeforeEach
    void createFilm() {
        film = new Film("Терминатор 1", "Шварцнеггер плохой", LocalDate.of(1984, Month.JANUARY, 1), 100);
        film.setMpa(mpaDao.getMpaById(1));
    }

    @Test
    void addGenresToFilm_returnGenresSize() {
        filmDbDao.addFilm(film);
        filmGenreDao.addGenresToFilm(film.getId(), List.of(genresDao.getGenreById(1)));

        Assertions.assertEquals(1, filmDbDao.getFilmById(film.getId()).getGenres().size());
    }

    @Test
    void addGenresToFilm_wrongGenre() {
        filmDbDao.addFilm(film);

        NotFoundException wrongGenre = assertThrows(
                NotFoundException.class,
                () -> filmGenreDao.addGenresToFilm(film.getId(), List.of(genresDao.getGenreById(10)))
        );
        Assertions.assertEquals("Некорректный жанр", wrongGenre.getMessage());
    }

    @Test
    void getFilmGenres_returnGenresSize() {
        filmDbDao.addFilm(film);
        filmGenreDao.addGenresToFilm(film.getId(), List.of(genresDao.getGenreById(1)));

        Assertions.assertEquals(1, filmGenreDao.getFilmGenres(film.getId()).size());
    }

    @Test
    void getFilmGenres_noGenres() {
        filmDbDao.addFilm(film);

        Assertions.assertEquals(0, filmGenreDao.getFilmGenres(film.getId()).size());
    }

    @Test
    void deleteGenresForFilm_returnGenresSize() {
        film.setGenres(List.of(genresDao.getGenreById(1)));
        filmDbDao.addFilm(film);
        filmGenreDao.deleteGenresForFilm(film.getId());

        Assertions.assertEquals(0, filmDbDao.getFilmById(film.getId()).getGenres().size());
    }

    @Test
    void deleteGenresForFilm_noGenres() {
        film = new Film("Терминатор 1", "Шварцнеггер плохой", LocalDate.of(1984, Month.JANUARY, 1), 100);
        film.setMpa(mpaDao.getMpaById(1));

        filmDbDao.addFilm(film);
        filmGenreDao.deleteGenresForFilm(film.getId());

        Assertions.assertEquals(0, filmDbDao.getFilmById(film.getId()).getGenres().size());
    }

}
