package cz.ondrejguth.cz.jobs.piskvorky.client_api;

import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;

class WebClientConnectionManagerTest {
    private WebClientConnectionManager webClientConnectionManager;
    private MockWebServer mockApi;

    @BeforeEach
    void setUp() throws IOException {
        mockApi = new MockWebServer();
        mockApi.start();
        webClientConnectionManager = new WebClientConnectionManager(mockApi.url("/").toString());
    }

    @AfterEach
    void tearDown() throws IOException {
        mockApi.shutdown();
    }

    @Test
    void getInstance() {
        Assertions.assertNotNull(webClientConnectionManager.getClient());
    }

    @Test
    void reconnect() {
        var clientInstance1 = webClientConnectionManager.getClient();
        webClientConnectionManager.reconnect();
        var clientInstance2 = webClientConnectionManager.getClient();
        Assertions.assertNotSame(clientInstance1, clientInstance2);
    }
}