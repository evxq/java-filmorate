package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.service.MpaService;

import java.util.List;

@RestController
@RequestMapping("/mpa")
@RequiredArgsConstructor
public class MpaController {

    private final MpaService mpaService;

    @GetMapping
    public List<Mpa> getAllRatings() {
        return mpaService.getAllRatings();
    }

    @GetMapping("/{mpaId}")
    public Mpa getMpaById(@PathVariable Integer mpaId) {
        return mpaService.getMpaById(mpaId);
    }

}
