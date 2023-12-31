package com.matsu.springrestart.service;

import com.matsu.springrestart.domain.Anime;
import com.matsu.springrestart.exception.BadRequestException;
import com.matsu.springrestart.mapper.AnimeMapper;
import com.matsu.springrestart.repository.AnimeRepository;
import com.matsu.springrestart.requests.AnimePostRequestBody;
import com.matsu.springrestart.requests.AnimePutRequestBody;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
@RequiredArgsConstructor
public class AnimeService {

    private final AnimeRepository animeRepository;


    public Page<Anime> listAll(Pageable pageable) {
        return animeRepository.findAll(pageable);
    }

    public List<Anime> listAllNonPageable() {
        return animeRepository.findAll();
    }

    public List<Anime> findByName(String name) {
        return animeRepository.findByName(name);
    }

    public Anime findByIdOrThrowBadRequest(Long id) {
        return animeRepository.findById(id)
                .orElseThrow(
                        () -> new BadRequestException("Anime not found"));
    }

    public Anime save(AnimePostRequestBody anime) {
        return animeRepository.save(AnimeMapper.INSTANCE.toAnime(anime));
    }

    public void delete(Long id) {
        Anime anime = findByIdOrThrowBadRequest(id);
        animeRepository.delete(anime);
    }

    public void replace(AnimePutRequestBody anime) {
        Anime anime1 = AnimeMapper.INSTANCE.toAnime(anime);
        anime1.setId(findByIdOrThrowBadRequest(anime.getId()).getId());

        animeRepository.save(anime1);
    }

}
