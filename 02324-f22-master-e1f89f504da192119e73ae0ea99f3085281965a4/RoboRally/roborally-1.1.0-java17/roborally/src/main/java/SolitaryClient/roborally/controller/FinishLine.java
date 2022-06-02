package SolitaryClient.roborally.controller;

import SolitaryClient.roborally.model.Phase;
import SolitaryClient.roborally.model.Space;

public class FinishLine extends FieldAction {
    /**
     * Here is a method for determining if the player on the victory fields has all checkpoints
     * if he has he should win. It does not do anything yet since it needs some method to call but there is not any.
     *
     * Here there is a message about that a player has won for now nothing happens but maybe later theese options could be used.
     * @param gameController the gameController of the respective game
     * @param space          the space this action should be executed for
     * @return True or false Whether an action was done or not
     */
    public boolean doAction(GameController gameController, Space space) {
        if (space.getPlayer() == null)
            return false;
        if (gameController.board.getCheckpointAmount() == space.getPlayer().getCheckpointNumber()) {
            space.getPlayer().setCheckpointNumber(space.getPlayer().getCheckpointNumber()+1);
            System.out.println("Doing some winning");
            String[] buttonOptions={space.getPlayer().getColor()+" Has won the game: continue?","End game","WOption continue","WOption endgame"};
            gameController.board.setButtonOptions(buttonOptions);
            gameController.board.setPhase(Phase.PLAYER_INTERACTION);
        }
        return true;

    }


}
