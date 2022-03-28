package cz.ondrejguth.cz.jobs.piskvorky.client_api;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class WebClientConnectionManagerTest {
    private final WebClientConnectionManager managerInstance = new WebClientConnectionManager();

    @Test
    void getInstance() {
        Assertions.assertNotNull(managerInstance.getClient());
    }

    @Test
    void reconnect() {
        var clientInstance1 = managerInstance.getClient();
        managerInstance.reconnect();
        var clientInstance2 = managerInstance.getClient();
        Assertions.assertNotSame(clientInstance1, clientInstance2);
    }
}