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
public class MpaServiceTest {

    private final MpaService mpaService;

    @Test
    void getMpaById_returnMpaName() {
        Assertions.assertEquals("G", mpaService.getMpaById(1).getName());
        Assertions.assertEquals("PG", mpaService.getMpaById(2).getName());
        Assertions.assertEquals("PG-13", mpaService.getMpaById(3).getName());
        Assertions.assertEquals("R", mpaService.getMpaById(4).getName());
        Assertions.assertEquals("NC-17", mpaService.getMpaById(5).getName());
    }

    @Test
    void getMpaById_wrongId() {
        NotFoundException nonUser = assertThrows(
                NotFoundException.class,
                () -> mpaService.getMpaById(10)
        );
        Assertions.assertEquals("Некорректный рейтинг", nonUser.getMessage());
    }

    @Test
    void getAllRatings_returnMpaListSize() {
        Assertions.assertEquals(5, mpaService.getAllRatings().size());
        Assertions.assertEquals("PG-13", mpaService.getAllRatings().get(2).getName());
        Assertions.assertEquals("NC-17", mpaService.getAllRatings().get(4).getName());
    }

}
