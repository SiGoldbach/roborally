package dk.dtu.compute.se.pisd.roborally.controller;

import dk.dtu.compute.se.pisd.roborally.model.Space;

public class Antenna extends FieldAction{
    public Antenna(){

    }

    /**
     * We have made the antenna as a field action and then in board we will,
     * Define which one is an antenna. This solution is preferred for us since we do not.
     * Want to keep information about whether there is an antenna or not on every field.
     * For now the first Antenna will be counted as the one the players move from.
     * @param gameController the gameController of the respective game
     * @param space the space this action should be executed for
     * @return Just true nothing that matters
     */

    @Override
    public boolean doAction(GameController gameController, Space space) {
        return true;
    }
}
