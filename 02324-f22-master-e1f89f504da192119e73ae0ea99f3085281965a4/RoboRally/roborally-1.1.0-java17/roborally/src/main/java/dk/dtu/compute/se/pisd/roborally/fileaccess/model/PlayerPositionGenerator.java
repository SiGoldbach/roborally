package dk.dtu.compute.se.pisd.roborally.fileaccess.model;

import dk.dtu.compute.se.pisd.roborally.model.Board;
import dk.dtu.compute.se.pisd.roborally.model.Player;

/**
 * Class for putting players position into files and getting them of files again.
 */
public class PlayerPositionGenerator {
    public String toString(Board board){
        StringBuilder PlayerPositions= new StringBuilder();
        for (int i = 0; i < board.getPlayers().size(); i++) {
            Player p=board.getPlayers().get(i);
            String temp=(p.getColor() + "," + p.getHeading() +","+ p.getSpace().x + "," + p.getSpace().y+",-");
            PlayerPositions.append(temp);


        }




        return PlayerPositions.toString();

    }


}
