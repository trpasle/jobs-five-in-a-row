package cz.ondrejguth.cz.jobs.piskvorky.client_api;

import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

public class ApiClientV1Util {
    public static final int MIN_COORDINATE = -20;
    public static final int MAX_COORDINATE = 20;

    /**
     * Start new game. Either join some existing or create a new one.
     *
     * @return gameId and gameToken of the new game
     * @throws org.springframework.web.reactive.function.client.WebClientException if something goes wrong
     */
    Mono<GameConnectionModel> newGame(final WebClient httpClient, final UserTokenModel userTokenModel) {
        return httpClient.post().uri("/connect").bodyValue(userTokenModel).retrieve().bodyToMono(GameConnectionModel.class);
    }

    /**
     * Retrieve the current game status.
     *
     * @param gameToken
     * @return game status: played coordinates, users and their symbol assignment, current player, the winner
     * @throws org.springframework.web.reactive.function.client.WebClientException if something goes wrong
     */
    Mono<GameStatusModel> retrieveGameStatus(final String gameToken, final WebClient httpClient, final UserTokenModel userTokenModel) {
        return httpClient.post().uri("/checkStatus").bodyValue(new GameUserTokenModel(userTokenModel.userToken(), gameToken)).retrieve().bodyToMono(GameStatusModel.class);
    }

    Mono<TurnResponseModel> play(final String gameToken, final int x, final int y, final WebClient httpClient, final UserTokenModel userTokenModel) {
        return httpClient.post().uri("/play").bodyValue(new TurnModel(userTokenModel.userToken(), gameToken, x, y)).retrieve().bodyToMono(TurnResponseModel.class);
    }
}
