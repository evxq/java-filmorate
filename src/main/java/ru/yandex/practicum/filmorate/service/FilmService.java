package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class FilmService {

    private final FilmStorage filmStorage;
    private final UserStorage userStorage;

    @Autowired
    public FilmService(FilmStorage filmStorage, UserStorage userStorage) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
    }

    public void likeFilm(Integer filmId, Integer userId) {
        validateFilmAndUserById(filmId, userId);
        filmStorage.getFilmById(filmId).addLike(userId);
        log.debug("Фильм id={} получил лайк от пользователя id={}", filmId, userId);
    }

    public void revokeLikeToFilm(Integer filmId, Integer userId) {
        validateFilmAndUserById(filmId, userId);
        if (!filmStorage.getFilmById(filmId).getLikes().contains(userId)) {
            log.debug("Фильм id={} не получал лайк от пользователя id={}", filmId, userId);
            throw new ValidationException(String.format("Фильм id=%d не получал лайк от пользователя id=%d", filmId, userId));
        }
        filmStorage.getFilmById(filmId).revokeLike(userId);
        log.debug("Пользователь id={} удалил лайк для фильма id={}", userId, filmId);
    }

    public List<Film> getTopPopFilms(Integer count) {
        if (count < 1) {
            log.debug("Размер списка фильмов указан меньше 1");
            throw new ValidationException("Размер списка не может быть меньше 1");
        }
        if (filmStorage.getAllFilms().isEmpty()) {
            log.debug("Список фильмов пуст");
            throw new ValidationException("Список фильмов пуст");
        }
        List<Film> sorted = filmStorage.getAllFilms().stream()
                            .sorted(Comparator.comparingInt(f -> f.getLikes().size()))
                            .collect(Collectors.toList());
        Collections.reverse(sorted);
        List<Film> limited = sorted.stream().limit(count).collect(Collectors.toList());
        log.debug("Получен список {} самых популярных фильмов", count);
        return limited;
    }

    private void validateFilmAndUserById(int filmId, int userId) {
        try {
            filmStorage.getFilmById(filmId);
        } catch (NotFoundException e) {
            log.debug("Фильм id={} не найден", filmId);
            throw new NotFoundException(String.format("Фильм id=%d не найден", filmId));
        }
        try {
            userStorage.getUserById(userId);
        } catch (NotFoundException e) {
            log.debug("Пользователь id={} не найден", userId);
            throw new NotFoundException(String.format("Пользователь id=%d не найден", userId));
        }

    }

}
