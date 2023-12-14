package com.matsu.springrestart.repository;

import com.matsu.springrestart.domain.Anime;

import com.matsu.springrestart.util.AnimeCreator;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
@DataJpaTest
@DisplayName("Tests for Anime repository")
class AnimeRepositoryTest {

    @Autowired
    private AnimeRepository animeRepository;



    @Test
    @DisplayName("Successful persist anime")
    void save_persist_anime_when_successful() {

        Anime anime = AnimeCreator.createAnimeToBeSaved();
        Anime savedAnime = this.animeRepository.save(anime);

        assertNotNull(savedAnime);
        assertNotNull(savedAnime.getId());
        assertEquals(savedAnime.getName(), anime.getName());
    }

    @Test
    @DisplayName("Successful updates anime")
    void save_updates_anime_when_successful() {

        Anime anime = AnimeCreator.createAnimeToBeSaved();
        Anime savedAnime = this.animeRepository.save(anime);
        log.info("Anime created id: {} name: {}", savedAnime.getId(), savedAnime.getName());

        savedAnime.setName("Hellsing");

        Anime updatedAnime = this.animeRepository.save(savedAnime);
        log.info("Anime updated id: {} name: {}", updatedAnime.getId(), updatedAnime.getName());

        assertNotNull(updatedAnime);
        assertNotNull(updatedAnime.getId());
        assertEquals(updatedAnime.getName(), savedAnime.getName());
    }

    @Test
    @DisplayName("Successful remove anime")
    void delete_remove_anime_when_successful() {

        Anime anime = AnimeCreator.createAnimeToBeSaved();
        Anime savedAnime = this.animeRepository.save(anime);

        this.animeRepository.delete(savedAnime);

        Optional<Anime> animeOptional = this.animeRepository.findById(savedAnime.getId());

        assertTrue(animeOptional.isEmpty());
    }

    @Test
    @DisplayName("Successful find by name anime")
    void find_by_name_return_list_of_anime_when_successful() {

        Anime anime = AnimeCreator.createAnimeToBeSaved();
        Anime savedAnime = this.animeRepository.save(anime);

        List<Anime> animeList = this.animeRepository.findByName(savedAnime.getName());

        assertFalse(animeList.isEmpty());
        assertTrue(animeList.contains(savedAnime));
    }

    @Test
    @DisplayName("Not found by name anime and return list empty")
    void find_by_name_return_empty_list_of_anime_when_not_found() {

        Anime anime = AnimeCreator.createAnimeToBeSaved();
        this.animeRepository.save(anime);
        String wrongName = "Sailor moon";

        List<Anime> animeList = this.animeRepository.findByName(wrongName);

        assertTrue(animeList.isEmpty());
    }

    @Test
    @DisplayName("Failed persist anime throw exception cause name is blank")
    void save_persist_anime_when_failed() {

        Anime anime = new Anime();

        //this use another lib
       // assertThatThrownBy(()-> this.animeRepository.save(anime)).isInstanceOf(ConstraintViolationException.class);

        assertThrowsExactly(ConstraintViolationException.class,
                ()->this.animeRepository.save(anime));

    }
}