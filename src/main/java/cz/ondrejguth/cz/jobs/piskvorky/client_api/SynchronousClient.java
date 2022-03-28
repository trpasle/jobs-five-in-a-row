package cz.ondrejguth.cz.jobs.piskvorky.client_api;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClientRequestException;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.netty.http.client.PrematureCloseException;

import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
@Slf4j
public class SynchronousClient {
    public static final int MIN_COORDINATE = 0;
    public static final int MAX_COORDINATE = 20;
    private final UserTokenModel userTokenModel;
    private LocalDateTime lastRequestTimestamp;
    private int secondsBetweenRequests = 1;

    private final WebClientConnectionManager clientConnection;

    public SynchronousClient(@Value("${cz.ondrejguth.cz.jobs.piskvorky.userToken}") String userToken, WebClientConnectionManager clientConnection) {
        userTokenModel = new UserTokenModel(userToken);
        this.clientConnection = clientConnection;
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
                return clientConnection.getClient().post().uri("/connect").bodyValue(userTokenModel).retrieve().bodyToMono(GameConnectionModel.class).block();
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
                return clientConnection.getClient().post().uri("/play").bodyValue(new TurnModel(userTokenModel.userToken(), gameConnectionModel.gameToken(), x, y)).retrieve().bodyToMono(TurnResponseModel.class).block();
            } catch (final WebClientResponseException.TooManyRequests e) {
                log.debug("Too many requests, waiting before attempting to take turn again");
                secondsBetweenRequests++;
            } catch (final WebClientResponseException.NotAcceptable e) {
                log.debug("It is other's player turn, waiting and trying again");
            } catch (final WebClientResponseException.BadRequest e) {
                throw new InvalidTurnException(e);
            } catch (final WebClientRequestException e) {
                if (e.getCause() instanceof PrematureCloseException)
                    clientConnection.reconnect();
            }
    }
}