package ru.yandex.practicum.filmorate.service;

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
public class GenreServiceTest {

    private final GenreService genreService;

    @Test
    void getGenreById_returnGenreName() {
        Assertions.assertEquals("Комедия", genreService.getGenreById(1).getName());
        Assertions.assertEquals("Драма", genreService.getGenreById(2).getName());
        Assertions.assertEquals("Мультфильм", genreService.getGenreById(3).getName());
        Assertions.assertEquals("Триллер", genreService.getGenreById(4).getName());
        Assertions.assertEquals("Документальный", genreService.getGenreById(5).getName());
        Assertions.assertEquals("Боевик", genreService.getGenreById(6).getName());
    }

    @Test
    void getGenreById_wrongId() {
        NotFoundException nonUser = assertThrows(
                NotFoundException.class,
                () -> genreService.getGenreById(10)
        );
        Assertions.assertEquals("Некорректный жанр", nonUser.getMessage());
    }

    @Test
    void getAllGenres_returnGenreListSize() {
        Assertions.assertEquals(6, genreService.getAllGenres().size());
        Assertions.assertEquals("Мультфильм", genreService.getAllGenres().get(2).getName());
        Assertions.assertEquals("Боевик", genreService.getAllGenres().get(5).getName());
    }

}
