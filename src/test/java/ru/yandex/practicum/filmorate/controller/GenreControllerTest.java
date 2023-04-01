package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.exception.NotFoundException;

import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class GenreControllerTest {

    private final GenreController genreController;

    @Test
    void getGenreById_returnGenreName() {
        Assertions.assertEquals("Комедия", genreController.getGenreById(1).getName());
        Assertions.assertEquals("Драма", genreController.getGenreById(2).getName());
        Assertions.assertEquals("Мультфильм", genreController.getGenreById(3).getName());
        Assertions.assertEquals("Триллер", genreController.getGenreById(4).getName());
        Assertions.assertEquals("Документальный", genreController.getGenreById(5).getName());
        Assertions.assertEquals("Боевик", genreController.getGenreById(6).getName());
    }

    @Test
    void getGenreById_wrongId() {
        NotFoundException nonUser = assertThrows(
                NotFoundException.class,
                () -> genreController.getGenreById(10)
        );
        Assertions.assertEquals("Некорректный жанр", nonUser.getMessage());
    }

    @Test
    void getAllGenres_returnGenreListSize() {
        Assertions.assertEquals(6, genreController.getAllGenres().size());
        Assertions.assertEquals("Мультфильм", genreController.getAllGenres().get(2).getName());
        Assertions.assertEquals("Боевик", genreController.getAllGenres().get(5).getName());
    }

}
