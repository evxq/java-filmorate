package ru.yandex.practicum.filmorate.dao.impl;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.dao.GenresDao;
import ru.yandex.practicum.filmorate.exception.NotFoundException;

import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class GenresDaoImplTest {

    private final GenresDao genresDao;

    @Test
    void getGenreById_returnGenreName() {
        Assertions.assertEquals("Genre", genresDao.getGenreById(1).getClass().getSimpleName());
        Assertions.assertEquals(1, genresDao.getGenreById(1).getId());
        Assertions.assertEquals("Комедия", genresDao.getGenreById(1).getName());
    }

    @Test
    void getGenreById_wrongGenreId() {
        NotFoundException wrongGenre = assertThrows(
                NotFoundException.class,
                () -> genresDao.getGenreById(10)
        );
        Assertions.assertEquals("Некорректный жанр", wrongGenre.getMessage());
    }

    @Test
    void getAllGenres_returnListSizeAndNames() {
        Assertions.assertEquals(6, genresDao.getAllGenres().size());
        Assertions.assertEquals("Комедия", genresDao.getAllGenres().get(0).getName());
        Assertions.assertEquals("Драма", genresDao.getAllGenres().get(1).getName());
        Assertions.assertEquals("Мультфильм", genresDao.getAllGenres().get(2).getName());
        Assertions.assertEquals("Триллер", genresDao.getAllGenres().get(3).getName());
        Assertions.assertEquals("Документальный", genresDao.getAllGenres().get(4).getName());
        Assertions.assertEquals("Боевик", genresDao.getAllGenres().get(5).getName());
    }

}
