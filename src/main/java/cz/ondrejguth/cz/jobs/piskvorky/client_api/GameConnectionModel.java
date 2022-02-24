package cz.ondrejguth.cz.jobs.piskvorky.client_api;

import java.io.Serializable;

public record GameConnectionModel(int statusCode, String gameToken,
                                  String gameId) implements Serializable {
}
