package com.matsu.springrestart.service;

import com.matsu.springrestart.domain.Anime;
import com.matsu.springrestart.exception.BadRequestException;
import com.matsu.springrestart.repository.AnimeRepository;
import com.matsu.springrestart.util.AnimeCreator;
import com.matsu.springrestart.util.AnimePostRequestBodyCreator;
import com.matsu.springrestart.util.AnimePutRequestBodyCreator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@ExtendWith(SpringExtension.class)
class AnimeServiceTest {


    @Mock
    private AnimeRepository animeRepository;


    @InjectMocks
    private AnimeService animeService;


    @Test
    @DisplayName("Successful to list all anime with pageable")
    void return_list_of_anime_on_page_successful() {
        PageImpl<Anime> animeImplPage = new PageImpl<>(List.of(AnimeCreator.createValidAnime()));
        when(animeRepository.findAll(ArgumentMatchers.any(PageRequest.class))).thenReturn(animeImplPage);

        String expectedName = AnimeCreator.createValidAnime().getName();
        Page<Anime> animePage = animeService.listAll(PageRequest.of(0, 20));

        assertNotNull(animePage);
        assertFalse(animePage.toList().isEmpty());
        assertEquals(1, animePage.toList().size());
        assertEquals(expectedName, animePage.toList().get(0).getName());
    }

    @Test
    @DisplayName("Successful to list all anime")
    void return_list_of_anime_successful() {

        when(animeRepository.findAll()).thenReturn(List.of(AnimeCreator.createValidAnime()));

        String expectedName = AnimeCreator.createValidAnime().getName();
        List<Anime> animeList = animeService.listAllNonPageable();

        assertNotNull(animeList);
        assertFalse(animeList.isEmpty());
        assertEquals(1, animeList.size());
        assertEquals(expectedName, animeList.get(0).getName());
    }

    @Test
    @DisplayName("Successful get anime by id")
    void return_anime_by_id_successful() {

        when(animeRepository
                .findById(ArgumentMatchers.anyLong()))
                .thenReturn(Optional.of(AnimeCreator.createValidAnime()));

        Long expectedId = AnimeCreator.createValidAnime().getId();
        Anime anime = animeService.findByIdOrThrowBadRequest(1L);

        assertNotNull(anime);
        assertEquals(expectedId, anime.getId());
    }

    @Test
    @DisplayName("Failed get anime by id")
    void return_anime_by_id_failed() {

        when(animeRepository
                .findById(ArgumentMatchers.anyLong()))
                .thenReturn(Optional.empty());

        assertThrows(BadRequestException.class, () -> animeService.findByIdOrThrowBadRequest(anyLong()));
        verify(animeRepository).findById(anyLong());
    }

    @Test
    @DisplayName("Successful get anime by name")
    void return_anime_by_name_successful() {

        when(animeRepository
                .findByName(ArgumentMatchers.anyString()))
                .thenReturn(List.of(AnimeCreator.createValidAnime()));

        String expectedName = AnimeCreator.createValidAnime().getName();
        List<Anime> animeList = animeService.findByName("anime");

        assertNotNull(animeList);
        assertFalse(animeList.isEmpty());
        assertEquals(1, animeList.size());
        assertEquals(expectedName, animeList.get(0).getName());
    }

    @Test
    @DisplayName("Failed get anime by name")
    void return_anime_by_name_failed() {

        when(animeRepository
                .findByName(ArgumentMatchers.anyString()))
                .thenReturn(Collections.emptyList());

        List<Anime> animeList = animeService.findByName("anime");

        assertNotNull(animeList);
        assertTrue(animeList.isEmpty());
    }

    @Test
    @DisplayName("Successful save anime")
    void save_anime_successful() {
        when(animeRepository
                .save(ArgumentMatchers.any(Anime.class)))
                .thenReturn(AnimeCreator.createValidAnime());

        Anime anime = animeService.save(AnimePostRequestBodyCreator.createAnimeToBeSaved());

        assertNotNull(anime);
        assertEquals(anime, AnimeCreator.createValidAnime());
    }

    @Test
    @DisplayName("Successful updated anime")
    void updated_anime_successful() {
        when(animeRepository
                .findById(ArgumentMatchers.anyLong()))
                .thenReturn(Optional.of(AnimeCreator.createValidAnime()));

        assertDoesNotThrow(() -> animeService.replace(AnimePutRequestBodyCreator.createAnimeToBeUpdated()));
        verify(animeRepository).save(ArgumentMatchers.any(Anime.class));
    }

    @Test
    @DisplayName("Successful deleted anime")
    void deleted_anime_successful() {
        doNothing().when(animeRepository)
                .delete(ArgumentMatchers.any(Anime.class));
        when(animeRepository
                .findById(ArgumentMatchers.anyLong()))
                .thenReturn(Optional.of(AnimeCreator.createValidAnime()));

        assertDoesNotThrow(() -> animeService.delete(1L));
        verify(animeRepository).delete(ArgumentMatchers.any(Anime.class));
    }
}