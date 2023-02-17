package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.time.Month;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@RestController
@RequestMapping("/films")
@Slf4j
public class FilmController {
    private int filmId;
    private HashMap<Integer, Film> filmMap = new HashMap<>();

    @GetMapping
    public List<Film> getAllFilms() {
        log.debug("Общее количестов фильмов: {}", filmMap.size());
        return new ArrayList<>(filmMap.values());
    }

    @PostMapping
    public Film addFilm(@RequestBody Film film) {
        validateFilm(film);
        filmId++;
        film.setId(filmId);
        filmMap.put(filmId, film);
        log.debug("Добавлен новый фильм {}", film.getName());
        return film;
    }

    @PutMapping
    public Film updateFilm(@RequestBody Film film) {
        if (!filmMap.containsKey(film.getId()) ) {
            String errorMessage = "Такой фильм отсутствует";
            log.debug(errorMessage);
            throw new ValidationException(errorMessage);
        } else {
            validateFilm(film);
            filmMap.put(film.getId(), film);
            log.debug("Обновлены данные о фильме: {}", film.getName());
            return film;
        }
    }

    private void validateFilm(Film film) {
        String errorMessage;
        if (film == null) {
            errorMessage = "Данные фильма не переданы";
            log.debug(errorMessage);
            throw new ValidationException(errorMessage);
        }
        if (film.getName() == null || film.getName().isBlank()) {
            errorMessage = "Не указано название фильма";
            log.debug(errorMessage);
            throw new ValidationException(errorMessage);
        }
        if (film.getDescription().getBytes().length > 200) {
            errorMessage = "Размер описания не должен превышать 200 символов";
            log.debug(errorMessage);
            throw new ValidationException(errorMessage);
        }
        if (film.getReleaseDate().isBefore(LocalDate.of(1895, Month.DECEMBER, 28))) {
            errorMessage = "Дата релиза указана некорректна";
            log.debug(errorMessage);
            throw new ValidationException(errorMessage);
        }
        if (film.getDuration() < 0) {
            errorMessage = "Продолжительность фильма указана некорректна";
            log.debug(errorMessage);
            throw new ValidationException(errorMessage);
        }
    }

}