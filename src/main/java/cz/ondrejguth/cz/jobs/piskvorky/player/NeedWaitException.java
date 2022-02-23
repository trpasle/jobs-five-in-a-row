package cz.ondrejguth.cz.jobs.piskvorky.player;

import org.springframework.web.reactive.function.client.WebClientResponseException;

public class NeedWaitException extends RuntimeException {
    public NeedWaitException(WebClientResponseException e) {
        super(e);
    }
}
