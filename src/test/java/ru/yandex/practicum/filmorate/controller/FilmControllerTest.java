package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.time.Month;

import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
public class FilmControllerTest {

    FilmController filmController = new FilmController();

    @Test
    void getAllFilms() {
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
    void updateFilm_emptyFilmMap() {
        ValidationException nonuser0 = assertThrows(
                ValidationException.class,
                () -> filmController.updateFilm(new Film("Терминатор 1", "Шварцнеггер плохой", LocalDate.of(1984, Month.JANUARY, 1), 100))
        );
        Assertions.assertEquals("Такой фильм отсутствует", nonuser0.getMessage());
    }

}