package dk.dtu.compute.se.pisd.roborally.controller;

import dk.dtu.compute.se.pisd.roborally.model.Board;
import dk.dtu.compute.se.pisd.roborally.model.Heading;
import dk.dtu.compute.se.pisd.roborally.model.Player;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class GameControllerTest {

    private final int TEST_WIDTH = 8;
    private final int TEST_HEIGHT = 8;

    private GameController gameController;

    @BeforeEach
    void setUp() {
        Board board = new Board(TEST_WIDTH, TEST_HEIGHT);
        gameController = new GameController(board);
        for (int i = 0; i < 6; i++) {
            Player player = new Player(board, null, "Player " + i);
            board.addPlayer(player);
            player.setSpace(board.getSpace(i, i));
            player.setHeading(Heading.values()[i % Heading.values().length]);
        }
        board.setCurrentPlayer(board.getPlayer(0));
    }

    @AfterEach
    void tearDown() {
        gameController = null;
    }

    @Test
    void moveCurrentPlayerToSpace() {
        Board board = gameController.board;
        Player player1 = board.getPlayer(0);
        Player player2 = board.getPlayer(1);

        gameController.moveCurrentPlayerToSpace(board.getSpace(0, 4));

        Assertions.assertEquals(player1, board.getSpace(0, 4).getPlayer(), "Player " + player1.getName() + " should beSpace (0,4)!");
        Assertions.assertNull(board.getSpace(0, 0).getPlayer(), "Space (0,0) should be empty!");
        Assertions.assertEquals(player2, board.getCurrentPlayer(), "Current player should be " + player2.getName() + "!");
    }

    @Test
    void FastForward() {
        Board board = gameController.board;
        gameController.board.getCurrentPlayer().setHeading(Heading.EAST);
        gameController.fastForward(gameController.board.getCurrentPlayer());

        Assertions.assertEquals(gameController.board.getCurrentPlayer().getSpace(), board.getSpace(2, 0));
    }

    @Test
    void moveBack() {
        Board board = gameController.board;

        System.out.println(gameController.board.getCurrentPlayer().getSpace().x);
        System.out.println(gameController.board.getCurrentPlayer().getSpace().y);


        gameController.board.getCurrentPlayer().setHeading(Heading.EAST);
        gameController.fastForward(gameController.board.getCurrentPlayer());
        gameController.moveBack(gameController.board.getCurrentPlayer());

        System.out.println(gameController.board.getCurrentPlayer().getSpace().x);
        System.out.println(gameController.board.getCurrentPlayer().getSpace().y);

        Assertions.assertEquals(gameController.board.getCurrentPlayer().getSpace(), board.getSpace(1, 0));
    }

    @Test
    void moveThreeTimes() {
        Board board = gameController.board;
        gameController.board.getCurrentPlayer().setHeading(Heading.EAST);
        gameController.moveThree(gameController.board.getCurrentPlayer());

        Assertions.assertEquals(gameController.board.getCurrentPlayer().getSpace(), board.getSpace(3, 0));
    }

    @Test
    void moveForward() {
        Board board = gameController.board;
        Player current = board.getCurrentPlayer();

        gameController.moveForward(current);

        Assertions.assertEquals(current, board.getSpace(0, 1).getPlayer(), "Player " + current.getName() + " should beSpace (0,1)!");
        Assertions.assertEquals(Heading.SOUTH, current.getHeading(), "Player 0 should be heading SOUTH!");
        Assertions.assertNull(board.getSpace(0, 0).getPlayer(), "Space (0,0) should be empty!");
    }

    /**
     * Here is an acceptance test for uturn method this test simply tries to see wether the method works as intended.
     */
    @Test
    void U_turn() {
        Board board = gameController.board;
        Player current = board.getCurrentPlayer();
        current.setHeading(Heading.NORTH);
        gameController.uTurn(current);
        Assertions.assertEquals(Heading.SOUTH, current.getHeading());
    }

    @Test
    void turn_left() {
        Board board = gameController.board;
        Player current = board.getCurrentPlayer();
        current.setHeading(Heading.NORTH);
        gameController.turnRight(current);
        Assertions.assertEquals(Heading.EAST, current.getHeading());

    }

    @Test
    void turn_right() {
        Board board = gameController.board;
        Player current = board.getCurrentPlayer();
        current.setHeading(Heading.NORTH);
        gameController.turnLeft(current);
        Assertions.assertEquals(Heading.WEST, current.getHeading());

    }

    @Test
    void doAction() {
        Board board = gameController.board;

        Player player = gameController.board.getCurrentPlayer();

        ConveyorBelt belt = new ConveyorBelt(Heading.SOUTH);
        gameController.board.getSpace(0, 0).addActions(belt);
        gameController.board.getSpace(0, 0).doActions(gameController);

        Assertions.assertEquals(player.getSpace(), board.getSpace(0, 1), "Player is on " + player.getSpace().x + " " + player.getSpace().y);
        Assertions.assertEquals(player.getHeading(), Heading.SOUTH);
        gameController.moveForward(player);
        Assertions.assertEquals(player.getSpace(), board.getSpace(0, 2));
        gameController.moveBack(player);
        gameController.moveBack(player);
        gameController.board.getSpace(0, 0).doActions(gameController);
        Assertions.assertEquals(player.getSpace(), board.getSpace(0, 1), "Player is on " + player.getSpace().x + " " + player.getSpace().y);
        Assertions.assertEquals(belt.getHeading(), Heading.SOUTH);

    }

    /**
     * I am here making a method testing whether the antenna was found correctly.
     * First i am asserting the antenna is there Afterwords i check the field is in the rigth place.
     * So i accept test both functionalities of the method
     */
    @Test
    void findAntenna() {
        gameController.board.getSpace(2, 2).getActions().add(new Antenna());
        Assertions.assertEquals(gameController.board.getAntennaSpace(), null);
        Assertions.assertEquals(gameController.findAntenna(), true);
        Assertions.assertEquals(gameController.board.getAntennaSpace(), gameController.board.getSpace(2, 2));

    }
    /**
     * Testing the first step in figuring out who is closest to the antenna
     */

    @Test
    void testFindDifferenceBetweenTwoFields() {
        int diff = gameController.board.calculateDistance(gameController.board.getSpace(2, 2), gameController.board.getSpace(4, 4));
        Assertions.assertEquals(diff, 4);
        diff = gameController.board.calculateDistance(gameController.board.getSpace(3, 3), gameController.board.getSpace(4, 4));
        Assertions.assertEquals(diff, 2);
        diff = gameController.board.calculateDistance(gameController.board.getSpace(0, 0), gameController.board.getSpace(6, 6));
        Assertions.assertEquals(diff, 12);


    }

    /**
     * Testing whether the method for finding the first player works properly.
     *
     */
    @Test
    void FindClosestPlayer() {
        gameController.board.setAntennaSpace(gameController.board.getSpace(0,0));
        Player testPlayer=gameController.board.getSpace(0,0).getPlayer();
        Assertions.assertEquals(gameController.findFirstPLayerToMoveRobot(),testPlayer);
    }

}