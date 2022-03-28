package cz.ondrejguth.cz.jobs.piskvorky.client_api;

import io.netty.handler.logging.LogLevel;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;
import reactor.netty.transport.logging.AdvancedByteBufFormat;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class SynchronousClientTest {
    @InjectMocks
    private SynchronousClient instance;

    @Mock
    private WebClientConnectionManager connectionManager;


    @BeforeEach
    void setUp() {
//        final WebClient webClient = WebClient.builder()
//                .clientConnector(new ReactorClientHttpConnector(HttpClient.create().wiretap("reactor.netty.http.client.HttpClient",
//                        LogLevel.DEBUG, AdvancedByteBufFormat.TEXTUAL)))
//                .baseUrl("https://piskvorky.jobs.cz/api/v1")
//                .defaultHeader("Accept", MediaType.APPLICATION_JSON_VALUE)
//                .defaultHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
//                .build();
//        Mockito.when(connectionManager.getClient()).thenReturn(webClient);
    }

    @Test
    void getMinCoordinate() {
        Assertions.assertEquals(SynchronousClient.MIN_COORDINATE, instance.getMinCoordinate());
    }

    @Test
    void getMaxCoordinate() {
        Assertions.assertEquals(SynchronousClient.MAX_COORDINATE, instance.getMaxCoordinate());
    }

    @Test
    void newGame() {
    }

    @Test
    void play() {
    }
}