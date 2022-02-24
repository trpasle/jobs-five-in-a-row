package cz.ondrejguth.cz.jobs.piskvorky.client_api;

import io.netty.handler.logging.LogLevel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientRequestException;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.netty.http.client.HttpClient;
import reactor.netty.http.client.PrematureCloseException;
import reactor.netty.transport.logging.AdvancedByteBufFormat;

import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
@Slf4j
public class SynchronousClient {
    public static final int MIN_COORDINATE = ApiClientV1Util.MIN_COORDINATE;
    public static final int MAX_COORDINATE = ApiClientV1Util.MAX_COORDINATE;
    private final ApiClientV1Util client = new ApiClientV1Util();
    private final UserTokenModel userTokenModel;
    private LocalDateTime lastRequestTimestamp;
    private WebClient httpClient;
    private int secondsBetweenRequests = 1;

    public SynchronousClient(@Value("${cz.ondrejguth.cz.jobs.piskvorky.userToken}") String userToken) {
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

    public int getMinCoordinate() {
        return MIN_COORDINATE;
    }

    public int getMaxCoordinate() {
        return MAX_COORDINATE;
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

    public GameConnectionModel newGame() {
        while (true)
            try {
                return client.newGame(httpClient, userTokenModel).block();
            } catch (final WebClientResponseException.TooManyRequests e) {
                log.debug("Too many requests, waiting before attempting to start new game again");
                waitUntilNextRequestAllowed();
                secondsBetweenRequests++;
            }
    }

    public TurnResponseModel play(final GameConnectionModel gameConnectionModel, final int x, final int y) {
        while (true)
            try {
                waitUntilNextRequestAllowed();
                return client.play(gameConnectionModel.gameToken(), x, y, httpClient, userTokenModel).block();
            } catch (final WebClientResponseException.TooManyRequests e) {
                log.debug("Too many requests, waiting before attempting to take turn again");
                secondsBetweenRequests++;
            } catch (final WebClientResponseException.NotAcceptable e) {
                log.debug("It is other's player turn, waiting and trying again");
            } catch (final WebClientResponseException.BadRequest e) {
                throw new InvalidTurnException(e);
            } catch (final WebClientRequestException e) {
                if (e.getCause() instanceof PrematureCloseException)
                    httpClient = newWebClient();
            }
    }
}