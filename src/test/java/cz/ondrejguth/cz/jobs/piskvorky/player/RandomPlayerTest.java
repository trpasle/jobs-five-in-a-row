package cz.ondrejguth.cz.jobs.piskvorky.player;

import cz.ondrejguth.cz.jobs.piskvorky.PlayerFacade;
import cz.ondrejguth.cz.jobs.piskvorky.client_api.CoordinateModel;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class RandomPlayerTest {
    @InjectMocks
    private RandomPlayer instance;

    @Mock
    private PlayerFacade playerFacade;

    @Test
    void computeTurn() {
        Mockito.when(playerFacade.getMaxXCoordinate()).thenReturn(1);
        Mockito.when(playerFacade.getMaxYCoordinate()).thenReturn(1);
        var ret = instance.computeTurn(new CoordinateModel[0]);
        Assertions.assertNotNull(ret);
    }
}