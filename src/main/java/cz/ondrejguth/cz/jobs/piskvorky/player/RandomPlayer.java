package cz.ondrejguth.cz.jobs.piskvorky.player;

import cz.ondrejguth.cz.jobs.piskvorky.client.ApiClientV1;
import cz.ondrejguth.cz.jobs.piskvorky.client.GameConnectionModel;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.util.Random;

@Service
@AllArgsConstructor
public class RandomPlayer {
    private final ApiClientV1 client;
    private final Random random = new Random();

    public GameConnectionModel startGame() {
        return client.newGame().block();
    }

    public void playNextTurn(final GameConnectionModel gameConnectionModel) {
        try {
            var respModel = client
                    .play(gameConnectionModel.gameToken(), random.nextInt(-20, 20), random.nextInt(-20, 20))
                    .block();
            if (respModel.statusCode() == 226)
                throw new GameCompletedException(respModel.winnerId());
        } catch (final WebClientResponseException e) {
            throw new InvalidTurnException(e);
        }
    }
}
