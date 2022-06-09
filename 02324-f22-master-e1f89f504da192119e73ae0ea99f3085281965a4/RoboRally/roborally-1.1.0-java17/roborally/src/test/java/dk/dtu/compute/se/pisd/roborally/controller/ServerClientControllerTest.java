package dk.dtu.compute.se.pisd.roborally.controller;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

class ServerClientControllerTest {


    ServerClientController sTest = new ServerClientController();

    @Test
    void hostGame() throws IOException, InterruptedException {
    }

    @Test
    void connectToGame() throws IOException, InterruptedException {
        sTest.hostGame("Game", "2", "2", 3);
        sTest.connectToGame("Game", "funny");
        String s = sTest.connectToGame("Game", "funny");


    }
}