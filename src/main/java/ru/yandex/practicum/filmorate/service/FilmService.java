package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.FilmLikesDao;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.dao.FilmDbDao;

import java.util.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class FilmService {

    private final FilmLikesDao filmLikesDao;
    private final FilmDbDao filmStorage;
    private final UserService userService;

    public Film getFilmById(Integer id) {
        Film film = filmStorage.getFilmById(id);
        if (film == null) {
            String errorMessage = "Такой фильм отсутствует";
            log.debug(errorMessage);
            throw new NotFoundException(errorMessage);
        }
        log.debug("Получен фильм id = {}", id);
        return film;
    }

    public List<Film> getAllFilms() {
        log.debug("Вызван список всех фильмов");
        return filmStorage.getAllFilms();
    }

    public Film addFilm(Film film) {
        if (film == null) {
            String errorMessage = "Данные фильма не переданы";
            log.debug(errorMessage);
            throw new ValidationException(errorMessage);
        }
        Film newFilm = filmStorage.addFilm(film);
        log.debug("Добавлен новый фильм {}, присвоен id = {}", newFilm.getName(), newFilm.getId());
        return newFilm;
    }

    public Film updateFilm(Film film) {
        Film updFilm = filmStorage.updateFilm(film);
        if (updFilm == null) {
            String errorMessage = "Такой фильм отсутствует";
            log.debug(errorMessage);
            throw new NotFoundException(errorMessage);
        } else {
            log.debug("Обновлены данные о фильме: {}, id = {}", updFilm.getName(), updFilm.getId());
            return updFilm;
        }
    }

    public void likeFilm(Integer filmId, Integer userId) {
        filmLikesDao.likeFilm(filmId, userId);
        log.debug("Фильм id = {} получил лайк от пользователя id = {}", filmId, userId);
    }

    public void revokeLikeToFilm(Integer filmId, Integer userId) {
        filmLikesDao.revokeLikeToFilm(filmId, userId);
        log.debug("Пользователь id = {} удалил лайк для фильма id = {}", userId, filmId);
    }

    public List<Film> getTopPopFilms(Integer count) {
        if (count < 1) {
            log.debug("Размер списка фильмов указан меньше 1");
            throw new ValidationException("Размер списка не может быть меньше 1");
        }
        log.debug("Вызван список {} самых популярных фильмов", count);

        return filmStorage.getTopPopFilms(count);
    }

}
