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
import java.util.Objects;
import java.util.concurrent.TimeUnit;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
@Slf4j
public class SynchronousClient {
    public static final int MAX_WAIT_BETWEEN_REQUESTS = 10;
    private final UserTokenModel userTokenModel;
    private final WebClientConnectionManager clientConnection;
    private LocalDateTime lastRequestTimestamp;
    private int secondsBetweenRequests = 1;

    public SynchronousClient(@Value("${cz.ondrejguth.cz.jobs.piskvorky.userToken}") String userToken, WebClientConnectionManager clientConnection) {
        userTokenModel = new UserTokenModel(userToken);
        this.clientConnection = clientConnection;
    }

    private void waitUntilNextRequestAllowed() {
        var now = LocalDateTime.now();
        if (secondsBetweenRequests > MAX_WAIT_BETWEEN_REQUESTS)
            throw new TooLongWaitingException();
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

    public GameConnectionModel newGame() throws TooLongWaitingException, WebClientResponseException.Unauthorized {
        while (true)
            try {
                return clientConnection.getClient().post().uri(ApiV1Constants.NEW_GAME_URI).bodyValue(userTokenModel).retrieve().bodyToMono(GameConnectionModel.class).block();
            } catch (final WebClientResponseException.TooManyRequests e) {
                log.debug("Too many requests, waiting before attempting to start new game again");
                waitUntilNextRequestAllowed();
                secondsBetweenRequests++;
            } catch (final WebClientResponseException.Unauthorized e) {
                log.error("Invalid user token. Invalid/expired registration?");
                throw e;
            }
    }

    public TurnResponseModel play(final GameConnectionModel gameConnectionModel, final int x, final int y)
            throws CoordinatesUsedException, InvalidGameException, TooLongWaitingException {
        Objects.requireNonNull(gameConnectionModel);
        while (true)
            try {
                waitUntilNextRequestAllowed();
                return clientConnection.getClient().post().uri(ApiV1Constants.PLAY_URI).bodyValue(new TurnModel(userTokenModel.userToken(), gameConnectionModel.gameToken(), x, y)).retrieve().bodyToMono(TurnResponseModel.class).block();
            } catch (final WebClientResponseException.TooManyRequests e) {
                log.debug("Too many requests, waiting before attempting to take turn again");
                secondsBetweenRequests++;
            } catch (final WebClientResponseException.NotAcceptable | WebClientResponseException.Gone e) {
                log.debug("It is other's player turn, waiting and trying again");
            } catch (final WebClientResponseException.Conflict e) {
                throw new CoordinatesUsedException(e);
            } catch (final WebClientResponseException.Unauthorized e) {
                log.debug("Game ID forgotten by the API?", e);
                throw new InvalidGameException();
            } catch (final WebClientRequestException e) {
                if (e.getCause() instanceof PrematureCloseException)
                    clientConnection.reconnect();
            }
    }
}