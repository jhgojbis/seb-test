package com.seb.controller;

import com.seb.application.SebApplication;
import com.seb.model.PlaceHolderRequest;
import com.seb.excption.CustomPlaceHolderException;
import com.seb.service.PlaceHolderService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@WebFluxTest(PlaceController.class)
@ContextConfiguration(classes = {SebApplication.class})
class PlaceControllerTest {

    @Autowired
    private WebTestClient client;

    @MockBean
    private PlaceHolderService service;

    @Test
    void testCreatePlaceholder() {
        var postResponse = """
                {
                  "title": "foo",
                  "body": "bar",
                  "userId": 1,
                  "id": 101
                }""";


        // given
        PlaceHolderRequest data = new PlaceHolderRequest("foo", "bar", 1);
        // when
        when(service.createPlaceholderSource(any(PlaceHolderRequest.class)))
                .thenReturn(Mono.just(postResponse));
        // then
        client.post()
                .uri("/api/v1/placeholder/create")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(data)
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class)
                .isEqualTo(postResponse);
    }

    @Test
    void testCreatePlaceholderErrorHandling() {
        // given
        PlaceHolderRequest data = new PlaceHolderRequest("foo", "bar", 1);
        String errorMessage = "An error occurred";
        // when
        when(service.createPlaceholderSource(any(PlaceHolderRequest.class)))
                .thenReturn(Mono.error(new CustomPlaceHolderException(errorMessage, new Exception("Error"))));
        // then
        client.post()
                .uri("/api/v1/placeholder/create")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(data)
                .exchange()
                .expectStatus().is4xxClientError()
                .expectBody(String.class)
                .isEqualTo(errorMessage);
    }

    @Test
    void testFetchPlaceholderByUserId() {
        // given
        var userId = "1";
        var getResponse = """
                {
                    "id": 1,
                    "userId": 1,
                    "title": "FOO",
                    "email": "foo@seb.se",
                    "body": "body to all"
                }""";
        var uri = UriComponentsBuilder.fromPath("/api/v1/placeholder/{userId}")
                .buildAndExpand(userId)
                .toUriString();
        // when
        when(service.getPlaceHolderByUserId(userId)).thenReturn(Mono.just(getResponse));

        // then
        client.get()
                .uri(uri)
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class)
                .isEqualTo(getResponse);
    }

    @Test
    void testFetchCommentsByPlaceholderId() {
        var nestedResponse = """
                  {
                    "postId": 1,
                    "id": 1,
                    "name": "Foo name",
                    "email": "foo email",
                    "body": "fooo body"
                  },\
                """;
        when(service.getPlaceHolderByUserId(any(String.class))).thenReturn(Mono.just("SomeResponse"));
        when(service.fetchCommentsByPlaceHolderResponse(anyString())).thenReturn(Mono.just(nestedResponse));

        client.get()
                .uri("/api/v1/placeholder/comments/1")
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class)
                .isEqualTo(nestedResponse);
    }
}
