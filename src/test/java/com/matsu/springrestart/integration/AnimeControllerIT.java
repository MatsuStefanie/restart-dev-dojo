package com.matsu.springrestart.integration;

import com.matsu.springrestart.domain.Anime;
import com.matsu.springrestart.repository.AnimeRepository;
import com.matsu.springrestart.requests.AnimePostRequestBody;
import com.matsu.springrestart.util.AnimeCreator;
import com.matsu.springrestart.util.AnimePostRequestBodyCreator;
import com.matsu.springrestart.wrapper.PageableResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestDatabase
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class AnimeControllerIT {

    @Autowired
    private TestRestTemplate template;
    @Autowired
    private AnimeRepository repository;

    @LocalServerPort
    private int port;

    @Test
    @DisplayName("Successful to list all anime with pageable")
    void return_list_of_anime_on_page_successful() {
        Anime anime = repository.save(AnimeCreator.createAnimeToBeSaved());
        String expectedName = anime.getName();

        PageableResponse<Anime> animePage = template.exchange(
                        "/animes",
                        HttpMethod.GET,
                        null,
                        new ParameterizedTypeReference<PageableResponse<Anime>>() {
                        })
                .getBody();

        assertNotNull(animePage);
        assertFalse(animePage.toList().isEmpty());
        assertEquals(1, animePage.toList().size());
        assertEquals(expectedName, animePage.toList().get(0).getName());
    }

    @Test
    @DisplayName("Successful to list all anime")
    void return_list_of_anime_successful() {
        Anime anime = repository.save(AnimeCreator.createAnimeToBeSaved());
        String expectedName = anime.getName();

        List<Anime> animeList = template.exchange(
                        "/animes/all",
                        HttpMethod.GET,
                        null,
                        new ParameterizedTypeReference<List<Anime>>() {
                        })
                .getBody();

        assertNotNull(animeList);
        assertFalse(animeList.isEmpty());
        assertEquals(1, animeList.size());
        assertEquals(expectedName, animeList.get(0).getName());
    }

    @Test
    @DisplayName("Successful get anime by id")
    void return_anime_by_id_successful() {
        Anime savedAnime = repository.save(AnimeCreator.createAnimeToBeSaved());

        Long expectedId = savedAnime.getId();
        Anime anime = template.getForObject("/animes/{id}", Anime.class, expectedId);

        assertNotNull(anime);
        assertEquals(expectedId, anime.getId());
    }

    @Test
    @DisplayName("Successful get anime by name")
    void return_anime_by_name_successful() {
        Anime savedAnime = repository.save(AnimeCreator.createAnimeToBeSaved());


        String expectedName = savedAnime.getName();
        String url = String.format("/animes/find?name=%s", expectedName);

        List<Anime> animeList = template.exchange(
                        url,
                        HttpMethod.GET,
                        null,
                        new ParameterizedTypeReference<List<Anime>>() {
                        })
                .getBody();

        assertNotNull(animeList);
        assertFalse(animeList.isEmpty());
        assertEquals(1, animeList.size());
        assertEquals(expectedName, animeList.get(0).getName());
    }

    @Test
    @DisplayName("Failed get anime by name")
    void return_anime_by_name_failed() {

        List<Anime> animeList = template.exchange(
                        "/animes/find?name=DBZ",
                        HttpMethod.GET,
                        null,
                        new ParameterizedTypeReference<List<Anime>>() {
                        })
                .getBody();

        assertNotNull(animeList);
        assertTrue(animeList.isEmpty());
    }

    @Test
    @DisplayName("Successful save anime")
    void save_anime_successful() {

        AnimePostRequestBody animePostRequestBody = AnimePostRequestBodyCreator.createAnimeToBeSaved();

        ResponseEntity<Anime> animeResponseEntity = template.postForEntity("/animes", animePostRequestBody, Anime.class);

        assertNotNull(animeResponseEntity);
        assertEquals(HttpStatus.CREATED, animeResponseEntity.getStatusCode());
        assertNotNull(animeResponseEntity.getBody());
        assertNotNull(animeResponseEntity.getBody().getId());
    }

    @Test
    @DisplayName("Successful updated anime")
    void updated_anime_successful() {
        Anime savedAnime = repository.save(AnimeCreator.createAnimeToBeSaved());

        savedAnime.setName("Naruto");

        ResponseEntity<Void> animeResponseEntity = template.exchange(
                "/animes",
                HttpMethod.PUT,
                new HttpEntity<>(savedAnime),
                Void.class);

        assertNotNull(animeResponseEntity);
        assertEquals(HttpStatus.NO_CONTENT, animeResponseEntity.getStatusCode());

    }

    @Test
    @DisplayName("Successful deleted anime")
    void deleted_anime_successful() {
        Anime savedAnime = repository.save(AnimeCreator.createAnimeToBeSaved());


        ResponseEntity<Void> animeResponseEntity = template.exchange("/animes/{id}",
                HttpMethod.DELETE,
                null,
                Void.class,
                savedAnime.getId());

        assertNotNull(animeResponseEntity);
        assertEquals(HttpStatus.NO_CONTENT, animeResponseEntity.getStatusCode());
    }

}
