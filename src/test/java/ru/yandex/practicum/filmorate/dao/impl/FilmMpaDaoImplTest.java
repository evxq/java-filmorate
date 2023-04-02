package ru.yandex.practicum.filmorate.dao.impl;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.dao.FilmDbDao;
import ru.yandex.practicum.filmorate.dao.FilmMpaDao;
import ru.yandex.practicum.filmorate.dao.MpaDao;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.time.Month;

import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class FilmMpaDaoImplTest {

    private final FilmMpaDao filmMpaDao;
    private final MpaDao mpaDao;
    private final FilmDbDao filmDbDao;
    Film film;

    @BeforeEach
    void createFilm() {
        film = new Film("Терминатор 1", "Шварцнеггер плохой", LocalDate.of(1984, Month.JANUARY, 1), 100);
        film.setMpa(mpaDao.getMpaById(2));
        filmDbDao.addFilm(film);
    }

    @Test
    void updateMpaToFilm_returnMpaId() {
        filmMpaDao.updateMpaToFilm(film.getId(), 3);

        Assertions.assertEquals(3, filmDbDao.getFilmById(film.getId()).getMpa().getId());
    }

    @Test
    void addMpaToFilm_returnMpaId() {
        Assertions.assertEquals(2, filmDbDao.getFilmById(film.getId()).getMpa().getId());
    }

    @Test
    void updateMpaToFilm_wrongMpaId() {
        NotFoundException wrongMpa = assertThrows(
                NotFoundException.class,
                () -> filmMpaDao.updateMpaToFilm(film.getId(), 100)
        );
        Assertions.assertEquals("Некорректный id рейтинга", wrongMpa.getMessage());
    }

    @Test
    void getFilmMpa_returnMpaId() {
        Assertions.assertEquals(2, filmMpaDao.getFilmMpa(film.getId()).getId());
    }

    @Test
    void getFilmMpa_wrongFilmId() {
        NotFoundException wrongFilm = assertThrows(
                NotFoundException.class,
                () -> Assertions.assertEquals(2, filmMpaDao.getFilmMpa(100).getId())
        );
        Assertions.assertEquals("Некорректный id фильма", wrongFilm.getMessage());
    }

}
