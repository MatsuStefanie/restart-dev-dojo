package com.matsu.springrestart.controller;


import com.matsu.springrestart.domain.Anime;
import com.matsu.springrestart.requests.AnimePostRequestBody;
import com.matsu.springrestart.requests.AnimePutRequestBody;
import com.matsu.springrestart.service.AnimeService;
import com.matsu.springrestart.util.DateUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("animes")
public class AnimeController {

    private final DateUtil dateUtil;
    private final AnimeService animeService;

    @GetMapping
    @Operation(summary = "List all anime paginated",
            description = "The default size is 20, use the parameter siza to change the default value",
            tags = {"Anime"})
    public ResponseEntity<Page<Anime>> list(@ParameterObject Pageable pageable) {
        log.info("Request all animes in base at {}",
                dateUtil.formatLocalDatetimeToDatabaseStyle(LocalDateTime.now()));
        return ResponseEntity.ok(animeService.listAll(pageable));
    }

    @GetMapping(path = "/all")
    public ResponseEntity<List<Anime>> listAll() {
        log.info("Request all animes in base at {}",
                dateUtil.formatLocalDatetimeToDatabaseStyle(LocalDateTime.now()));
        return ResponseEntity.ok(animeService.listAllNonPageable());
    }

    @GetMapping(path = "/{id}")
    public ResponseEntity<Anime> findById(@PathVariable long id) {
        log.info("Request a anime in base at {}",
                dateUtil.formatLocalDatetimeToDatabaseStyle(LocalDateTime.now()));
        return ResponseEntity.ok(animeService.findByIdOrThrowBadRequest(id));
    }

    @GetMapping(path = "/auth/{id}")
    public ResponseEntity<Anime> findByIdAuthenticationPrincipal(@PathVariable long id,
                                                                 @AuthenticationPrincipal UserDetails userDetails) {
        log.info("The {} request a anime in base at {}", userDetails.getUsername(),
                dateUtil.formatLocalDatetimeToDatabaseStyle(LocalDateTime.now()));
        return ResponseEntity.ok(animeService.findByIdOrThrowBadRequest(id));
    }

    @GetMapping(path = "/find")
    public ResponseEntity<List<Anime>> findByName(@RequestParam String name) {
        log.info("Request a specific anime in base at {}",
                dateUtil.formatLocalDatetimeToDatabaseStyle(LocalDateTime.now()));
        return ResponseEntity.ok(animeService.findByName(name));
    }

    @PostMapping(path = "/auth")
    public ResponseEntity<Anime> save(@RequestBody @Valid AnimePostRequestBody anime) {
        log.info("Add a new anime in base at {}",
                dateUtil.formatLocalDatetimeToDatabaseStyle(LocalDateTime.now()));
        return new ResponseEntity<>(animeService.save(anime), HttpStatus.CREATED);
    }

    @DeleteMapping(path = "/auth/{id}")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Successful Operation"),
            @ApiResponse(responseCode = "400", description = "When anime does not exist in the database")
    })
    public ResponseEntity<Void> delete(@PathVariable long id) {
        log.info("Remove a anime in base at {}",
                dateUtil.formatLocalDatetimeToDatabaseStyle(LocalDateTime.now()));
        animeService.delete(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PutMapping(path = "/auth")
    public ResponseEntity<Void> replace(@RequestBody AnimePutRequestBody anime) {
        log.info("Rename a anime in base at {}",
                dateUtil.formatLocalDatetimeToDatabaseStyle(LocalDateTime.now()));
        animeService.replace(anime);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
