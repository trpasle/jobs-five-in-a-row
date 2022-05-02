package cz.ondrejguth.cz.jobs.piskvorky.player_engine;

import cz.ondrejguth.cz.jobs.piskvorky.client_api.CoordinateModel;

public interface PlayerEngine {
    CoordinateModel computeTurn(CoordinateModel[] currentCoordinates);
}
