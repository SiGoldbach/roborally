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

    public String sliderChoice(String message1, String message2, List<String> listOptions){
        String[] options = new String[listOptions.size()];
        listOptions.toArray(options);
        String getGame = (String) JOptionPane.showInputDialog(
                null,
                message1,
                message2,
                JOptionPane.QUESTION_MESSAGE,
                null,
                options,
                options[options.length-1]);
        return getGame;
    }

}
