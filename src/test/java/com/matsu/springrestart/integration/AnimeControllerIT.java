package com.matsu.springrestart.integration;

import com.matsu.springrestart.domain.Anime;
import com.matsu.springrestart.domain.CustomerUser;
import com.matsu.springrestart.repository.AnimeRepository;
import com.matsu.springrestart.repository.CustomerUserRepository;
import com.matsu.springrestart.requests.AnimePostRequestBody;
import com.matsu.springrestart.util.AnimeCreator;
import com.matsu.springrestart.util.AnimePostRequestBodyCreator;
import com.matsu.springrestart.wrapper.PageableResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.system.CapturedOutput;
import org.springframework.boot.test.system.OutputCaptureExtension;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.domain.Page;
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
@ExtendWith(OutputCaptureExtension.class)
class AnimeControllerIT {

    @Autowired
    @Qualifier(value = "testRestTemplateRoleUser")
    private TestRestTemplate templateUser;
    @Autowired
    @Qualifier(value = "testRestTemplateRoleAdmin")
    private TestRestTemplate templateAdmin;
    @Autowired
    private AnimeRepository animeRepository;
    @Autowired
    private CustomerUserRepository userRepository;

    private static final CustomerUser USER = CustomerUser.builder()
            .userName("user")
            .password("{bcrypt}$2a$10$yO6/lt784rqyr/BpsBBqbOS0.iMy5dvpa7pdtVhefFsmrMOWrdMiC")
            .name("User")
            .authorities("ROLE_USER")
            .build();

    private static final CustomerUser ADMIN = CustomerUser.builder()
            .userName("admin")
            .password("{bcrypt}$2a$10$yO6/lt784rqyr/BpsBBqbOS0.iMy5dvpa7pdtVhefFsmrMOWrdMiC")
            .name("Administrator")
            .authorities("ROLE_ADMIN,ROLE_USER")
            .build();


    @TestConfiguration
    @Lazy
    static class Config {
        @Bean(name = "testRestTemplateRoleUser")
        public TestRestTemplate testRestTemplateRoleUserCreator(@Value("${local.server.port}") int port) {

            RestTemplateBuilder restTemplateBuilder = new RestTemplateBuilder()
                    .rootUri("http://localhost:" + port)
                    .basicAuthentication("user", "test");
            return new TestRestTemplate(restTemplateBuilder);
        }

        @Bean(name = "testRestTemplateRoleAdmin")
        public TestRestTemplate testRestTemplateRoleAdminCreator(@Value("${local.server.port}") int port) {

            RestTemplateBuilder restTemplateBuilder = new RestTemplateBuilder()
                    .rootUri("http://localhost:" + port)
                    .basicAuthentication("admin", "test");
            return new TestRestTemplate(restTemplateBuilder);
        }
    }

    @Test
    @DisplayName("Successful to list all anime with pageable")
    void return_list_of_anime_on_page_successful() {
        userRepository.save(USER);
        Anime anime = animeRepository.save(AnimeCreator.createAnimeToBeSaved());
        String expectedName = anime.getName();

        Page<Anime> animePage = templateUser.exchange(
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
        userRepository.save(USER);
        Anime anime = animeRepository.save(AnimeCreator.createAnimeToBeSaved());
        String expectedName = anime.getName();

        List<Anime> animeList = templateUser.exchange(
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
    @DisplayName("Successful get anime by id if auth like admin")
    void return_anime_by_id_case_auth_like_admin_successful(CapturedOutput capturedOutput) {
        userRepository.save(ADMIN);
        Anime savedAnime = animeRepository.save(AnimeCreator.createAnimeToBeSaved());

        Long expectedId = savedAnime.getId();
        Anime anime = templateAdmin.getForObject("/animes/auth/{id}", Anime.class, expectedId);

        assertNotNull(anime);
        assertEquals(expectedId, anime.getId());
        assertTrue(capturedOutput.getOut().contains(" request a anime in base at "));
    }

    @Test
    @DisplayName("Successful get anime by id")
    void return_anime_by_id_successful() {
        userRepository.save(USER);
        Anime savedAnime = animeRepository.save(AnimeCreator.createAnimeToBeSaved());

        Long expectedId = savedAnime.getId();
        Anime anime = templateUser.getForObject("/animes/{id}", Anime.class, expectedId);

        assertNotNull(anime);
        assertEquals(expectedId, anime.getId());
    }

    @Test
    @DisplayName("Successful get anime by id after auth")
    void return_anime_by_id_when_auth_successful() {
        Anime savedAnime = animeRepository.save(AnimeCreator.createAnimeToBeSaved());

        userRepository.save(USER);
        userRepository.save(ADMIN);
        Long expectedId = savedAnime.getId();
        Anime anime = templateAdmin.getForObject("/animes/auth/{id}", Anime.class, expectedId);

        assertNotNull(anime);
        assertEquals(expectedId, anime.getId());
    }

    @Test
    @DisplayName("Successful get anime by name")
    void return_anime_by_name_successful() {
        userRepository.save(USER);
        userRepository.save(ADMIN);
        Anime savedAnime = animeRepository.save(AnimeCreator.createAnimeToBeSaved());


        String expectedName = savedAnime.getName().trim();
        String url = String.format("/animes/find?name=%s", expectedName);

        List<Anime> animeList = templateAdmin.exchange(
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
        userRepository.save(USER);

        List<Anime> animeList = templateUser.exchange(
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
        userRepository.save(ADMIN);

        AnimePostRequestBody animePostRequestBody = AnimePostRequestBodyCreator.createAnimeToBeSaved();

        ResponseEntity<Anime> animeResponseEntity = templateAdmin.postForEntity(
                "/animes/auth",
                animePostRequestBody,
                Anime.class);

        assertNotNull(animeResponseEntity);
        assertEquals(HttpStatus.CREATED, animeResponseEntity.getStatusCode());
        assertNotNull(animeResponseEntity.getBody());
        assertNotNull(animeResponseEntity.getBody().getId());
    }

    @Test
    @DisplayName("Successful updated anime")
    void updated_anime_successful() {

        userRepository.save(ADMIN);
        Anime savedAnime = animeRepository.save(AnimeCreator.createAnimeToBeSaved());

        savedAnime.setName("Naruto");

        ResponseEntity<Void> animeResponseEntity = templateAdmin.exchange(
                "/animes/auth",
                HttpMethod.PUT,
                new HttpEntity<>(savedAnime),
                Void.class);

        assertNotNull(animeResponseEntity);
        assertEquals(HttpStatus.NO_CONTENT, animeResponseEntity.getStatusCode());

    }

    @Test
    @DisplayName("Successful deleted anime")
    void deleted_anime_successful() {

        userRepository.save(ADMIN);
        Anime savedAnime = animeRepository.save(AnimeCreator.createAnimeToBeSaved());


        ResponseEntity<Void> animeResponseEntity = templateAdmin.exchange("/animes/auth/{id}",
                HttpMethod.DELETE,
                null,
                Void.class,
                savedAnime.getId());

        assertNotNull(animeResponseEntity);
        assertEquals(HttpStatus.NO_CONTENT, animeResponseEntity.getStatusCode());
    }

}
