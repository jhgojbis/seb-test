package com.seb.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.seb.application.SebApplication;
import com.seb.model.PlaceHolderRequest;
import com.seb.model.PlaceHolderResponse;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.List;

import static org.junit.Assert.assertEquals;

@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = {SebApplication.class})
@TestPropertySource(locations = "classpath:application.yaml")
class PlaceHolderServiceTest {

    @Autowired
    private PlaceHolderService service;

    @Autowired
    private WebClient.Builder webClientBuilder;
    private MockWebServer mockWebServer;

    @BeforeEach
    public void setUp() throws Exception {
        mockWebServer = new MockWebServer();
        mockWebServer.start();
        WebClient mockWebClient = webClientBuilder.baseUrl(mockWebServer.url("/").toString()).build();
        ReflectionTestUtils.setField(service, "webClient", mockWebClient);
    }


    @AfterEach
    public void tearDown() throws IOException {
        mockWebServer.shutdown();
    }

    @Test
    void testPostPlaceHolder() throws IOException {
        String mockedResponse = loadJsonFromResources("mockedData/postDataResponse.json");
        mockWebServer.enqueue(new MockResponse().setBody(mockedResponse).setResponseCode(200));

        var response = service.postPlaceHolder(new PlaceHolderRequest("foo", "bar", 1));
        assertEquals(mockedResponse, response.block());
    }

    @Test
    void fetchCommentsByPlaceHolderResponse() throws IOException {
        var commentRequest = """
                [
                    {
                        "userId": 1,
                        "id": 1,
                        "title": "sunt aut facere repellat provident occaecati excepturi optio reprehenderit",
                        "body": "quia et suscipit\\nsuscipit recusandae consequuntur expedita et cum\\nreprehenderit molestiae ut ut quas totam\\nnostrum rerum est autem sunt rem eveniet architecto"
                    }
                ]
                                """;

        String mockedResponse = loadJsonFromResources("mockedData/commentsDataResponse.json");
        mockWebServer.enqueue(new MockResponse().setBody(mockedResponse).setResponseCode(200));
        var response = service.fetchCommentsByPlaceHolderResponse(commentRequest);
        var serviceResponse = new ObjectMapper().readValue(response.block(Duration.ofSeconds(1)), new TypeReference<List<PlaceHolderResponse>>() {
        });
        assertEquals("1", serviceResponse.get(0).getPostId());
        assertEquals("1", serviceResponse.get(0).getId());
    }

    @Test
    void testGetPlaceholder() throws IOException {
        String mockedResponse = loadJsonFromResources("mockedData/fetchDataResponse.json");
        mockWebServer.enqueue(new MockResponse().setBody(mockedResponse).setResponseCode(200));

        var response = service.getPlaceHolderByUserId("1");
        assertEquals(mockedResponse, response.block());
    }

    private String loadJsonFromResources(String path) throws IOException {
        return new String(Files.readAllBytes(Paths.get("src/test/resources", path)));
    }
}