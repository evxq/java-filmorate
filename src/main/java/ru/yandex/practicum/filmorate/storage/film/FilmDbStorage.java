package ru.yandex.practicum.filmorate.storage.film;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dao.*;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Component
@Primary
@RequiredArgsConstructor
@Slf4j
public class FilmDbStorage implements FilmStorage {

    private final JdbcTemplate jdbcTemplate;
    private final FilmLikesDao filmLikesDao;
    private final FilmGenreDao filmGenreDao;
    private final MpaDao mpaDao;
    private final GenresDao genresDao;
    private final FilmMpaDao filmMpaDao;

    @Override
    public Film getFilmById(Integer filmId) {
        String sqlQuery = "SELECT * FROM films WHERE film_id = ?";
        Film film;
        try {
            film = jdbcTemplate.queryForObject(sqlQuery, this::makeFilm, filmId);
        } catch (DataAccessException e) {
            log.warn("Вызван некорректный id фильмa id = {}", filmId);
            throw new NotFoundException("Некорректный id фильмa");
        }
        return film;
    }

    @Override
    public List<Film> getAllFilms() {
        String sqlQuery = "SELECT * FROM films";
        log.debug("Из БД вызван список всех фильмов");
        return jdbcTemplate.query(sqlQuery, this::makeFilm);
    }

    @Override
    public List<Film> getTopPopFilms(Integer count) {
        String sqlQuery = "SELECT * FROM films WHERE film_id IN" +
                "(SELECT film_id FROM film_likes GROUP BY film_id ORDER BY COUNT(user_id) DESC LIMIT ?)";
        List<Film> topList = jdbcTemplate.query(sqlQuery, this::makeFilm, count);
        if (topList.isEmpty()) {
            String sqlQueryAll = "SELECT * FROM films LIMIT ?";
            topList = jdbcTemplate.query(sqlQueryAll, this::makeFilm, count);
        }

        return topList;
    }

    private Film makeFilm(ResultSet rs, int rowNum) throws SQLException {
        Integer filmId = rs.getInt("film_id");
        Film film = new Film(
                rs.getString("name"),
                rs.getString("description"),
                rs.getDate("releaseDate").toLocalDate(),
                rs.getInt("duration"));
        film.setId(filmId);
        film.setMpa(filmMpaDao.getFilmMpa(filmId));                         // вызвали рейтинг фильма и присвоили объекту фильма
        film.getLikes().addAll(filmLikesDao.getFilmLikes(filmId));          // вызвали список лайков и присвоили объекту фильма
        film.getGenres().addAll(filmGenreDao.getFilmGenres(filmId));        // вызвали список жанров и присвоили объекту фильма
        log.debug("Из БД вызван фильм id = {}", filmId);

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
        filmMpaDao.addMpaToFilm(filmId, film.getMpa().getId());            // Добавили mpa фильма в БД film_mpa
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
        filmMpaDao.updateMpaToFilm(filmId, film.getMpa().getId());          // Добавили mpa фильма в БД film_mpa
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

}
