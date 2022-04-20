package dk.dtu.compute.se.pisd.roborally.controller;

import dk.dtu.compute.se.pisd.roborally.model.Phase;
import dk.dtu.compute.se.pisd.roborally.model.Space;

public class Checkpoint extends FieldAction{
    private final int checkPointsRequired;

    /**
     * This constructor creates this action the checkpoints have to be ordered
     * @param checkPointsRequired
     */
    public Checkpoint(int checkPointsRequired){
        this.checkPointsRequired=checkPointsRequired;

    }

    /**
     * Here it increments the players checkpoint amount to control if the player can get the checkpoint yet
     *
     * Here the player gets information when he receaves the checkpoint.
     * @param gameController the gameController of the respective game
     * @param space the space this action should be executed for
     * @return
     */
    public  boolean doAction(GameController gameController, Space space){
        if(space.getPlayer()==null)
            return false;
        if(space.getPlayer().getCheckpointNumber()==checkPointsRequired){
            space.getPlayer().setCheckpointNumber(space.getPlayer().getCheckpointNumber()+1);
            String[] buttonOptions={space.getPlayer().getColor()+"Has reached checkpoint: "+space.getPlayer().getCheckpointNumber(),"Cool","Ok","Cool"};
            gameController.board.setButtonOptions(buttonOptions);
            gameController.board.setPhase(Phase.PLAYER_INTERACTION);
        }

        return true;
    }
}
