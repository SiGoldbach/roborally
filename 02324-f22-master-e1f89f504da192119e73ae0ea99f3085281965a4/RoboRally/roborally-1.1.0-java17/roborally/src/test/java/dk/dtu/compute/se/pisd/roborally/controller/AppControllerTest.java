package dk.dtu.compute.se.pisd.roborally.controller;

import dk.dtu.compute.se.pisd.roborally.fileaccess.LoadBoard;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AppControllerTest {
    public static final String BOARDSFOLDER = "boards";
    private static final String DEFAULTBOARD = "defaultboard";
    private static final String JSON_EXT = "json";

    @Test
    void newGame() {
    }

    /**
     * Test to see whether the file was found properly, was really useful for developing p4.
     * Furthermore, if this test suddenly fails being able to save a game definitely won't be possible if this test fails.
     */
    @Test
    void FileLoadingTest(){
        ClassLoader classLoader = LoadBoard.class.getClassLoader();
        // TODO: this is not very defensive, and will result in a NullPointerException
        //       when the folder "resources" does not exist! But, it does not need
        //       the file "simpleCards.json" to exist!
        String filename =
                classLoader.getResource(BOARDSFOLDER).getPath() + "/" + DEFAULTBOARD + "." + JSON_EXT;
        System.out.println(filename);


    }
}