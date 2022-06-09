package dk.dtu.compute.se.pisd.roborally.view;
import javax.swing.*;
import java.util.List;
import java.util.concurrent.atomic.AtomicIntegerArray;


public class PopUpBoxView {

    public  String gameInstance(String inputDialogue,String messageDialogue){
        final JFrame save = new JFrame();
        String getSaveMessage = JOptionPane.showInputDialog(save,inputDialogue);
        JOptionPane.showMessageDialog(save,messageDialogue+""+getSaveMessage);
        return getSaveMessage;
    }

    public String loadGame(List<String> loadedGames){
        String[] games = new String[loadedGames.size()];
        loadedGames.toArray(games);
        String getGame = (String) JOptionPane.showInputDialog(
                null,
                "Choose a loaded game",
                "What game",
                JOptionPane.QUESTION_MESSAGE,
                null,
                games,
                games[games.length-1]);
        return getGame;
    }

    public String leftOrRight(List<String> leftOrRightCmd){
        String[] options = new String[leftOrRightCmd.size()];
        leftOrRightCmd.toArray(options);
        String getGame = (String) JOptionPane.showInputDialog(
                null,
                "Choose an option",
                "WHICH DIRECTION??",
                JOptionPane.QUESTION_MESSAGE,
                null,
                options,
                options[options.length-1]);
        return getGame;
    }


}
