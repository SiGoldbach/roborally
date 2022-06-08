package dk.dtu.compute.se.pisd.roborally.TestsWithServer;

import dk.dtu.compute.se.pisd.roborally.controller.ServerClientController;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

/**
 * These test are used to test if the client side of our server client relationship works,
 * so they won't work if the server is closed.
 */
class ServerClientControllerTest {

    @Test
    void getBoard() throws IOException, InterruptedException {
        String board = new ServerClientController().getBoard("simmple");
        if (board == null)
            fail();
        System.out.println(board);

    }

    @Test
    void saveBoard() throws IOException, InterruptedException {
        new ServerClientController().saveBoard("hat", "test");
    }
    @Test
    void GetListOfBoards() throws IOException, InterruptedException {
        System.out.println(new ServerClientController().possibleBoards());
    }
    @Test
    void GettingBoard() throws IOException, InterruptedException {
        new ServerClientController().saveBoard("Board","Boardname");
    }
}