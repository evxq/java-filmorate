package ru.yandex.practicum.filmorate.dao;

import ru.yandex.practicum.filmorate.model.Mpa;

public interface FilmMpaDao {

    void addMpaToFilm(Integer filmId, Integer mpaId);

    void updateMpaToFilm(Integer filmId, Integer mpaId);

    Mpa getFilmMpa(Integer filmId);

}
