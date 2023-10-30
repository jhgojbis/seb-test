package com.seb.controller;


import com.seb.model.PlaceHolderRequest;
import com.seb.service.PlaceHolderService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/v1/placeholder")
@AllArgsConstructor
@Slf4j
public class PlaceController {

    private final PlaceHolderService service;

    @PostMapping(path = "/create", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<String>> create(@Valid @RequestBody PlaceHolderRequest data) {
        log.info("request {}", data);
        return service.createPlaceholderSource(data)
                .map(ResponseEntity::ok);
    }

    @GetMapping(path = "/{userId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<String>> getFilteringResource(@PathVariable String userId) {
        log.info("userId {}", userId);
        return service.getPlaceHolderByUserId(userId)
                .map(ResponseEntity::ok);
    }

    @GetMapping(path = "/comments/{userId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<String>> getComments(@PathVariable String userId) {
        log.info("userId {}", userId);
        return service.getPlaceHolderByUserId(userId)
                .flatMap(service::fetchCommentsByPlaceHolderResponse)
                .map(ResponseEntity::ok);
    }
}
