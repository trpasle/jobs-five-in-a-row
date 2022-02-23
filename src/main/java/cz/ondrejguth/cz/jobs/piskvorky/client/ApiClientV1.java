package cz.ondrejguth.cz.jobs.piskvorky.client;

import io.netty.handler.logging.LogLevel;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;
import reactor.netty.transport.logging.AdvancedByteBufFormat;

import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.function.Predicate;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
@Slf4j
public class ApiClientV1 {
    private final UserTokenModel userTokenModel;
    private LocalDateTime lastRequestTimestamp;
    private WebClient httpClient;
    private int secondsBetweenRequests = 1;

    private final Predicate<? super Throwable> tooManyRequestsPredicate = e -> e instanceof WebClientResponseException.TooManyRequests;
    private final Consumer<? super Throwable> handleTooManyRequests = e -> secondsBetweenRequests++;

    public ApiClientV1(@Value("${cz.ondrejguth.cz.jobs.piskvorky.userToken}") String userToken) {
        userTokenModel = new UserTokenModel(userToken);
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
    }

    private void waitUntilNextRequestAllowed() {
        var now = LocalDateTime.now();
        if (lastRequestTimestamp != null && now.minusSeconds(secondsBetweenRequests).isBefore(lastRequestTimestamp)) {
            try {
                log.debug("Waiting between requests for " + secondsBetweenRequests + " seconds.");
                TimeUnit.SECONDS.sleep(secondsBetweenRequests);
            } catch (InterruptedException e) {
                //intentionally ignore
            }
        }
        lastRequestTimestamp = now;
    }

    /**
     * Start new game. Either join some existing or create a new one.
     *
     * @return gameId and gameToken of the new game
     * @throws org.springframework.web.reactive.function.client.WebClientException if something goes wrong
     */
    public Mono<GameConnectionModel> newGame() {
        waitUntilNextRequestAllowed();
            return httpClient
                    .post()
                    .uri("/connect")
                    .bodyValue(userTokenModel)
                    .retrieve()
                    .bodyToMono(GameConnectionModel.class)
                    .doOnError(tooManyRequestsPredicate, handleTooManyRequests);
    }

    /**
     * Retrieve the current game status.
     *
     * @param gameToken
     * @return game status: played coordinates, users and their symbol assignment, current player, the winner
     * @throws org.springframework.web.reactive.function.client.WebClientException if something goes wrong
     */
    public Mono<GameStatusModel> retrieveGameStatus(final String gameToken) {
        waitUntilNextRequestAllowed();
        return httpClient
                .post()
                .uri("/checkStatus")
                .bodyValue(new GameUserTokenModel(userTokenModel.userToken(), gameToken))
                .retrieve()
                .bodyToMono(GameStatusModel.class)
                .doOnError(tooManyRequestsPredicate, handleTooManyRequests);
    }

    public Mono<TurnResponseModel> play(final String gameToken, final int x, final int y) {
        waitUntilNextRequestAllowed();
        return httpClient
                .post()
                .uri("/play")
                .bodyValue(new TurnModel(userTokenModel.userToken(), gameToken, x, y))
                .retrieve()
                .bodyToMono(TurnResponseModel.class)
                .doOnError(tooManyRequestsPredicate, handleTooManyRequests)
                .doOnError(WebClientResponseException.NotAcceptable.class, e -> {
                    try {TimeUnit.SECONDS.sleep(1);} catch (InterruptedException ex) {}
                });
    }
}