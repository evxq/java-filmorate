package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.time.LocalDate;
import java.time.Month;
import java.util.List;

@RestController
@RequestMapping("/films")
@RequiredArgsConstructor
@Slf4j
public class FilmController {

    private final FilmService filmService;

    @GetMapping
    public List<Film> getAllFilms() {
        return filmService.getAllFilms();
    }

    @PostMapping
    public Film addFilm(@RequestBody Film film) {
        validateFilm(film);
        return filmService.addFilm(film);
    }

    @PutMapping
    public Film updateFilm(@RequestBody Film film) {
        validateFilm(film);
        return filmService.updateFilm(film);
    }

    @GetMapping("/{id}")                                             // получить фильм по id
    public Film getFilmById(@PathVariable Integer id) {
        validateId(id);
        return filmService.getFilmById(id);
    }

    @PutMapping("/{id}/like/{userId}")                               // пользователь ставит лайк фильму
    public void likeFilm(@PathVariable Integer id, @PathVariable Integer userId) {
        validateId(id);
        validateId(userId);
        filmService.likeFilm(id, userId);
    }

    @DeleteMapping("/{id}/like/{userId}")                            // пользователь удаляет лайк
    public void revokeLikeToFilm(@PathVariable Integer id, @PathVariable Integer userId) {
        validateId(id);
        validateId(userId);
        filmService.revokeLikeToFilm(id, userId);
    }

    @GetMapping("/popular")                                          //  рейтинг из первых count фильмов
    public List<Film> getTopPopFilms(@RequestParam(defaultValue = "10", required = false) Integer count) {
        return filmService.getTopPopFilms(count);
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

    private void validateId(Integer id) {
        if (id < 0) {
            String errorMessage = "Передан некорректный id";
            log.debug(errorMessage);
            throw new NotFoundException(errorMessage);
        }
    }

}