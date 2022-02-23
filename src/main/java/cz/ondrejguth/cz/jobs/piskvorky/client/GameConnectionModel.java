package cz.ondrejguth.cz.jobs.piskvorky.client;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.Serializable;

public record GameConnectionModel(int statusCode, String gameToken,
                                  String gameId) implements Serializable {
}
