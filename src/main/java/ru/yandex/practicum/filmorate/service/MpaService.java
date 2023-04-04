package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.MpaDao;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class MpaService {

    private final MpaDao mpaDao;

    public Mpa getMpaById(Integer mpaId) {
        Mpa mpa = mpaDao.getMpaById(mpaId);
        if (mpa == null) {
            String errorMessage = "Такой рейтинг отсутствует";
            log.debug(errorMessage);
            throw new NotFoundException(errorMessage);
        }
        log.debug("Вызван рейтинг id = {}", mpaId);
        return mpa;
    }

    public List<Mpa> getAllRatings() {
        log.debug("Вызван список всех рейтингов");
        return mpaDao.getAllRatings();
    }

}
