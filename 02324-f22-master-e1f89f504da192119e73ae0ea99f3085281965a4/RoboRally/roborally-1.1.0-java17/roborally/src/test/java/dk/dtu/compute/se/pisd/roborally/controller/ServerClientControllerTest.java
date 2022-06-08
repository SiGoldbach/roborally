package dk.dtu.compute.se.pisd.roborally.controller;

import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

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
}