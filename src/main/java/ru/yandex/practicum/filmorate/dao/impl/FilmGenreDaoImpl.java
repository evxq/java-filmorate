package ru.yandex.practicum.filmorate.dao.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dao.FilmGenreDao;
import ru.yandex.practicum.filmorate.dao.GenresDao;
import ru.yandex.practicum.filmorate.model.Genre;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;

@Component
@Primary
@RequiredArgsConstructor
@Slf4j
public class FilmGenreDaoImpl implements FilmGenreDao {

    private final JdbcTemplate jdbcTemplate;
    private final GenresDao genresDao;

    @Override
    public void addGenresToFilm(Integer filmId, List<Genre> genres) {
        for (Genre genre : genres) {
            String sqlQuery = "INSERT INTO film_genre (film_id, genre_id)" +
                    "VALUES (?, ?)";
            jdbcTemplate.update(sqlQuery, filmId, genre.getId());
            log.debug("Фильму id = {} присвоен жанр id = {}", filmId, genre.getId());
        }
    }

    @Override
    public void deleteGenresForFilm(Integer filmId) {
        String sqlQuery = "DELETE FROM film_genre WHERE film_id = ?";
        jdbcTemplate.update(sqlQuery, filmId);
        log.debug("У фильма id = {} удален список жанров", filmId);
    }

    @Override
    public Collection<Genre> getFilmGenres(Integer filmId) {
        String sqlQuery = "SELECT genre_id FROM film_genre WHERE film_id = ?";
        log.debug("Вызван список жанров для фильма id = {}", filmId);

        return jdbcTemplate.query(sqlQuery, (rs, rowNUm) -> getFilmGenre(rs), filmId);
    }

    private Genre getFilmGenre(ResultSet rs) throws SQLException {
        int genreId = rs.getInt("genre_id");
        return genresDao.getGenreById(genreId);
    }

}
