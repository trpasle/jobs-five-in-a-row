package cz.ondrejguth.cz.jobs.piskvorky;

import cz.ondrejguth.cz.jobs.piskvorky.client_api.CoordinateModel;
import cz.ondrejguth.cz.jobs.piskvorky.client_api.GameConnectionModel;
import cz.ondrejguth.cz.jobs.piskvorky.client_api.SynchronousClient;
import cz.ondrejguth.cz.jobs.piskvorky.client_api.TurnResponseModel;
import cz.ondrejguth.cz.jobs.piskvorky.player_engine.PlayerEngine;
import cz.ondrejguth.cz.jobs.piskvorky.player_engine.RandomPlayer;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class GameLogicTest {
    @InjectMocks
    private GameLogic instance;

    @Mock
    private SynchronousClient apiClient;

    @Mock
    private PlayerEngine playerEngine;

    private final GameConnectionModel testConnectionModel = new GameConnectionModel(201, "token", "id");

    @Test
    void startNewGame() {
        Mockito.when(apiClient.newGame()).thenReturn(testConnectionModel);
        final var ret = instance.startNewGame();
        Assertions.assertEquals(testConnectionModel, ret);
        Mockito.verify(apiClient, Mockito.atLeastOnce()).newGame();
        Mockito.verifyNoMoreInteractions(apiClient);
    }

    @Test
    void doNextMove() {
        final var emptyPastCoordinates = new CoordinateModel[0];
        final var coordShould = new CoordinateModel("", -1, 0);
        final var turnRespShould = new TurnResponseModel(
                201,
                "me",
                "foe",
                "foe",
                null,
                new CoordinateModel[]{coordShould}
        );
        Mockito.when(playerEngine.computeTurn(Mockito.any())).thenReturn(turnRespShould.coordinates()[0]);
        Mockito.when(apiClient.play(Mockito.any(), Mockito.anyInt(), Mockito.anyInt())).thenReturn(turnRespShould);

        final var ret = instance.doNextMove(testConnectionModel, emptyPastCoordinates);

        Assertions.assertTrue(ret.coordinates().length > 0);
        Assertions.assertEquals(turnRespShould, ret);
        Mockito.verify(playerEngine, Mockito.atLeastOnce()).computeTurn(emptyPastCoordinates);
        Mockito.verify(apiClient, Mockito.atLeastOnce()).play(testConnectionModel, coordShould.x(), coordShould.y());
    }


    @Test
    void playGame() {
//        final var turn = new CoordinateModel("", 1, 1);
//        Mockito.when(apiClient.newGame()).thenReturn(testConnectionModel);
//        Mockito.when(playerEngine.computeTurn(Mockito.any())).thenReturn(turn);
//        Mockito.when(apiClient.play(testConnectionModel, turn.x(), turn.y()))
//                .thenReturn(new TurnResponseModel(
//                        226,
//                        "me",
//                        "foe",
//                        "foe",
//                        "foe",
//                        new CoordinateModel[]{
//                                turn
//                        }
//                ));
//        Assertions.assertDoesNotThrow(() -> instance.playOneGame());
    }

    @Test
    void onApplicationEvent() {
        var spyInstance = Mockito.spy(instance);
        Mockito.doNothing().when(spyInstance).playOneGame();

        //pass the null value is incorrect, though it should work
        spyInstance.onApplicationEvent(null);

        Mockito.verify(spyInstance, Mockito.atLeastOnce()).playOneGame();
    }
}