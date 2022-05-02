package cz.ondrejguth.cz.jobs.piskvorky.player_engine;

import cz.ondrejguth.cz.jobs.piskvorky.client_api.ApiV1Constants;
import cz.ondrejguth.cz.jobs.piskvorky.client_api.CoordinateModel;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Random;

@Service
public class RandomPlayer implements PlayerEngine {
    private final Random random = new Random();

    @Override
    public CoordinateModel computeTurn(final CoordinateModel[] currentCoordinates) {
        CoordinateModel ret = null;
        do {
            ret = new CoordinateModel(null,
                    random.nextInt(ApiV1Constants.MIN_X_COORD, ApiV1Constants.MAX_X_COORD),
                    random.nextInt(ApiV1Constants.MIN_Y_COORD, ApiV1Constants.MAX_Y_COORD));
        } while (Arrays.stream(currentCoordinates).anyMatch(ret::equals));
        return ret;
    }
}
