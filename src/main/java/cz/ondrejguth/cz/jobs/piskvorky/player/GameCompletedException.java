package cz.ondrejguth.cz.jobs.piskvorky.player;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Value;

@AllArgsConstructor
@Getter
public class GameCompletedException extends RuntimeException {
    private final String winnerId;
}
