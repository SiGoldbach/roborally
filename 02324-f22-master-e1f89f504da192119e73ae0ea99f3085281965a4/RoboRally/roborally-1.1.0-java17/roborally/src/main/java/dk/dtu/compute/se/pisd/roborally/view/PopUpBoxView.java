package dk.dtu.compute.se.pisd.roborally.view;
import javax.swing.*;



public class PopUpBoxView {
    public  String saveGameInstance(String inputDialogue,String messageDialogue){
        final JFrame save = new JFrame();
        String getSaveMessage = JOptionPane.showInputDialog(save,inputDialogue);
        JOptionPane.showMessageDialog(save,messageDialogue+""+getSaveMessage);
        return getSaveMessage;
    }

    public  String loadGameInstance(){
        final JFrame load = new JFrame();
        String getLoadMessage = JOptionPane.showInputDialog(load, "State name of game");
        JOptionPane.showMessageDialog(load,"You have loaded the game: "+getLoadMessage);
        return getLoadMessage;
    }


}
