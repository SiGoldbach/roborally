package dk.dtu.compute.se.pisd.roborally.controller;

import dk.dtu.compute.se.pisd.roborally.model.Space;

public class GearSpace extends FieldAction{
    private final boolean direction;

    public GearSpace(boolean direction) {
        this.direction = direction;
    }


    @Override
    public boolean doAction(GameController gameController, Space space) {
        if(space.getPlayer()==null) {
            return false;
        }
        if(direction==false){
            space.getPlayer().setHeading(space.getPlayer().getHeading().next());
        }else{
            space.getPlayer().setHeading(space.getPlayer().getHeading().prev());
        }
        return true;
    }
}
