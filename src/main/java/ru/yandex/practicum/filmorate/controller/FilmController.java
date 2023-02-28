package ru.yandex.practicum.filmorate.controller;

import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;

import java.util.List;

@RestController
@RequestMapping("/films")
public class FilmController {

    private final FilmStorage filmStorage;
    private final FilmService filmService;

    public FilmController(FilmStorage filmStorage, FilmService filmService) {
        this.filmStorage = filmStorage;
        this.filmService = filmService;
    }

    @GetMapping
    public List<Film> getAllFilms() {
        return filmStorage.getAllFilms();
    }

    @PostMapping
    public Film addFilm(@RequestBody Film film) {
        return filmStorage.addFilm(film);
    }

    @PutMapping
    public Film updateFilm(@RequestBody Film film) {
        return filmStorage.updateFilm(film);
    }

    @GetMapping("/{id}")                                                                    // получить фильм по id
    public Film getFilmById(@PathVariable Integer id) {
        return filmStorage.getFilmById(id);
    }

    @PutMapping("/{id}/like/{userId}")                                                      // пользователь ставит лайк фильму
    public void likeFilm(@PathVariable Integer id, @PathVariable Integer userId) {
        filmService.likeFilm(id, userId);
    }

    @DeleteMapping("{id}/like/{userId}")                                                    // пользователь удаляет лайк
    public void revokeLikeToFilm(@PathVariable Integer id, @PathVariable Integer userId) {
        filmService.revokeLikeToFilm(id, userId);
    }

    @GetMapping("/popular")                                                                 //  рейтинг из первых count фильмов
    public List<Film> getTopPopFilms(@RequestParam(defaultValue = "10", required = false) Integer count) {
        return filmService.getTopPopFilms(count);
    }

}