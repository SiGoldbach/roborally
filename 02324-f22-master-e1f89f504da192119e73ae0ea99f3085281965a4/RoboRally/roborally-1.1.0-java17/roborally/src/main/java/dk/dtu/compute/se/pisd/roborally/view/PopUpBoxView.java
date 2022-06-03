package dk.dtu.compute.se.pisd.roborally.view;
import javax.swing.*;



public class PopUpBoxView {
    private JFrame SavedGame;
    private JFrame LoadedGame;

    public static String saveGameInstance(){
        final JFrame save = new JFrame();
        String getSaveMessage = JOptionPane.showInputDialog(save,"Save game as:");
        JOptionPane.showMessageDialog(save,"You saved the game as: "+getSaveMessage);
        return getSaveMessage;
    }

    public static String loadGameInstance(){
        final JFrame load = new JFrame();
        String getLoadMessage = JOptionPane.showInputDialog(load, "State name of game");
        JOptionPane.showMessageDialog(load,"You have loaded the game: "+getLoadMessage);
        return getLoadMessage;
    }


}
