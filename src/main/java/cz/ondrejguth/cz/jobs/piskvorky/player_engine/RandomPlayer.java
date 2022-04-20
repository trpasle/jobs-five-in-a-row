package cz.ondrejguth.cz.jobs.piskvorky.player_engine;

import cz.ondrejguth.cz.jobs.piskvorky.client_api.ApiV1Constants;
import cz.ondrejguth.cz.jobs.piskvorky.client_api.CoordinateModel;
import org.springframework.stereotype.Service;

import java.util.Random;

@Service
public class RandomPlayer {
    private final Random random = new Random();

    public CoordinateModel computeTurn(final CoordinateModel[] currentCoordinates) {
        return new CoordinateModel(null,
                random.nextInt(ApiV1Constants.MIN_X_COORD, ApiV1Constants.MAX_X_COORD),
                random.nextInt(ApiV1Constants.MIN_Y_COORD, ApiV1Constants.MAX_Y_COORD));
    }
}
