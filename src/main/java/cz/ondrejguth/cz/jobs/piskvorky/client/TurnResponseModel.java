package cz.ondrejguth.cz.jobs.piskvorky.client;

import java.io.Serializable;

public record TurnResponseModel(int statusCode, String playerCrossId, String playerCircleId,
                                String actualPlayerId, String winnerId,
                                CoordinateModel[] coordinates) implements Serializable {
}
