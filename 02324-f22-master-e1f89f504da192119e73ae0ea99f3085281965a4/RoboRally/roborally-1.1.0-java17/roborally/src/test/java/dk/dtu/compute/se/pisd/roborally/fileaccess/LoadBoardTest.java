package dk.dtu.compute.se.pisd.roborally.fileaccess;

import dk.dtu.compute.se.pisd.roborally.controller.GameController;
import dk.dtu.compute.se.pisd.roborally.controller.ServerClientController;
import dk.dtu.compute.se.pisd.roborally.model.Phase;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

class LoadBoardTest {
    /**
     * Testing new board method for loadBoard
     */
    @Test
    void loadBoard() throws IOException, InterruptedException {
        try {


            new ServerClientController().getBoard("defaultboard");
        }catch (InterruptedException e){
            fail();
        }
        GameController gtest =new GameController(LoadBoard.loadBoard(new ServerClientController().getBoard("NewFGame")));
        System.out.println(gtest.board.width);
        System.out.println(gtest.board.height);
        Assertions.assertEquals(gtest.board.getPhase(), Phase.PROGRAMMING);
    }
}