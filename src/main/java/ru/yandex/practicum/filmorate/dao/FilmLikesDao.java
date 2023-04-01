package ru.yandex.practicum.filmorate.dao;

import java.util.Collection;

public interface FilmLikesDao {

    Collection<Integer> getFilmLikes(Integer filmId);

    void likeFilm(Integer filmId, Integer userId);

    void revokeLikeToFilm(Integer filmId, Integer userId);

}
