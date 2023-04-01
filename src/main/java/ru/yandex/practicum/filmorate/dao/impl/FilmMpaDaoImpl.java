package ru.yandex.practicum.filmorate.dao.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dao.FilmMpaDao;
import ru.yandex.practicum.filmorate.dao.MpaDao;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component
@Primary
@Slf4j
public class FilmMpaDaoImpl implements FilmMpaDao {

    private final JdbcTemplate jdbcTemplate;
    private final MpaDao mpaDao;

    public FilmMpaDaoImpl(JdbcTemplate jdbcTemplate, MpaDao mpaDao) {
        this.jdbcTemplate = jdbcTemplate;
        this.mpaDao = mpaDao;
    }

    @Override
    public void addMpaToFilm(Integer filmId, Integer mpaId) {
        String sqlQuery = "INSERT INTO film_mpa (film_id, mpa_id)" +
                "VALUES (?, ?)";
        try {
            jdbcTemplate.update(sqlQuery, filmId, mpaId);
            log.debug("Фильму id = {} присвоен рейтинг id = {}", filmId, mpaId);
        } catch (DataIntegrityViolationException e) {
            throw new NotFoundException("Некорректный id рейтинга");
        }
    }

    @Override
    public void updateMpaToFilm(Integer filmId, Integer mpaId) {
        String sqlQuery = "UPDATE film_mpa SET mpa_id = ? WHERE film_id = ?";
        try {
            jdbcTemplate.update(sqlQuery, mpaId, filmId);
            log.debug("Фильму id = {} присвоен рейтинг id = {}", filmId, mpaId);
        } catch (DataIntegrityViolationException e) {
            throw new NotFoundException("Некорректный id рейтинга");
        }
    }

    @Override
    public Mpa getFilmMpa(Integer filmId) {
        String sqlQuery = "SELECT mpa_id FROM film_mpa WHERE film_id = ?";
        Integer mpaId;
        try {
            mpaId = jdbcTemplate.query(sqlQuery, (rs, rowNUm) -> getFilmRating(rs), filmId).get(0);
        } catch (RuntimeException e) {
            log.warn("Вызван некорректный фильм id = {}", filmId);
            throw new NotFoundException("Некорректный id фильма");
        }
        log.debug("Вызван рейтинг для фильма id = {}", filmId);

        return mpaDao.getMpaById(mpaId);
    }

    private Integer getFilmRating(ResultSet rs) throws SQLException {
        return rs.getInt("mpa_id");
    }

}
