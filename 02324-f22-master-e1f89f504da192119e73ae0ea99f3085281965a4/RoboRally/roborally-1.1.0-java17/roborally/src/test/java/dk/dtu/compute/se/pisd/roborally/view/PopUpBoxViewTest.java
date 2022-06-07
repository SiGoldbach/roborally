package dk.dtu.compute.se.pisd.roborally.view;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
public class PopUpBoxViewTest {

    @Test
    void loadGameTest(){
        List<String> games = new ArrayList<>();
        games.add("awdnin");
        games.add("jaja");
        games.add("nono");
        new PopUpBoxView().loadGame(games);

    }




}