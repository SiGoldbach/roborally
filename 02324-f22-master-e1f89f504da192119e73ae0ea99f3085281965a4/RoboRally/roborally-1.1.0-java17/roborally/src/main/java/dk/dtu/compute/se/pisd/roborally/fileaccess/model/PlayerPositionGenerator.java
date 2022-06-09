package dk.dtu.compute.se.pisd.roborally.fileaccess.model;

import dk.dtu.compute.se.pisd.roborally.model.Board;
import dk.dtu.compute.se.pisd.roborally.model.Heading;
import dk.dtu.compute.se.pisd.roborally.model.Player;

import java.util.Arrays;

/**
 * Class for putting players position into files and getting them of files again.
 */
public class PlayerPositionGenerator {
    public String toString(Board board){
        StringBuilder PlayerPositions= new StringBuilder();
        for (int i = 0; i < board.getPlayers().size(); i++) {
            Player p=board.getPlayers().get(i);
            String temp=(p.getColor() + "," + p.getHeading() +","+ p.getSpace().x + "," + p.getSpace().y+",/");
            PlayerPositions.append(temp);
        }
        return PlayerPositions.toString();
    }

    public void updatePlayersPosition(String position, Board board) throws Exception {
        String[] eachIndividualPlayer = position.split("/");
        System.out.println(Arrays.toString(eachIndividualPlayer) + " Length: " + eachIndividualPlayer.length);
        if (board.getPlayers().size() != eachIndividualPlayer.length)
            throw new BoardAnPositionOutOfSyncException(board.getPlayers().size(), eachIndividualPlayer.length);

        for (int i = 0; i < eachIndividualPlayer.length; i++) {
            String[] playerAR = eachIndividualPlayer[i].split(",");
            Player p = board.getPlayers().get(i);
            p.setSpace(board.getSpace((Integer.parseInt(playerAR[2])), Integer.parseInt(playerAR[3])));
            switch (playerAR[1]) {
                case "NORTH":
                    p.setHeading(Heading.NORTH);
                    break;
                case "WEST":
                    p.setHeading(Heading.WEST);
                    break;
                case "SOUTH":
                    p.setHeading(Heading.SOUTH);
                    break;
                case "EAST":
                    p.setHeading(Heading.EAST);
                    break;
            }
        }
    }

    static class BoardAnPositionOutOfSyncException extends Exception {
        public BoardAnPositionOutOfSyncException(int playersOnBoard, int playersOnServer) {
            System.out.println("Players on the board is: " + playersOnBoard + " Players on the server is: " + playersOnServer);

        }
    }
}
