package dk.dtu.compute.se.pisd.roborally.view;
import javax.swing.*;



public class PopUpBoxView {
    public  String gameInstance(String inputDialogue,String messageDialogue){
        final JFrame save = new JFrame();
        String getSaveMessage = JOptionPane.showInputDialog(save,inputDialogue);
        JOptionPane.showMessageDialog(save,messageDialogue+""+getSaveMessage);
        return getSaveMessage;
    }



}
