package cz.ondrejguth.cz.jobs.piskvorky;

import cz.ondrejguth.cz.jobs.piskvorky.player.GameCompletedException;
import cz.ondrejguth.cz.jobs.piskvorky.player.InvalidTurnException;
import cz.ondrejguth.cz.jobs.piskvorky.player.NeedWaitException;
import cz.ondrejguth.cz.jobs.piskvorky.player.RandomPlayer;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.util.concurrent.TimeUnit;

@Component
@AllArgsConstructor
@Slf4j
public class Player implements ApplicationListener<ApplicationReadyEvent> {
    private final RandomPlayer playerLogic;
    private static final int SECONDS_BETWEEN_REQUESTS = 2;

    public void playGame() {
        var connModel = playerLogic.startGame();
        try {
            while (true) {
                try {
                    playerLogic.playNextTurn(connModel);
                } catch (final NeedWaitException e) {
                    try {
                        log.debug("Waiting for " + SECONDS_BETWEEN_REQUESTS + " seconds.");
                        TimeUnit.SECONDS.sleep(SECONDS_BETWEEN_REQUESTS);
                    } catch (InterruptedException ex) {}
                } catch (final InvalidTurnException e) {
                    log.debug("Invalid turn", e);
                    //try again
                }
            }
        } catch (final GameCompletedException e) {
            log.info(e.getWinnerId());
        }
    }

    @Override
    public void onApplicationEvent(final ApplicationReadyEvent event) {
        playGame();
    }
}
