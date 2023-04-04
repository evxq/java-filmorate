package ru.yandex.practicum.filmorate.dao.impl;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.dao.MpaDao;
import ru.yandex.practicum.filmorate.exception.NotFoundException;

import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class MpaDaoImplTest {

    private final MpaDao mpaDao;

    @Test
    void getMpaById_returnMpaName() {
        Assertions.assertEquals("Mpa", mpaDao.getMpaById(1).getClass().getSimpleName());
        Assertions.assertEquals(1, mpaDao.getMpaById(1).getId());
        Assertions.assertEquals("G", mpaDao.getMpaById(1).getName());
    }

    @Test
    void getMpaById_wrongMpaId() {
        NotFoundException wrongMpa = assertThrows(
                NotFoundException.class,
                () -> mpaDao.getMpaById(10)
        );
        Assertions.assertEquals("Некорректный рейтинг", wrongMpa.getMessage());
    }

    @Test
    void getAllRatings_returnListSizeAndNames() {
        Assertions.assertEquals(5, mpaDao.getAllRatings().size());
        Assertions.assertEquals("G", mpaDao.getAllRatings().get(0).getName());
        Assertions.assertEquals("PG", mpaDao.getAllRatings().get(1).getName());
        Assertions.assertEquals("PG-13", mpaDao.getAllRatings().get(2).getName());
        Assertions.assertEquals("R", mpaDao.getAllRatings().get(3).getName());
        Assertions.assertEquals("NC-17", mpaDao.getAllRatings().get(4).getName());
    }

}
