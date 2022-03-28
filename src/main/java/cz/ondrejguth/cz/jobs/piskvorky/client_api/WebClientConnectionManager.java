package cz.ondrejguth.cz.jobs.piskvorky.client_api;

import io.netty.handler.logging.LogLevel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;
import reactor.netty.transport.logging.AdvancedByteBufFormat;

@Component
@Slf4j
public class WebClientConnectionManager {
    private WebClient httpClient = newWebClient();

    public WebClient getClient() {
        return httpClient;
    }

    public void reconnect() {
        httpClient = newWebClient();
    }

    private static WebClient newWebClient() {
        log.debug("Creating new WebClient.");
        return WebClient.builder()
                .clientConnector(new ReactorClientHttpConnector(HttpClient.create().wiretap("reactor.netty.http.client.HttpClient",
                        LogLevel.DEBUG, AdvancedByteBufFormat.TEXTUAL)))
                .baseUrl("https://piskvorky.jobs.cz/api/v1")
                .defaultHeader("Accept", MediaType.APPLICATION_JSON_VALUE)
                .defaultHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                .build();
    }}
