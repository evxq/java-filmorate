package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.GenresDao;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class GenreService {

    private final GenresDao genresDao;

    public Genre getGenreById(Integer genreId) {
        Genre genre = genresDao.getGenreById(genreId);
        if (genre == null) {
            String errorMessage = "Такой жанр отсутствует";
            log.debug(errorMessage);
            throw new NotFoundException(errorMessage);
        }
        log.debug("Вызван жанр id = {}", genreId);
        return genre;
    }

    public List<Genre> getAllGenres() {
        log.debug("Вызван список всех жанров");
        return genresDao.getAllGenres();
    }

}
