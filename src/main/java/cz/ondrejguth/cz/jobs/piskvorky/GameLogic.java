package cz.ondrejguth.cz.jobs.piskvorky;

import cz.ondrejguth.cz.jobs.piskvorky.client_api.*;
import cz.ondrejguth.cz.jobs.piskvorky.player_engine.PlayerEngine;
import cz.ondrejguth.cz.jobs.piskvorky.player_engine.RandomPlayer;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
@Slf4j
public class GameLogic implements ApplicationListener<ApplicationReadyEvent> {
    private final SynchronousClient apiClient;
    private final PlayerEngine playerEngine;

    GameConnectionModel startNewGame() {
        return apiClient.newGame();
    }

    TurnResponseModel doNextMove(final GameConnectionModel connModel, final CoordinateModel[] pastCoordinates)
            throws CoordinatesUsedException, InvalidGameException {
        var newTurn = playerEngine.computeTurn(pastCoordinates);
        return apiClient.play(connModel, newTurn.x(), newTurn.y());
    }


    public void playOneGame() {
        final var connModel = startNewGame();
        var turnResponse = new TurnResponseModel(0, null, null, null, null, new CoordinateModel[0]);
        do {
            try {
                turnResponse = doNextMove(connModel, turnResponse.coordinates());
            } catch (final CoordinatesUsedException e) {
                log.debug("Invalid turn, coordinates already used", e);
                //try again with different coordinates
            }
        } while (turnResponse.nextTurnPossible());
        log.info(turnResponse.winnerId());
    }

    @Override
    public void onApplicationEvent(final ApplicationReadyEvent event) {
        playOneGame();
    }
}
