package ru.yandex.practicum.filmorate.dao;

import ru.yandex.practicum.filmorate.model.Genre;

import java.util.Collection;
import java.util.List;

public interface FilmGenreDao {

    void addGenresToFilm(Integer filmId, List<Genre> genres);

    void deleteGenresForFilm(Integer filmId);

    Collection<Genre> getFilmGenres(Integer filmId);

}
