package cz.ondrejguth.cz.jobs.piskvorky;

import cz.ondrejguth.cz.jobs.piskvorky.client_api.*;
import cz.ondrejguth.cz.jobs.piskvorky.player_engine.PlayerEngine;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class GameLogic implements ApplicationListener<ApplicationReadyEvent> {
    private final SynchronousClient apiClient;
    private final PlayerEngine playerEngine;
    private final String userId;

    public GameLogic(
            SynchronousClient apiClient,
            PlayerEngine playerEngine,
            @Value("${cz.ondrejguth.cz.jobs.piskvorky.userId}") String userId) {
        this.apiClient = apiClient;
        this.playerEngine = playerEngine;
        this.userId = userId;
    }


    /**
     *
     * @return true if this player won, false if this player lost, null if the game is left unfinished
     */
    public Boolean tryWinOneGame() {
        try {
            final var connModel = apiClient.newGame();
            var turnResponse = new TurnResponseModel(0, null, null, null, null, new CoordinateModel[0]);
            do {
                try {
                    var newTurn = playerEngine.computeTurn(turnResponse.coordinates());
                    turnResponse = apiClient.play(connModel, newTurn.x(), newTurn.y());
                } catch (final CoordinatesUsedException e) {
                    log.debug("Invalid turn, coordinates already used", e);
                    //try again with different coordinates
                }
            } while (turnResponse.nextTurnPossible());
            log.info(turnResponse.winnerId());
            return userId.equals(turnResponse.winnerId());
        } catch (final TooLongWaitingException|InvalidGameException e) {
            log.warn("API too busy or forgotten this game, giving up", e);
            return null;
        }
    }

    @Override
    public void onApplicationEvent(final ApplicationReadyEvent event) {
        tryWinOneGame();
    }
}
