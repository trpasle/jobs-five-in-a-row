package cz.ondrejguth.cz.jobs.piskvorky.client_api;

import java.io.Serializable;

public record TurnResponseModel(int statusCode, String playerCrossId, String playerCircleId,
                                String actualPlayerId, String winnerId,
                                CoordinateModel[] coordinates) implements Serializable {

    public boolean nextTurnPossible() {
        return winnerId == null || winnerId.length() <= 0;
    }
}
