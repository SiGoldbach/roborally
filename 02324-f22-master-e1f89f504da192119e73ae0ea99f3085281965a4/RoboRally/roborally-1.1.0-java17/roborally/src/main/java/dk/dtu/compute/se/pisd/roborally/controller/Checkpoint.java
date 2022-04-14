package dk.dtu.compute.se.pisd.roborally.controller;

import dk.dtu.compute.se.pisd.roborally.model.Space;

public class Checkpoint extends FieldAction{
    private final int checkPointsRequired;
    public Checkpoint(int checkPointsRequired){
        this.checkPointsRequired=checkPointsRequired;

    }


    public  boolean doAction(GameController gameController, Space space){
        if(space.getPlayer()==null)
            return false;
        if(space.getPlayer().getCheckpointNumber()==checkPointsRequired){
            space.getPlayer().setCheckpointNumber(space.getPlayer().getCheckpointNumber()+1);
        }

        return true;
    }
}
