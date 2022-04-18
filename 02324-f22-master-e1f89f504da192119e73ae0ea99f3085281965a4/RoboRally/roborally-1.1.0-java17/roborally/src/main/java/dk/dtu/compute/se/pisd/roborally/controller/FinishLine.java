package dk.dtu.compute.se.pisd.roborally.controller;

import dk.dtu.compute.se.pisd.roborally.model.Space;

public class FinishLine extends FieldAction {
    /**
     * Here is a method for determining if the player on the victory fields has all checkpoints
     * if he has he should win. It does not do anything yet since it needs some method to call but there is not any.
     *
     * @param gameController the gameController of the respective game
     * @param space          the space this action should be executed for
     * @return
     */
    public boolean doAction(GameController gameController, Space space) {
        if (space.getPlayer() == null)
            return false;
        if (gameController.board.getCheckpointAmount() == space.getPlayer().getCheckpointNumber()) {
            System.out.println("Doing some winning");
            gameController.winnerField(space.getPlayer());
        }
        return true;

    }


}
