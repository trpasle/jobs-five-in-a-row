package cz.ondrejguth.cz.jobs.piskvorky.client_api;

public final class InvalidTurnException extends RuntimeException {

    public InvalidTurnException(Exception e) {
        super(e);
    }
}
