package ru.yandex.practicum.filmorate.storage.film;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class InMemoryFilmStorage implements FilmStorage {

    private int filmId;
    private HashMap<Integer, Film> filmMap = new HashMap<>();

    @Override
    public Film getFilmById(Integer id) {
        return filmMap.get(id);
    }

    @Override
    public List<Film> getAllFilms() {
        return new ArrayList<>(filmMap.values());
    }

    @Override
    public Film addFilm(Film film) {
        filmId++;
        film.setId(filmId);
        filmMap.put(filmId, film);
        return film;
    }

    @Override
    public Film updateFilm(Film film) {
        if (!filmMap.containsKey(film.getId())) {
            return null;
        } else {
            filmMap.put(film.getId(), film);
            return filmMap.get(film.getId());
        }
    }

    public void likeFilm(Integer filmId, Integer userId) {
        getFilmById(filmId).getLikes().add(userId);
    }

    public void revokeLikeToFilm(Integer filmId, Integer userId) {
        getFilmById(filmId).getLikes().remove(userId);
    }

    public List<Film> getTopPopFilms(Integer count) {
        List<Film> sorted = getAllFilms().stream()
                .sorted((f1, f2) -> f2.getLikes().size() - f1.getLikes().size())
                .limit(count)
                .collect(Collectors.toList());
        return sorted;
    }

}
