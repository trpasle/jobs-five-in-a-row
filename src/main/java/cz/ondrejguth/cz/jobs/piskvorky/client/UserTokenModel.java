package cz.ondrejguth.cz.jobs.piskvorky.client;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.Serializable;

@Getter
@AllArgsConstructor
public class UserTokenModel implements Serializable {
    private final String userToken;
}
