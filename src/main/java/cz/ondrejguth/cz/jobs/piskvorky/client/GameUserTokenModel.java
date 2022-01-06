package cz.ondrejguth.cz.jobs.piskvorky.client;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor @Getter
public class GameUserTokenModel {
    private final String userToken;
    private final String gameToken;
}
