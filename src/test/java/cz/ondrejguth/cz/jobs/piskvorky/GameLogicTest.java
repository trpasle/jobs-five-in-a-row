package cz.ondrejguth.cz.jobs.piskvorky;

import cz.ondrejguth.cz.jobs.piskvorky.client_api.*;
import cz.ondrejguth.cz.jobs.piskvorky.player_engine.PlayerEngine;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class GameLogicTest {
    @InjectMocks
    private GameLogic instance;

    @Mock
    private SynchronousClient apiClient;

    @Mock
    private PlayerEngine playerEngine;

    private final GameConnectionModel testConnectionModel = new GameConnectionModel(201, "token", "id");
    private final CoordinateModel coordShould = new CoordinateModel("", -1, 0);

    @BeforeEach
    private void setUp() {
        ReflectionTestUtils.setField(instance, "userId", "me");
    }


    private void tryWinOneGame(String winnerId) {
        final CoordinateModel coordShould2 = new CoordinateModel("", -1, 1);
        Mockito.when(apiClient.newGame()).thenReturn(testConnectionModel);
        Mockito.when(playerEngine.computeTurn(Mockito.any())).thenReturn(coordShould, coordShould, coordShould2);
        Mockito.when(apiClient.play(Mockito.any(), Mockito.anyInt(), Mockito.anyInt())).thenReturn(
                new TurnResponseModel(
                        201,
                        "me",
                        "foe",
                        "foe",
                        null,
                        new CoordinateModel[]{coordShould}
                )
        ).thenThrow(CoordinatesUsedException.class).thenReturn(
                new TurnResponseModel(
                        226,
                        "me",
                        "foe",
                        "foe",
                        winnerId,
                        new CoordinateModel[]{coordShould2}
                )
        );

        var ret = instance.tryWinOneGame();
        Assertions.assertEquals(winnerId.equals("me"), ret);

        Mockito.verify(apiClient, Mockito.atLeastOnce()).newGame();
        Mockito.verify(playerEngine, Mockito.atLeast(3)).computeTurn(Mockito.any());
        Mockito.verify(apiClient, Mockito.atLeastOnce()).play(testConnectionModel, coordShould.x(), coordShould.y());
        Mockito.verify(apiClient, Mockito.atLeastOnce()).play(testConnectionModel, coordShould2.x(), coordShould2.y());
    }

    @Test
    void tryWinOneGameMeWinning() {
        tryWinOneGame("me");
    }

    @Test
    void tryWinOneGameMeLosing() {
        tryWinOneGame("foe");
    }

    @Test
    void tryWinOneGameCannotConnect() {
        Mockito.when(apiClient.newGame()).thenThrow(TooLongWaitingException.class);

        Assertions.assertNull(instance.tryWinOneGame());

        Mockito.verify(apiClient, Mockito.atLeastOnce()).newGame();
        Mockito.verifyNoMoreInteractions(apiClient);
    }

    private void tryWinOneGameCannotPlay(final Class<? extends Exception> exception) {
        Mockito.when(apiClient.newGame()).thenReturn(testConnectionModel);
        Mockito.when(playerEngine.computeTurn(Mockito.any())).thenReturn(coordShould);
        Mockito.when(apiClient.play(Mockito.any(), Mockito.anyInt(), Mockito.anyInt())).thenThrow(exception);

        Assertions.assertNull(instance.tryWinOneGame());

        Mockito.verify(apiClient, Mockito.atLeastOnce()).newGame();
        Mockito.verify(playerEngine, Mockito.atLeastOnce()).computeTurn(Mockito.any());
        Mockito.verify(apiClient, Mockito.atLeastOnce()).play(testConnectionModel, coordShould.x(), coordShould.y());
    }

    @Test
    void tryWinOneGamePlayTooLong() {
        tryWinOneGameCannotPlay(TooLongWaitingException.class);
    }
    @Test
    void tryWinOneGamePlayInvalidGame() {
        tryWinOneGameCannotPlay(InvalidGameException.class);
    }


    @Test
    void onApplicationEvent() {
        var spyInstance = Mockito.spy(instance);
        Mockito.doReturn(null).when(spyInstance).tryWinOneGame();

        //pass the null value is incorrect, though it should work
        spyInstance.onApplicationEvent(null);

        Mockito.verify(spyInstance, Mockito.atLeastOnce()).tryWinOneGame();
    }
}