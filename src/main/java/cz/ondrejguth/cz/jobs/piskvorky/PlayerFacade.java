package cz.ondrejguth.cz.jobs.piskvorky;

import cz.ondrejguth.cz.jobs.piskvorky.client_api.*;
import cz.ondrejguth.cz.jobs.piskvorky.player_engine.RandomPlayer;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.Arrays;

@Service
@AllArgsConstructor
@Slf4j
public class PlayerFacade implements ApplicationListener<ApplicationReadyEvent> {
    private final SynchronousClient client;
    private final RandomPlayer player;

    //TODO redesign this method
    //it is not testable
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

                Assert.isTrue(Arrays.stream(turnResponse.coordinates()).noneMatch(newTurn::equals),
                        "Player engine error: it provided coordinates that are already used");

                turnResponse = client.play(connModel, newTurn.x(), newTurn.y());
            } catch (final CoordinatesUsedException e) {
                log.debug("Invalid turn, coordinates already used", e);
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
