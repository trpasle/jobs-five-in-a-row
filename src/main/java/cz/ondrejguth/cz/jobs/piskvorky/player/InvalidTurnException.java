package cz.ondrejguth.cz.jobs.piskvorky.player;

import org.springframework.web.reactive.function.client.WebClientResponseException;

public class InvalidTurnException extends RuntimeException {
    public InvalidTurnException(WebClientResponseException e) {
        super(e);
    }
}
