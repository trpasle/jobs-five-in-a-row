package cz.ondrejguth.cz.jobs.piskvorky.client;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.Serializable;

@AllArgsConstructor @Getter
public class GameConnectionModel implements Serializable {
    private final int statusCode;
    private final String gameToken;
    private final String gameId;
}
