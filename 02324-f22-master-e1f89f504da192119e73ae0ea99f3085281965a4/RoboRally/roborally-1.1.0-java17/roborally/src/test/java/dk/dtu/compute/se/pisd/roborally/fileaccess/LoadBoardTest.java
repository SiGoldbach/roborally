package dk.dtu.compute.se.pisd.roborally.fileaccess;

import dk.dtu.compute.se.pisd.roborally.controller.GameController;
import dk.dtu.compute.se.pisd.roborally.controller.ServerClientController;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

class LoadBoardTest {
    /**
     * Testing new board method for loadBoard
     */
    @Test
    void loadBoard() throws IOException, InterruptedException {
        new GameController(LoadBoard.loadBoard(new ServerClientController().getBoard("defaultboard")));
    }
}