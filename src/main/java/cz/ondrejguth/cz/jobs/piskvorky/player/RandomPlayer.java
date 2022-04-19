package cz.ondrejguth.cz.jobs.piskvorky.player;

import cz.ondrejguth.cz.jobs.piskvorky.PlayerFacade;
import cz.ondrejguth.cz.jobs.piskvorky.client_api.CoordinateModel;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.Random;

@Service
@Setter
public class RandomPlayer {
    private final Random random = new Random();
    @Autowired
    @Lazy
    private PlayerFacade playerFacade;

    public CoordinateModel computeTurn(final CoordinateModel[] currentCoordinates) {
        return new CoordinateModel(null,
                random.nextInt(playerFacade.getMinXCoordinate(), playerFacade.getMaxXCoordinate()),
                random.nextInt(playerFacade.getMinYCoordinate(), playerFacade.getMaxYCoordinate()));
    }
}
