package com.matsu.springrestart.service;

import com.matsu.springrestart.domain.Anime;
import com.matsu.springrestart.repository.AnimeRepository;
import com.matsu.springrestart.requests.AnimePostRequestBody;
import com.matsu.springrestart.requests.AnimePutRequestBody;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;


@Service
@RequiredArgsConstructor
public class AnimeService {

    private final AnimeRepository animeRepository;


    public List<Anime> listAll() {
        return animeRepository.findAll();
    }

    public Anime findByIdOrThrowBadRequest(Long id) {
        return animeRepository.findById(id)
                .orElseThrow(
                        () -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Anime not found"));
    }

    public Anime save(AnimePostRequestBody anime) {
        return animeRepository.save(Anime.builder().name(anime.getName()).build());
    }

    public void delete(Long id) {
        animeRepository.delete(findByIdOrThrowBadRequest(id));
    }

    public void replace(AnimePutRequestBody anime) {

        delete(anime.getId());
        animeRepository.save(Anime.builder().id(anime.getId()).name(anime.getName()).build());
    }
}
