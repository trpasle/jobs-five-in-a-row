package cz.ondrejguth.cz.jobs.piskvorky.client;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor @Getter
public class TurnModel {
    private final String userToken;
    private final String gameToken;
    private final int positionX;
    private final int positionY;
}
