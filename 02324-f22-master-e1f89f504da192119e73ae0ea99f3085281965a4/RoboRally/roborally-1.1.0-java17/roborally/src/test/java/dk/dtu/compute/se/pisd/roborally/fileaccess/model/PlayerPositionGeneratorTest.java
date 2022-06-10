package dk.dtu.compute.se.pisd.roborally.fileaccess.model;

import dk.dtu.compute.se.pisd.roborally.controller.ServerClientController;
import dk.dtu.compute.se.pisd.roborally.fileaccess.LoadBoard;
import dk.dtu.compute.se.pisd.roborally.model.Board;
import dk.dtu.compute.se.pisd.roborally.model.Heading;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;

class PlayerPositionGeneratorTest {

    /**
     * A test about important logic to the server aka. the player positions.
     *
     * @throws IOException
     * @throws InterruptedException
     */
    @Test
    void testingSomeServerLogic() throws IOException, InterruptedException {
        String board = new ServerClientController().getBoard("BigFunnyGame");
        Board toTest = LoadBoard.loadBoard(board);
        System.out.println(new PlayerPositionGenerator().toString(toTest));

    }

    /**
     * Here we get the board from the server, so it has to be on the server for this test to work.
     * This is fine since this unit test is primarily written for development purposes,
     * instead of proving that current code is correct.
     *
     * @throws IOException
     * @throws InterruptedException
     */

    @Test
    void updatePlayersPosition() throws Exception {
        String board = new ServerClientController().getBoard("BigFunnyGame");
        Board toTest = LoadBoard.loadBoard(board);
        new PlayerPositionGenerator().updatePlayersPosition("red,SOUTH,0,6-green,WEST,1,7-", toTest);
        Assertions.assertEquals(toTest.getPlayers().get(0).getSpace().y, 6);
        Assertions.assertEquals(toTest.getPlayers().get(1).getSpace().y, 7);
        Assertions.assertEquals(toTest.getPlayers().get(0).getHeading(), Heading.SOUTH);
        Assertions.assertEquals(toTest.getPlayers().get(1).getHeading(), Heading.WEST);


    }
}