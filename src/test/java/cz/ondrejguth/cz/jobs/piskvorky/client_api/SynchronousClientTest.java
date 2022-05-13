package cz.ondrejguth.cz.jobs.piskvorky.client_api;

import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okio.ByteString;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.io.IOException;

@ExtendWith(MockitoExtension.class)
class SynchronousClientTest {
    @InjectMocks
    private SynchronousClient instance;

    @Mock
    private WebClientConnectionManager connectionManager;

    private MockWebServer mockApi;

    private final GameConnectionModel testConnectionModel = new GameConnectionModel(201, "c9d78e8e-30fc-42f7-91b8-0aa40f3f9ba0", "907e768f-5896-4d5d-81c7-174fbb7a3e40");

    @BeforeEach
    private void setUp() throws IOException {
        mockApi = new MockWebServer();
        mockApi.start();
        Mockito.when(connectionManager.getClient()).thenReturn(WebClient.create(mockApi.url("/").toString()));
    }

    @AfterEach
    private void tearDown() throws IOException {
        mockApi.shutdown();
    }


    @Test
    void newGame() throws InterruptedException, IOException {
        mockApi.enqueue(new MockResponse().setResponseCode(201).addHeader("Content-Type", "application/json").setBody("{\n" +
                "  \"statusCode\": 201,\n" +
                "  \"gameToken\": \"c9d78e8e-30fc-42f7-91b8-0aa40f3f9ba0\",\n" +
                "  \"gameId\": \"907e768f-5896-4d5d-81c7-174fbb7a3e40\",\n" +
                "  \"headers\": {}\n" +
                "}"));
        Assertions.assertEquals(
                testConnectionModel,
                instance.newGame()
        );

        var recReq = mockApi.takeRequest();
        Assertions.assertEquals(recReq.getMethod(), "POST");
        Assertions.assertEquals(recReq.getPath(), ApiV1Constants.NEW_GAME_URI);
        Assertions.assertTrue(recReq.getBody().indexOf(ByteString.encodeUtf8("userToken")) > -1);
    }

    @Test
    void newGameInvalidUserToken() {
        mockApi.enqueue(
                new MockResponse().setResponseCode(401).addHeader("content-type", "application/json ").setBody("{\n" +
                        "  \"statusCode\": 401,\n" +
                        "  \"errors\": {\n" +
                        "    \"userToken\": \"Invalid user token.\"\n" +
                        "  },\n" +
                        "  \"headers\": {}\n" +
                        "}")
        );
        Assertions.assertThrows(WebClientResponseException.Unauthorized.class, () -> instance.newGame());
    }

    @Test
    void newGameTooLongWaiting() {
        //TODO
    }

    @Test
    void play() throws InterruptedException, IOException {
        mockApi.enqueue(new MockResponse().setResponseCode(201).addHeader("Content-Type", "application/json").setBody("{\n" +
                "  \"statusCode\": 201,\n" +
                "  \"playerCrossId\": \"b48ce652-3bb9-4180-99b9-459a282f58ac\",\n" +
                "  \"playerCircleId\": \"af05f814-a669-4287-8ffb-a317d831a4f6\",\n" +
                "  \"actualPlayerId\": \"af05f814-a669-4287-8ffb-a317d831a4f6\",\n" +
                "  \"winnerId\": null,\n" +
                "  \"coordinates\": [\n" +
                "    {\n" +
                "      \"playerId\": \"b48ce652-3bb9-4180-99b9-459a282f58ac\",\n" +
                "      \"x\": 0,\n" +
                "      \"y\": 0\n" +
                "    }\n" +
                "  ],\n" +
                "  \"headers\": {}\n" +
                "}"));

        var expTurnResponseModel = new TurnResponseModel(
                    201,
                    "b48ce652-3bb9-4180-99b9-459a282f58ac",
                    "af05f814-a669-4287-8ffb-a317d831a4f6",
                    "af05f814-a669-4287-8ffb-a317d831a4f6",
                    null,
                    new CoordinateModel[] {new CoordinateModel("b48ce652-3bb9-4180-99b9-459a282f58ac", 0, 0)}
            );
        var methTurnResponseModel = instance.play(testConnectionModel, 0, 0);
        Assertions.assertEquals(expTurnResponseModel.statusCode(), methTurnResponseModel.statusCode());
        Assertions.assertEquals(expTurnResponseModel.actualPlayerId(), methTurnResponseModel.actualPlayerId());
        Assertions.assertEquals(expTurnResponseModel.playerCircleId(), methTurnResponseModel.playerCircleId());
        Assertions.assertEquals(expTurnResponseModel.playerCrossId(), methTurnResponseModel.playerCrossId());
        Assertions.assertEquals(expTurnResponseModel.winnerId(), methTurnResponseModel.winnerId());
        Assertions.assertArrayEquals(expTurnResponseModel.coordinates(), methTurnResponseModel.coordinates());

        var recReq = mockApi.takeRequest();
        Assertions.assertEquals(recReq.getMethod(), "POST");
        Assertions.assertEquals(recReq.getPath(), ApiV1Constants.PLAY_URI);
        Assertions.assertTrue(recReq.getBody().indexOf(ByteString.encodeUtf8("userToken")) > -1);
        Assertions.assertTrue(recReq.getBody().indexOf(ByteString.encodeUtf8("gameToken")) > -1);
        Assertions.assertTrue(recReq.getBody().indexOf(ByteString.encodeUtf8("positionX")) > -1);
        Assertions.assertTrue(recReq.getBody().indexOf(ByteString.encodeUtf8("positionY")) > -1);
    }

    @Test
    void playUsedCoordinates() throws IOException {
        mockApi.enqueue(new MockResponse().setResponseCode(409).addHeader("Content-Type", "application/json").setBody("{\n" +
                "  \"statusCode\": 409,\n" +
                "  \"errors\": {\n" +
                "    \"ilegalMove\": \"This coordinates are used!\"\n" +
                "  },\n" +
                "  \"headers\": {}\n" +
                "}"));

        Assertions.assertThrows(CoordinatesUsedException.class, () -> instance.play(testConnectionModel, 0, 0));
    }

    @Test
    void playInvalidGame() throws IOException {
        mockApi.enqueue(new MockResponse().setResponseCode(401).addHeader("Content-Type", "application/json").setBody("{\n" +
                "  \"statusCode\": 401,\n" +
                "  \"errors\": {\n" +
                "    \"gameToken\": \"Invalid game token.\"\n" +
                "  },\n" +
                "  \"headers\": {}\n" +
                "}"));

        Assertions.assertThrows(InvalidGameException.class, () -> instance.play(testConnectionModel, 0, 0));
    }

    @Test
    void playGameCompleted() throws IOException {
        mockApi.enqueue(new MockResponse().setResponseCode(226).addHeader("Content-Type", "application/json").setBody("{\n" +
                "  \"statusCode\": 226,\n" +
                "  \"playerCrossId\": \"b48ce652-3bb9-4180-99b9-459a282f58ac\",\n" +
                "  \"playerCircleId\": \"af05f814-a669-4287-8ffb-a317d831a4f6\",\n" +
                "  \"actualPlayerId\": \"af05f814-a669-4287-8ffb-a317d831a4f6\",\n" +
                "  \"winnerId\": \"af05f814-a669-4287-8ffb-a317d831a4f6\",\n" +
                "  \"coordinates\": [\n" +
                "    {\n" +
                "      \"playerId\": \"b48ce652-3bb9-4180-99b9-459a282f58ac\",\n" +
                "      \"x\": 0,\n" +
                "      \"y\": 0\n" +
                "    }\n" +
                "  ],\n" +
                "  \"headers\": {}\n" +
                "}"));
        var expTurnResponseModel = new TurnResponseModel(
                226,
                "b48ce652-3bb9-4180-99b9-459a282f58ac",
                "af05f814-a669-4287-8ffb-a317d831a4f6",
                "af05f814-a669-4287-8ffb-a317d831a4f6",
                "af05f814-a669-4287-8ffb-a317d831a4f6",
                new CoordinateModel[] {new CoordinateModel("b48ce652-3bb9-4180-99b9-459a282f58ac", 0, 0)}
        );
        var methTurnResponseModel = instance.play(testConnectionModel, 0, 0);
        Assertions.assertEquals(expTurnResponseModel.statusCode(), methTurnResponseModel.statusCode());
        Assertions.assertEquals(expTurnResponseModel.actualPlayerId(), methTurnResponseModel.actualPlayerId());
        Assertions.assertEquals(expTurnResponseModel.playerCircleId(), methTurnResponseModel.playerCircleId());
        Assertions.assertEquals(expTurnResponseModel.playerCrossId(), methTurnResponseModel.playerCrossId());
        Assertions.assertEquals(expTurnResponseModel.winnerId(), methTurnResponseModel.winnerId());
        Assertions.assertArrayEquals(expTurnResponseModel.coordinates(), methTurnResponseModel.coordinates());
    }
}