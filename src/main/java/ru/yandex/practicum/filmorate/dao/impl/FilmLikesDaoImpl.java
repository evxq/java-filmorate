package ru.yandex.practicum.filmorate.dao.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dao.FilmLikesDao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;

@Component
@Primary
@Slf4j
public class FilmLikesDaoImpl implements FilmLikesDao {

    private final JdbcTemplate jdbcTemplate;

    public FilmLikesDaoImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Collection<Integer> getFilmLikes(Integer filmId) {
        String sqlQuery = "SELECT user_id FROM film_likes WHERE film_id = ?";
        log.debug("Вызван список лайков для фильма id = {}", filmId);

        return jdbcTemplate.query(sqlQuery, (rs, rowNUm) -> getUserIdLike(rs), filmId);
    }

    private Integer getUserIdLike(ResultSet rs) throws SQLException {
        return rs.getInt("user_id");
    }

    @Override
    public void likeFilm(Integer filmId, Integer userId) {
        String sqlQuery = "INSERT INTO film_likes (film_id, user_id)" +
                "VALUES (?, ?)";
        jdbcTemplate.update(sqlQuery, filmId, userId);
    }

    @Override
    public void revokeLikeToFilm(Integer filmId, Integer userId) {
        String sqlQuery = "DELETE FROM film_likes WHERE film_id = ? AND user_id = ?";
        jdbcTemplate.update(sqlQuery, filmId, userId);
    }

}
