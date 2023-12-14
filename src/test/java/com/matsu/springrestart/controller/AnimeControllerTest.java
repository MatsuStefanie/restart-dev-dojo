package com.matsu.springrestart.controller;


import com.matsu.springrestart.domain.Anime;
import com.matsu.springrestart.requests.AnimePostRequestBody;
import com.matsu.springrestart.requests.AnimePutRequestBody;
import com.matsu.springrestart.service.AnimeService;
import com.matsu.springrestart.util.AnimeCreator;
import com.matsu.springrestart.util.AnimePostRequestBodyCreator;
import com.matsu.springrestart.util.AnimePutRequestBodyCreator;
import com.matsu.springrestart.util.DateUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.system.CapturedOutput;
import org.springframework.boot.test.system.OutputCaptureExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

@ExtendWith({SpringExtension.class, OutputCaptureExtension.class})
class AnimeControllerTest {


    @Mock
    private AnimeService animeService;

    @Mock
    private DateUtil dateUtil;

    @InjectMocks
    private AnimeController animeController;

    @BeforeEach
    void setUp() {
        when(dateUtil.formatLocalDatetimeToDatabaseStyle(any())).thenReturn("dd-MM-yyyy HH:mm:ss");
    }

    @Test
    @DisplayName("Successful to list all anime with pageable")
    void return_list_of_anime_on_page_successful(CapturedOutput capturedOutput) {
        PageImpl<Anime> animeImplPage = new PageImpl<>(List.of(AnimeCreator.createValidAnime()));
        when(animeService.listAll(ArgumentMatchers.any())).thenReturn(animeImplPage);

        String expectedName = AnimeCreator.createValidAnime().getName();
        Page<Anime> animePage = animeController.list(PageRequest.of(0, 20)).getBody();

        assertNotNull(animePage);
        assertFalse(animePage.toList().isEmpty());
        assertEquals(1, animePage.toList().size());
        assertEquals(expectedName, animePage.toList().get(0).getName());
        assertTrue(capturedOutput.getOut().contains("Request all animes in base at "));
    }

    @Test
    @DisplayName("Successful to list all anime")
    void return_list_of_anime_successful(CapturedOutput capturedOutput) {

        when(animeService.listAllNonPageable()).thenReturn(List.of(AnimeCreator.createValidAnime()));

        String expectedName = AnimeCreator.createValidAnime().getName();
        List<Anime> animeList = animeController.listAll().getBody();

        assertNotNull(animeList);
        assertFalse(animeList.isEmpty());
        assertEquals(1, animeList.size());
        assertEquals(expectedName, animeList.get(0).getName());
        assertTrue(capturedOutput.getOut().contains("Request all animes in base at "));
    }

    @Test
    @DisplayName("Successful get anime by id")
    void return_anime_by_id_successful(CapturedOutput capturedOutput) {

        when(animeService
                .findByIdOrThrowBadRequest(ArgumentMatchers.anyLong()))
                .thenReturn(AnimeCreator.createValidAnime());

        Long expectedId = AnimeCreator.createValidAnime().getId();
        Anime anime = animeController.findById(1).getBody();

        assertNotNull(anime);
        assertEquals(expectedId, anime.getId());
        assertTrue(capturedOutput.getOut().contains("Request a anime in base at "));
    }

    @Test
    @DisplayName("Successful get anime by name")
    void return_anime_by_name_successful(CapturedOutput capturedOutput) {

        when(animeService
                .findByName(ArgumentMatchers.anyString()))
                .thenReturn(List.of(AnimeCreator.createValidAnime()));

        String expectedName = AnimeCreator.createValidAnime().getName();
        List<Anime> animeList = animeController.findByName("anime").getBody();

        assertNotNull(animeList);
        assertFalse(animeList.isEmpty());
        assertEquals(1, animeList.size());
        assertEquals(expectedName, animeList.get(0).getName());
        assertTrue(capturedOutput.getOut().contains("Request a specific anime in base at "));
    }

    @Test
    @DisplayName("Failed get anime by name")
    void return_anime_by_name_failed(CapturedOutput capturedOutput) {

        when(animeService
                .findByName(ArgumentMatchers.anyString()))
                .thenReturn(Collections.emptyList());

        List<Anime> animeList = animeController.findByName("anime").getBody();

        assertNotNull(animeList);
        assertTrue(animeList.isEmpty());
        assertTrue(capturedOutput.getOut().contains("Request a specific anime in base at "));
    }

    @Test
    @DisplayName("Successful save anime")
    void save_anime_successful(CapturedOutput capturedOutput) {
        when(animeService
                .save(ArgumentMatchers.any(AnimePostRequestBody.class)))
                .thenReturn(AnimeCreator.createValidAnime());

        Anime anime = animeController.save(AnimePostRequestBodyCreator.createAnimeToBeSaved()).getBody();

        assertNotNull(anime);
        assertEquals(anime, AnimeCreator.createValidAnime());
        assertTrue(capturedOutput.getOut().contains("Add a new anime in base at "));
    }

    @Test
    @DisplayName("Successful updated anime")
    void updated_anime_successful(CapturedOutput capturedOutput) {
        doNothing().when(animeService)
                .replace(ArgumentMatchers.any(AnimePutRequestBody.class));

        ResponseEntity<Void> anime = animeController.replace(AnimePutRequestBodyCreator.createAnimeToBeUpdated());


        assertDoesNotThrow(() -> animeController.replace(AnimePutRequestBodyCreator.createAnimeToBeUpdated()));
        assertNotNull(anime);
        assertEquals(anime.getStatusCode(), HttpStatus.NO_CONTENT);
        assertTrue(capturedOutput.getOut().contains("Rename a anime in base at "));
    }

    @Test
    @DisplayName("Successful deleted anime")
    void deleted_anime_successful(CapturedOutput capturedOutput) {
        doNothing().when(animeService)
                .delete(ArgumentMatchers.anyLong());

        ResponseEntity<Void> anime = animeController.delete(1);


        assertDoesNotThrow(() -> animeController.delete(1));
        assertNotNull(anime);
        assertEquals(anime.getStatusCode(), HttpStatus.NO_CONTENT);
        assertTrue(capturedOutput.getOut().contains("Remove a anime in base at "));
    }
}