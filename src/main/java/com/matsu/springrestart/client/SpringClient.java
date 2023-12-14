package com.matsu.springrestart.client;

import com.matsu.springrestart.domain.Anime;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;

@Slf4j
public class SpringClient {
    public static void main(String[] args) {
        ResponseEntity<Anime> entity = new RestTemplate()
                .getForEntity("http://localhost:8080/animes/{id}", Anime.class, 16);
        log.info(entity.toString());

        Anime anime = new RestTemplate()
                .getForObject("http://localhost:8080/animes/{id}", Anime.class, 16);
        log.info(String.valueOf(anime));

        Anime[] animes = new RestTemplate()
                .getForObject("http://localhost:8080/animes/all", Anime[].class);
        log.info(Arrays.toString(animes));

        ResponseEntity<List<Anime>> animeList = new RestTemplate()
                .exchange("http://localhost:8080/animes/all",
                        HttpMethod.GET,
                        null,
                        new ParameterizedTypeReference<>() {
                        });

        log.info(animeList.getBody().toString());


        // Anime supernatural = Anime.builder().name("supernatural").build();

        //  Anime animeSaved = new RestTemplate().postForObject("http://localhost:8080/animes", supernatural, Anime.class);
        // log.info("Saved anime {}", animeSaved);

        Anime myName = Anime.builder().name("My name 2").build();
        ResponseEntity<Anime> myNameSaved = new RestTemplate().exchange("http://localhost:8080/animes",
                HttpMethod.POST,
                new HttpEntity<>(myName, createJsonHeader()),
                Anime.class);
        log.info("Saved anime {}", myNameSaved);

        Anime animeToBeUpdated = myNameSaved.getBody();
        animeToBeUpdated.setName("Say my name");

        ResponseEntity<Void> sayMyNameUpdated = new RestTemplate().exchange("http://localhost:8080/animes",
                HttpMethod.PUT,
                new HttpEntity<>(animeToBeUpdated, createJsonHeader()),
                Void.class);

        log.info(sayMyNameUpdated.toString());

        ResponseEntity<Void> sayMyNameDeleted = new RestTemplate().exchange("http://localhost:8080/animes/{id}",
                HttpMethod.DELETE,
               null,
                Void.class,
                animeToBeUpdated.getId());

        log.info(sayMyNameDeleted.toString());
    }

    //We can add header in request
    private static HttpHeaders createJsonHeader() {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        return httpHeaders;
    }
}
