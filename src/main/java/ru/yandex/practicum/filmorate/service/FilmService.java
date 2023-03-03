package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class FilmService {

    private final FilmStorage filmStorage;
    private final UserService userService;

    public Film getFilmById(Integer id) {
        Film film = filmStorage.getFilmById(id);
        if (film == null) {
            String errorMessage = "Такой фильм отсутствует";
            log.debug(errorMessage);
            throw new NotFoundException(errorMessage);
        }
        log.debug("Получен фильм id={}", id);
        return film;
    }

    public List<Film> getAllFilms() {
        log.debug("Вызван список всех фильмов. Общее количестов фильмов: {}", filmStorage.getAllFilms().size());
        return filmStorage.getAllFilms();
    }

    public Film addFilm(Film film) {
        filmStorage.addFilm(film);
        log.debug("Добавлен новый фильм {}, присвоен id={}", film.getName(), film.getId());
        return film;
    }

    public Film updateFilm(Film film) {
        Film updFilm = filmStorage.updateFilm(film);
        if (updFilm == null) {
            String errorMessage = "Такой фильм отсутствует";
            log.debug(errorMessage);
            throw new NotFoundException(errorMessage);
        } else {
            log.debug("Обновлены данные о фильме: {}, id={}", film.getName(), film.getId());
            return updFilm;
        }
    }

    public void likeFilm(Integer filmId, Integer userId) {
        checkExistenceOfFilmAndUser(filmId, userId);
        getFilmById(filmId).getLikes().add(userId);
        log.debug("Фильм id={} получил лайк от пользователя id={}", filmId, userId);
    }

    public void revokeLikeToFilm(Integer filmId, Integer userId) {
        checkExistenceOfFilmAndUser(filmId, userId);
        getFilmById(filmId).getLikes().remove(userId);
        log.debug("Пользователь id={} удалил лайк для фильма id={}", userId, filmId);
    }

    public List<Film> getTopPopFilms(Integer count) {
        if (count < 1) {
            log.debug("Размер списка фильмов указан меньше 1");
            throw new ValidationException("Размер списка не может быть меньше 1");
        }
        List<Film> sorted = getAllFilms().stream()
                            .sorted((f1, f2) -> f2.getLikes().size() - f1.getLikes().size())
                            .limit(count)
                            .collect(Collectors.toList());
        log.debug("Получен список {} самых популярных фильмов", count);
        return sorted;
    }

    private void checkExistenceOfFilmAndUser(int filmId, int userId) {
        getFilmById(filmId);
        userService.getUserById(userId);
    }

}
