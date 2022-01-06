package cz.ondrejguth.cz.jobs.piskvorky.client;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.Serializable;

@AllArgsConstructor @Getter
public class TurnResponseModel implements Serializable {
    private final int statusCode;
    private final String playerCrossId;
    private final String playerCircleId;
    private final String actualPlayerId;
    private final String winnerId;
    private final CoordinateModel [] coordinates;
}
