package com.matsu.springrestart.util;

import com.matsu.springrestart.domain.Anime;

public class AnimeCreator {

    public static Anime createAnimeToBeSaved(){
        return Anime.builder()
                .name("Promise never land")
                .build();
    }
    public static Anime createValidAnime(){
        return Anime.builder()
                .name("Promise never land")
                .id(1L)
                .build();
    }
    public static Anime createValidUpdatedAnime(){
        return Anime.builder()
                .name("Hellsing")
                .id(1L)
                .build();
    }
}
