package com.seb.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.seb.excption.CustomPlaceHolderException;
import com.seb.model.PlaceHolderRequest;
import com.seb.model.PlaceHolderResponse;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.List;


@Service
@AllArgsConstructor
public class PlaceHolderService {

    private WebClient webClient;

    public ObjectMapper mapper() {
        return new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);
    }

    public Mono<String> postPlaceHolder(PlaceHolderRequest data) {
        return Mono.fromCallable(() -> mapper().writeValueAsString(data))
                .subscribeOn(Schedulers.boundedElastic())
                .flatMap(body -> {
                            String uriString = UriComponentsBuilder
                                    .fromPath("posts")
                                    .toUriString();
                            return webClient.post()
                                    .uri(uriString)
                                    .body(Mono.just(body), String.class)
                                    .retrieve()
                                    .bodyToMono(String.class);
                        }
                )
                .onErrorMap(originalException ->
                        new CustomPlaceHolderException("Error when calling post", originalException));
    }

    public Mono<String> getPlaceHolderByUserId(String userId) {
        String uriString = UriComponentsBuilder
                .fromPath("posts")
                .queryParam("userId", userId)
                .toUriString();
        var errorMessage = "Error when calling get placeHolder by userId";
        return webClientGetCall(uriString, errorMessage);
    }

    public Mono<String> fetchCommentsByPlaceHolderResponse(String response) {
        return Mono.fromCallable(()
                        -> mapper().readValue(response, new TypeReference<List<PlaceHolderResponse>>() {
                })).subscribeOn(Schedulers.boundedElastic())
                .flatMapMany(Flux::fromIterable)
                .flatMap(comment -> getCommentsByPostId(String.valueOf(comment.getId())))
                .collectList()
                .map(Object::toString)
                .onErrorMap(originalException ->
                        new CustomPlaceHolderException("Error when calling get", originalException));
    }
    private Mono<String> getCommentsByPostId(String postId) {
        String uriString = UriComponentsBuilder
                .fromPath("/posts/{postId}/comments")
                .buildAndExpand(postId)
                .toUriString();
        var errorMessage = "Error when calling get comment by postId";
        return webClientGetCall(uriString, errorMessage);
    }

    private Mono<String> webClientGetCall(String uriString, String errorMessage) {
        return webClient.get()
                .uri(uriString)
                .retrieve()
                .bodyToMono(String.class)
                .onErrorMap(originalException ->
                        new CustomPlaceHolderException(errorMessage, originalException));
    }
}
