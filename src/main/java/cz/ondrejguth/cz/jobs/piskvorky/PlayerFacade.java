package cz.ondrejguth.cz.jobs.piskvorky;

import cz.ondrejguth.cz.jobs.piskvorky.client_api.CoordinateModel;
import cz.ondrejguth.cz.jobs.piskvorky.client_api.InvalidTurnException;
import cz.ondrejguth.cz.jobs.piskvorky.client_api.SynchronousClient;
import cz.ondrejguth.cz.jobs.piskvorky.client_api.TurnResponseModel;
import cz.ondrejguth.cz.jobs.piskvorky.player.RandomPlayer;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
@Slf4j
public class PlayerFacade implements ApplicationListener<ApplicationReadyEvent> {
    private final SynchronousClient client;
    private final RandomPlayer player;

    public int getMinCoordinate() {
        return client.getMinCoordinate();
    }

    public int getMaxCoordinate() {
        return client.getMaxCoordinate();
    }


    public void playGame() {
        final var connModel = client.newGame();
        TurnResponseModel turnResponse = new TurnResponseModel(0,
                null,
                null,
                null,
                null,
                new CoordinateModel[0]);
        do {
            try {
                final var newTurn = player.computeTurn(turnResponse.coordinates());
                turnResponse = client.play(connModel, newTurn.x(), newTurn.y());
            } catch (final InvalidTurnException e) {
                log.debug("Invalid turn", e);
                //try again with different coordinates
            }
        } while (turnResponse.winnerId() == null || turnResponse.winnerId().length() == 0);
        log.info(turnResponse.winnerId());
    }

    @Override
    public void onApplicationEvent(final ApplicationReadyEvent event) {
        playGame();
    }
}
