package cz.ondrejguth.cz.jobs.piskvorky;

import cz.ondrejguth.cz.jobs.piskvorky.client_api.CoordinateModel;
import cz.ondrejguth.cz.jobs.piskvorky.client_api.GameConnectionModel;
import cz.ondrejguth.cz.jobs.piskvorky.client_api.SynchronousClient;
import cz.ondrejguth.cz.jobs.piskvorky.client_api.TurnResponseModel;
import cz.ondrejguth.cz.jobs.piskvorky.player_engine.RandomPlayer;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class PlayerFacadeTest {
    @InjectMocks
    private PlayerFacade instance;

    @Mock
    private SynchronousClient apiClient;

    @Mock
    private RandomPlayer playerEngine;

    @Test
    void playGame() {
        final var conn = new GameConnectionModel(201, "token", "id");
        final var turn = new CoordinateModel("", 1, 1);
        Mockito.when(apiClient.newGame()).thenReturn(conn);
        Mockito.when(playerEngine.computeTurn(Mockito.any())).thenReturn(turn);
        Mockito.when(apiClient.play(conn, turn.x(), turn.y()))
                .thenReturn(new TurnResponseModel(
                        226,
                        "me",
                        "foe",
                        "foe",
                        "foe",
                        new CoordinateModel[]{
                                turn
                        }
                ));
        Assertions.assertDoesNotThrow(() -> instance.playGame());
    }

    @Test
    void onApplicationEvent() {
        var spyInstance = Mockito.spy(instance);
        Mockito.doNothing().when(spyInstance).playGame();

        //pass the null value is incorrect, though it should work
        spyInstance.onApplicationEvent(null);

        Mockito.verify(spyInstance, Mockito.atLeastOnce()).playGame();
    }
}