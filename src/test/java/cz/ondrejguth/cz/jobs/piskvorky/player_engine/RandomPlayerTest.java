package cz.ondrejguth.cz.jobs.piskvorky.player_engine;

import cz.ondrejguth.cz.jobs.piskvorky.client_api.CoordinateModel;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class RandomPlayerTest {
    private RandomPlayer instance = new RandomPlayer();

    @Test
    void computeTurn() {
        var ret = instance.computeTurn(new CoordinateModel[0]);
        Assertions.assertNotNull(ret);
    }
}