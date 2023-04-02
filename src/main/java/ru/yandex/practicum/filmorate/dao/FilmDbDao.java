package ru.yandex.practicum.filmorate.dao;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

public interface FilmDbDao {

    Film getFilmById(Integer id);

    List<Film> getAllFilms();

    List<Film> getTopPopFilms(Integer count);

    Film addFilm(Film film);

    Film updateFilm(Film film);
}
