package cz.ondrejguth.cz.jobs.piskvorky.client_api;

import io.netty.handler.logging.LogLevel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
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
    private final String apiUrl;

    public WebClientConnectionManager(@Value("${cz.ondrejguth.cz.jobs.piskvorky.api_url}") String apiUrl) {
        this.apiUrl = apiUrl;
    }

    public WebClient getClient() {
        return httpClient;
    }

    public void reconnect() {
        log.debug("Reconnect, will create new WebClient");
        httpClient = newWebClient();
    }

    private WebClient newWebClient() {
        log.debug("Creating new WebClient.");
        return WebClient.builder()
                .clientConnector(new ReactorClientHttpConnector(HttpClient.create().wiretap("reactor.netty.http.client.HttpClient",
                        LogLevel.DEBUG, AdvancedByteBufFormat.TEXTUAL)))
                .baseUrl(apiUrl)
                .defaultHeader("Accept", MediaType.APPLICATION_JSON_VALUE)
                .defaultHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                .build();
    }}
