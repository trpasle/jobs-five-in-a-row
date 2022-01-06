package cz.ondrejguth.cz.jobs.piskvorky.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.Duration;

@Component
public class ApiClientV1 {
    private final WebClient httpClient;
    private final UserTokenModel userTokenModel;

    public ApiClientV1(@Value("@{cz.ondrejguth.cz.jobs.piskvorky.userToken}") String userToken) {
        userTokenModel = new UserTokenModel(userToken);
        httpClient = WebClient.builder()
                .baseUrl("https://piskvorky.jobs.cz/api/v1")
                .defaultHeader("Accept", MediaType.APPLICATION_JSON_VALUE)
                .defaultHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                .build();
    }


    /**
     * Start new game. Either join some existing or create a new one.
     *
     * @return gameId and gameToken of the new game
     * @throws org.springframework.web.reactive.function.client.WebClientException if something goes wrong
     */
    public Mono<GameConnectionModel> newGame() {
        return httpClient
                .post()
                .uri("/connect")
                .bodyValue(userTokenModel)
                .retrieve()
                .bodyToMono(GameConnectionModel.class);
    }

    /**
     * Retrieve the current game status.
     *
     * @param gameToken
     * @return game status: played coordinates, users and their symbol assignment, current player, the winner
     * @throws org.springframework.web.reactive.function.client.WebClientException if something goes wrong
     */
    public Mono<GameStatusModel> retrieveGameStatus(final String gameToken) {
        return httpClient
                .post()
                .uri("/checkStatus")
                .bodyValue(new GameUserTokenModel(userTokenModel.getUserToken(), gameToken))
                .retrieve()
                .bodyToMono(GameStatusModel.class);
    }

    public Mono<TurnResponseModel> playTurn(final String gameToken, final int x, final int y) {
        return httpClient
                .post()
                .uri("/play")
                .bodyValue(new TurnModel(userTokenModel.getUserToken(), gameToken, x, y))
                .retrieve()
                .bodyToMono(TurnResponseModel.class);
    }
}