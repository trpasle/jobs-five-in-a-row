package cz.ondrejguth.cz.jobs.piskvorky.client_api;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TurnResponseModelTest {

    @Test
    void nextTurnPossible() {
        final var winInstance = new TurnResponseModel(201, "me", "foe", "foe", "foe", null);
        Assertions.assertFalse(winInstance.nextTurnPossible());

        final var noWinnerInst = new TurnResponseModel(0, "foe", "me", "me", "", null);
        Assertions.assertTrue(noWinnerInst.nextTurnPossible());

        final var nullWinnerInst = new TurnResponseModel(0, "foe", "me", "me", null, null);
        Assertions.assertTrue(nullWinnerInst.nextTurnPossible());
    }
}