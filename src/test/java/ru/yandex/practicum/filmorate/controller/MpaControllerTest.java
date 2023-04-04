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
public class MpaControllerTest {

    private final MpaController mpaController;

    @Test
    void getMpaById_returnMpaName() {
        Assertions.assertEquals("G", mpaController.getMpaById(1).getName());
        Assertions.assertEquals("PG", mpaController.getMpaById(2).getName());
        Assertions.assertEquals("PG-13", mpaController.getMpaById(3).getName());
        Assertions.assertEquals("R", mpaController.getMpaById(4).getName());
        Assertions.assertEquals("NC-17", mpaController.getMpaById(5).getName());
    }

    @Test
    void getMpaById_wrongId() {
        NotFoundException nonUser = assertThrows(
                NotFoundException.class,
                () -> mpaController.getMpaById(10)
        );
        Assertions.assertEquals("Некорректный рейтинг", nonUser.getMessage());
    }

    @Test
    void getAllRatings_returnMpaListSize() {
        Assertions.assertEquals(5, mpaController.getAllRatings().size());
        Assertions.assertEquals("PG-13", mpaController.getAllRatings().get(2).getName());
        Assertions.assertEquals("NC-17", mpaController.getAllRatings().get(4).getName());
    }

}
