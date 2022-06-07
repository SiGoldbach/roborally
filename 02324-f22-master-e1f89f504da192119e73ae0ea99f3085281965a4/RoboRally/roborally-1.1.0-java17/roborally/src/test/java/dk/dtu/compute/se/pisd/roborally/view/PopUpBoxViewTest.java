package dk.dtu.compute.se.pisd.roborally.view;

import dk.dtu.compute.se.pisd.roborally.controller.ServerClientController;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class PopUpBoxViewTest {

    @Test
    void loadGameTest() {
        List<String> games = new ArrayList<>();
        games.add("awdnin");
        games.add("jaja");
        games.add("nono");
        new PopUpBoxView().loadGame(games);

    }

    /**
     * Testing two get requests and the popupBox
     * @throws IOException
     * @throws InterruptedException
     */
    @Test
    void testOfGetBoardsAndMenuAndGetSpecificBoard() throws IOException, InterruptedException {
        String val = new PopUpBoxView().loadGame(new ServerClientController().possibleBoards());
        String board = new ServerClientController().getBoard(val);
        System.out.println(board);

    }


}