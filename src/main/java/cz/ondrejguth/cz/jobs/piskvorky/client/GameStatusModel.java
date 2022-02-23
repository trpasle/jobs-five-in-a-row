package cz.ondrejguth.cz.jobs.piskvorky.client;

public record GameStatusModel(int statusCode, String playerCrossId, String playerCircleId,
                              String actualPlayerId, String winnerId,
                              CoordinateModel[] coordinates) {
}
