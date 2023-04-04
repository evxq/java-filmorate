package ru.yandex.practicum.filmorate.dao.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dao.*;
import ru.yandex.practicum.filmorate.dao.FilmDbDao;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.*;

@Component
@Primary
@RequiredArgsConstructor
@Slf4j
public class FilmDbDaoImpl implements FilmDbDao {

    private final JdbcTemplate jdbcTemplate;
    private final FilmGenreDao filmGenreDao;
    private final MpaDao mpaDao;
    private final GenresDao genresDao;

    private static final String SELECT_FILM_WITH_ALL_DATA =
            "SELECT f.film_id, f.name, f.description, f.releaseDate, f.duration, " +
                    "f.mpa_id, mpa.name mpa_name, " +
                    "fg.genre_id, g.name genre_name, " +
                    "fl.user_id " +
                    "FROM films f " +
                    "LEFT JOIN mpa ON f.mpa_id  = mpa.mpa_id " +
                    "LEFT JOIN film_genre fg ON f.film_id = fg.film_id " +
                    "LEFT JOIN genres g ON fg.genre_id = g.genre_id " +
                    "LEFT JOIN film_likes fl ON f.film_id = fl.film_id ";

    @Override
    public Film getFilmById(Integer filmId) {
        Film film;
        try {
            film = jdbcTemplate.query(SELECT_FILM_WITH_ALL_DATA + "WHERE f.film_id = ?", this::extractFilm, filmId);
            if (film == null) {
                log.warn("Вызван некорректный id фильмa id = {}", filmId);
                throw new NotFoundException("Некорректный id фильмa");
            }
        } catch (DataAccessException | NullPointerException e) {
            log.warn("Вызван некорректный id фильмa id = {}", filmId);
            throw new NotFoundException("Некорректный id фильмa");
        }
        return film;
    }

    @Override
    public List<Film> getAllFilms() {
        List<Film> filmList;
        try {
            filmList = jdbcTemplate.query(SELECT_FILM_WITH_ALL_DATA, extractAllFilms);
            log.debug("Из БД вызван список всех фильмов");
        } catch (DataAccessException | NullPointerException e) {
            log.warn("Ошибка при вызове списка всех фильмов");
            throw new NotFoundException("Ошибка при вызове списка всех фильмов");
        }
        return filmList;
    }

    @Override
    public List<Film> getTopPopFilms(Integer count) {
        String sqlQuery = SELECT_FILM_WITH_ALL_DATA + "WHERE f.film_id IN" +
                "(SELECT fl.film_id FROM film_likes GROUP BY fl.film_id ORDER BY COUNT(fl.user_id) DESC LIMIT ?)";
        List<Film> topList = jdbcTemplate.query(sqlQuery, extractAllFilms, count);
        if (topList.isEmpty()) {
            String sqlQueryAll = SELECT_FILM_WITH_ALL_DATA + "LIMIT ?";
            topList = jdbcTemplate.query(sqlQueryAll, extractAllFilms, count);
        }
        return topList;
    }

    private Film extractFilm(ResultSet rs) throws SQLException {
        Map<Integer, Film> filmMap = new HashMap<>();

        return makeFilm(filmMap, rs);
    }

    private final ResultSetExtractor<List<Film>> extractAllFilms = rs -> {
        Map<Integer, Film> filmMap = new HashMap<>();
        makeFilm(filmMap, rs);

        return new ArrayList<>(filmMap.values());
    };

    private Film makeFilm(Map<Integer, Film> map, ResultSet rs) throws SQLException {
        Film film = null;
        while (rs.next()) {
            Integer filmId = rs.getInt("film_id");
            film = map.get(filmId);
            if (film == null) {
                String name = rs.getString("name");
                String description = rs.getString("description");
                LocalDate releaseDate = rs.getDate("releaseDate").toLocalDate();
                Integer duration = rs.getInt("duration");
                film = new Film(name, description, releaseDate, duration);
                film.setId(rs.getInt("film_id"));
                film.setMpa(new Mpa(rs.getInt("mpa_id"), rs.getString("mpa_name")));
                map.put(filmId, film);
            }
            int genreId = rs.getInt("genre_id");
            if (genreId != 0) {
                film.getGenres().add(new Genre(genreId, rs.getString("genre_name")));
            }
            int userId = rs.getInt("user_id");
            if (userId != 0) {
                film.getLikes().add(userId);
            }
        }
        return film;
    }

    @Override
    public Film addFilm(Film film) {
        String sqlQuery = "INSERT INTO films (name, description, releaseDate, duration) " +
                "VALUES (?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement stmt = connection.prepareStatement(sqlQuery, new String[]{"film_id"});
            stmt.setString(1, film.getName());
            stmt.setString(2, film.getDescription());
            stmt.setDate(3, Date.valueOf(film.getReleaseDate()));
            stmt.setInt(4, film.getDuration());
            return stmt;
        }, keyHolder);

        Integer filmId = keyHolder.getKey().intValue();
        film.setId(filmId);
        film.setMpa(mpaDao.getMpaById(film.getMpa().getId()));             // присвоили объекту фильма объект mpa
        updateMpaToFilm(filmId, film.getMpa().getId());                    // Добавили mpa фильма в БД film_mpa
        filmGenreDao.addGenresToFilm(filmId, film.getGenres());            // Добавили список жанров фильма в БД
        log.debug("В БД добавлен фильм id = {}", filmId);

        return film;
    }

    @Override
    public Film updateFilm(Film film) {
        String sqlQuery = "UPDATE films SET " +
                "name = ?, description = ?, releaseDate = ?, duration = ? " +
                "WHERE film_id = ?";
        Integer filmId = film.getId();
        int i = jdbcTemplate.update(sqlQuery,
                film.getName(),
                film.getDescription(),
                Date.valueOf(film.getReleaseDate()),
                film.getDuration(),
                filmId);
        if (i == 0) {
            log.debug("Некорректные данные фильма");
            throw new NotFoundException("Некорректные данные фильма");
        }
        film.setMpa(mpaDao.getMpaById(film.getMpa().getId()));              // присвоили объекту фильма объект mpa
        updateMpaToFilm(filmId, film.getMpa().getId());                     // Добавили mpa фильма в БД film_mpa
        List<Genre> genresList = new ArrayList<>();
        List<Integer> idList = new ArrayList<>();
        for (Genre genre : film.getGenres()) {
            if (!idList.contains(genre.getId())) {
                genresList.add(genresDao.getGenreById(genre.getId()));
            }
            idList.add(genre.getId());
        }
        film.setGenres(genresList);
        filmGenreDao.deleteGenresForFilm(filmId);
        filmGenreDao.addGenresToFilm(filmId, film.getGenres());
        log.debug("В БД обновлен фильм id = {}", filmId);

        return film;
    }

    private void updateMpaToFilm(Integer filmId, Integer mpaId) {
        String sqlQuery = "UPDATE films SET mpa_id = ? WHERE film_id = ?";
        try {
            jdbcTemplate.update(sqlQuery, mpaId, filmId);
            log.debug("Фильму id = {} присвоен рейтинг id = {}", filmId, mpaId);
        } catch (DataIntegrityViolationException e) {
            throw new NotFoundException("Некорректный id рейтинга");
        }
    }

}
