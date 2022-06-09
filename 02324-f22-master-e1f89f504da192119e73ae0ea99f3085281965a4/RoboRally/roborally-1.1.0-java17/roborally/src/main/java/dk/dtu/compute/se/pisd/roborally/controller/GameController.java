/*
 *  This file is part of the initial project provided for the
 *  course "Project in Software Development (02362)" held at
 *  DTU Compute at the Technical University of Denmark.
 *
 *  Copyright (C) 2019, 2020: Ekkart Kindler, ekki@dtu.dk
 *
 *  This software is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation; version 2 of the License.
 *
 *  This project is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this project; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 */
package dk.dtu.compute.se.pisd.roborally.controller;

import dk.dtu.compute.se.pisd.roborally.fileaccess.model.PlayerPositionGenerator;
import dk.dtu.compute.se.pisd.roborally.model.*;
import dk.dtu.compute.se.pisd.roborally.view.PopUpBoxView;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * ...
 *
 * @author Ekkart Kindler, ekki@dtu.dk
 */
public class GameController {

    final public Board board;

    public GameController(@NotNull Board board) {
        this.board = board;
    }

    /**
     * This is just some dummy controller operation to make a simple move to see something
     * happening on the board. This method should eventually be deleted!
     *
     * @param space the space to which the current player should move
     */
    public void moveCurrentPlayerToSpace(@NotNull Space space) {
        if (space.getPlayer() == null) {
            board.getCurrentPlayer().setSpace(space);
            board.setCounter(board.getCounter() + 1);
            for (int i = 0; i < board.getPlayers().size(); i++) {
                if (i == board.getPlayers().size() - 1) {
                    board.setCurrentPlayer(board.getPlayers().get(0));
                    break;
                }
                if (board.getCurrentPlayer().equals(board.getPlayers().get(i))) {
                    board.setCurrentPlayer(board.getPlayers().get(i + 1));
                    break;
                }


            }
        }

        // TODO Assignment V1: method should be implemented by the students:
        //   - the current player should be moved to the given space
        //     (if it is free()
        //   - and the current player should be set to the player
        //     following the current player
        //   - the counter of moves in the game should be increased by one
        //     if the player is moved

    }

    // XXX: V2
    public void startProgrammingPhase() {
        board.setPhase(Phase.PROGRAMMING);
        board.setCurrentPlayer(board.getPlayer(0));
        board.setStep(0);

        for (int i = 0; i < board.getPlayersNumber(); i++) {
            Player player = board.getPlayer(i);
            if (player != null) {
                for (int j = 0; j < Player.NO_REGISTERS; j++) {
                    CommandCardField field = player.getProgramField(j);
                    field.setCard(null);
                    field.setVisible(true);
                }
                for (int j = 0; j < Player.NO_CARDS; j++) {
                    CommandCardField field = player.getCardField(j);
                    field.setCard(generateRandomCommandCard());
                    field.setVisible(true);
                }
            }
        }
    }

    public void startWaitingPhase() throws IOException, InterruptedException, ExecutionException {
        board.setPhase(Phase.WAITINGPLAYERS);

        while(refresh()[0].equals("WaitingForPlayersToConnect"));
        startProgrammingPhase();
    }

    // XXX: V2
    private CommandCard generateRandomCommandCard() {
        Command[] commands = Command.values();
        int random = (int) (Math.random() * commands.length);
        return new CommandCard(commands[random]);
    }


    /**
     * Now we will change this method to make the player closest to the antenna start,
     * Little side note is that we will then set it to the previous player. We are doing it
     * this way because Then we can switch the player in the start of the turn instead of the end
     * This saves us a lot of trouble when dealing with synchronising the logic and the visual aspect,
     * in the player interaction phase.
     */
    // XXX: V2
    public void finishProgrammingPhase() throws IOException, InterruptedException {
        findAntenna();
        if (board.getAntennaSpace() != null) {
            board.setCurrentPlayer(findFirstPLayerToMoveRobot());
        } else {
            board.setCurrentPlayer(board.getPlayer(0));
        }
        new ServerClientController().lockin(board.getMyGameRoomNumber(), board.getMyPlayerNumber(), board.getPlayer(board.getMyPlayerNumber()).getTotalRegisters());

        waitProgrammingPhase();
        board.setPhase(Phase.ACTIVATION);
        board.setStep(0);
    }

    public String[] refresh() {
        try {
            Thread.sleep(333);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        String response = null;
        try {
            response = new ServerClientController().refresh(board.getMyGameRoomNumber(), board.getMyPlayerNumber());
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        String[] responseArr = response.split("-");
        return responseArr;
    }

    public void waitActivation() {
        board.setPhase(Phase.WAITACTIVATION);

        while(refresh()[0].equals("WaitingForOthersToPlayTurn")){
            try {
                new PlayerPositionGenerator().updatePlayersPosition(refresh()[1], board);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if(refresh()[0].equals("WaitingForYouToPlayTurn")){
            try {
                new PlayerPositionGenerator().updatePlayersPosition(refresh()[1], board);
            } catch (Exception e) {
                e.printStackTrace();
            }
            board.setPhase(Phase.ACTIVATION);
        }
        else if(refresh()[0].equals("WaitingForYouToLock")){
            try {
                new PlayerPositionGenerator().updatePlayersPosition(refresh()[1], board);
            } catch (Exception e) {
                e.printStackTrace();
            }

            startProgrammingPhase();
        }
    }

    public void waitProgrammingPhase() {
        board.setPhase(Phase.WAITPROGRAMMING);

        while(refresh()[0].equals("WaitingForOthersToLock"));
    }

    /**
     * I am making a method finding the antenna if it is not there it will return false.
     * This return statement might be used for error handling later...
     *
     * @return If it finds an antenna or not and set the board.antenna to be correct
     */
    public boolean findAntenna() {
        for (int x = 0; x < board.width; x++) {
            for (int y = 0; y < board.height; y++) {
                for (int k = 0; k < board.getSpace(x, y).getActions().size(); k++) {
                    if (board.getSpace(x, y).getActions().get(k).getClass() == Antenna.class) {
                        board.setAntennaSpace(board.getSpace(x, y));
                        return true;
                    }

                }
            }

        }
        return false;
    }

    /**
     * This method needs exeption handling since if the Antenna is null it will be mad.
     * This will be handled elsewhere.
     *
     * @return Returns the first player to move the in the turn
     */
    public Player findFirstPLayerToMoveRobot() {
        int diff = board.width + board.height + 1;
        Player player = null;
        for (int i = 0; i < board.getPlayers().size(); i++) {
            int testDiff = board.calculateDistance(board.getAntennaSpace(), board.getPlayers().get(i).getSpace());
            if (testDiff < diff) {
                player = board.getPlayers().get(i);
                diff = testDiff;

            }

        }
        return player;
    }

    // XXX: V2
    private void makeProgramFieldsVisible(int register) {
        if (register >= 0 && register < Player.NO_REGISTERS) {
            for (int i = 0; i < board.getPlayersNumber(); i++) {
                Player player = board.getPlayer(i);
                CommandCardField field = player.getProgramField(register);
                field.setVisible(true);
            }
        }
    }

    // XXX: V2
    private void makeProgramFieldsInvisible() {
        for (int i = 0; i < board.getPlayersNumber(); i++) {
            Player player = board.getPlayer(i);
            for (int j = 0; j < Player.NO_REGISTERS; j++) {
                CommandCardField field = player.getProgramField(j);
                field.setVisible(false);
            }
        }
    }

    // XXX: V2
    public void turnFinished() {
        try {
            new ServerClientController().playturn(board.getMyGameRoomNumber(), board.getMyPlayerNumber(), new PlayerPositionGenerator().toString(board));
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        try {
            new PlayerPositionGenerator().updatePlayersPosition(refresh()[1], board);
        } catch (Exception e) {
            e.printStackTrace();
        }

        waitActivation();
    }

    // XXX: V2
    public void executeStep() {
        board.setStepMode(true);
        continuePrograms();
    }

    // XXX: V2
    private void continuePrograms() {
        executeNextStep();
        System.out.println(board.getStep());
        board.setStep(board.getStep() + 1);

        if(board.getPhase() != Phase.PLAYER_INTERACTION){
            board.setPhase(Phase.WAITENDTURN);
        }
    }

    // XXX: V2
    private void executeNextStep() {
        Player currentPlayer = board.getPlayer(board.getMyPlayerNumber());
        if (board.getPhase() == Phase.ACTIVATION && currentPlayer != null) {
            if (board.getStep() >= 0 && board.getStep() < Player.NO_REGISTERS) {
                CommandCard card = currentPlayer.getProgramField(board.getStep()).getCard();
                if (card != null) {
                    Command command = card.command;
                    executeCommand(currentPlayer, command);
                }
                activateEOTActions();
                activateEOTCPActions();
            } else {
                // this should not happen
                assert false;
            }
        } else {
            // this should not happen
            assert false;
        }
    }

    // XXX: V2
    private void executeCommand(@NotNull Player player, Command command) {
        if (player.board == board && command != null) {
            // XXX This is a very simplistic way of dealing with some basic cards and
            //     their execution. This should eventually be done in a more elegant way
            //     (this concerns the way cards are modelled as well as the way they are executed).

            switch (command) {
                case FORWARD:
                    this.moveForward(player);
                    break;
                case RIGHT:
                    this.turnRight(player);
                    break;
                case LEFT:
                    this.turnLeft(player);
                    break;
                case FAST_FORWARD:
                    this.fastForward(player);
                    break;
                case OPTION_LEFT_RIGHT:
                    executeCommandOptionAndContinue(new PopUpBoxView().leftOrRight(Arrays.asList("Left", "Right")));
                    break;
                case U_TURN:
                    this.uturn(player);
                    break;
                case MOVE_THREE:
                    this.moveThree(player);
                    break;
                case MOVE_BACK:
                    this.moveBack(player);
                    break;
                default:
                    // DO NOTHING (for now)
            }
        }
    }

    public void moveForward(@NotNull Player player) {
        if (player.board == board) {
            Space space = player.getSpace();
            Heading heading = player.getHeading();

            Space target = board.getNeighbour(space, heading);
            if (target != null) {
                try {
                    moveToSpace(player, target, heading);
                } catch (ImpossibleMoveException e) {
                    // we don't do anything here  for now; we just catch the
                    // exception so that we do not pass it on to the caller
                    // (which would be very bad style).
                }
            }
        }
    }

    /**
     * Method overloading for conveyorBelts that need to move in a specific direction instead of the players heading.
     *
     * @param player  The player that must be moved
     * @param heading The heading the player must be moved
     */
    public void moveForward(@NotNull Player player, Heading heading) {
        if (player.board == board) {
            Space space = player.getSpace();

            Space target = board.getNeighbour(space, heading);
            if (target != null) {
                try {
                    moveToSpace(player, target, heading);
                } catch (ImpossibleMoveException e) {
                    // we don't do anything here  for now; we just catch the
                    // exception so that we do no pass it on to the caller
                    // (which would be very bad style).
                }
            }
        }
    }


    // TODO Assignment V2
    public void fastForward(@NotNull Player player) {
        moveForward(player);
        moveForward(player);
    }

    // TODO Assignment V2
    public void turnRight(@NotNull Player player) {
        player.setHeading(player.getHeading().next());

    }

    // TODO Assignment V2
    public void turnLeft(@NotNull Player player) {
        player.setHeading(player.getHeading().prev());

    }

    /**
     * New method in V3 for turning either left or right
     */
    public void leftOrRight(@NotNull Player player) {
        String[] buttonOptions = {"Turn left", "Turn right", "Left", "Right"};
        board.setButtonOptions(buttonOptions);

        board.setPhase(Phase.PLAYER_INTERACTION);
    }

    /**
     * New method for making an u-turn / facing the opposite direction.
     */
    public void uturn(@NotNull Player player) {
        turnRight(player);
        turnRight(player);

    }

    /**
     * New method for moving three blocks forward.
     */
    public void moveThree(@NotNull Player player) {
        moveForward(player);
        moveForward(player);
        moveForward(player);

    }

    /**
     * New method for moving backwards facing same direction.
     */
    public void moveBack(@NotNull Player player) {
        uturn(player);
        moveForward(player);
        uturn(player);
    }


    /**
     * Method getting called from gameController
     * There are some issues because of the way execute next step is made it changes to next player before you choose
     * direction and therefore the previous player gets chosen.
     * As said this should be fixed later
     * <p>
     * This method now also redirects from other Method to inform when a player has gotten a checkpoint or when a player has won the game
     *
     * @param choice This is where we get after using the gui to get a choice the lambda expression will be called with choice.
     */
    public void executeCommandOptionAndContinue(String choice) {
        board.setPhase(Phase.ACTIVATION);
        if (choice.equals("Left")) {
            turnLeft(board.getCurrentPlayer());
            board.setPhase(Phase.WAITENDTURN);
        } else if (choice.equals("Right")) {
            turnRight(board.getCurrentPlayer());
            board.setPhase(Phase.WAITENDTURN);
        } else if (choice.equals("OK")) {
        } else if (choice.equals("Cool")) {
        } else if (choice.equals("WOption continue")) {
        } else if (choice.equals("WOption endgame")) {

        }


        this.continuePrograms();

    }


    public boolean moveCards(@NotNull CommandCardField source, @NotNull CommandCardField target) {
        CommandCard sourceCard = source.getCard();
        CommandCard targetCard = target.getCard();
        if (sourceCard != null && targetCard == null) {
            target.setCard(sourceCard);
            source.setCard(null);
            return true;
        } else {
            return false;
        }
    }

    /**
     * A method called when no corresponding controller operation is implemented yet. This
     * should eventually be removed.
     */
    public void notImplemented() {
        // XXX just for now to indicate that the actual method is not yet implemented
        assert false;
    }

    class ImpossibleMoveException extends Exception {

        private Player player;
        private Space space;
        private Heading heading;

        public ImpossibleMoveException(Player player, Space space, Heading heading) {
            super("Move impossible");
            this.player = player;
            this.space = space;
            this.heading = heading;
        }
    }

    void moveToSpace(@NotNull Player player, @NotNull Space space, @NotNull Heading heading) throws ImpossibleMoveException {
        assert board.getNeighbour(player.getSpace(), heading) == space; // make sure the move to here is possible in principle
        Player other = space.getPlayer();
        if (other != null) {
            Space target = board.getNeighbour(space, heading);
            if (target != null) {
                // XXX Note that there might be additional problems with
                //     infinite recursion here (in some special cases)!
                //     We will come back to that!
                moveToSpace(other, target, heading);

                // Note that we do NOT embed the above statement in a try catch block, since
                // the thrown exception is supposed to be passed on to the caller

                assert target.getPlayer() == null : target; // make sure target is free now
            } else {
                throw new ImpossibleMoveException(player, space, heading);
            }
        }
        player.setSpace(space);
    }

    /**
     * Doing all the fields, If a player is on the field.
     * Changed from doing all field action to avoid a certain problem with conveyerbelts pointing south or east.
     * Because of how the fields where looped through
     */
    private void activateEOTActions() {
        for (int i = 0; i < board.getPlayers().size(); i++) {
            for (int j = 0; j < board.getPlayers().get(i).getSpace().getActions().size(); j++) {
                board.getPlayers().get(i).getSpace().getActions().get(j).doAction(this, board.getPlayers().get(i).getSpace());

            }

        }
    }

    /**
     * Doing only the checkpoint actions which should also be done after the conveyor belts and stuff.
     */
    private void activateEOTCPActions() {
        for (int i = 0; i < board.width; i++) {
            for (int j = 0; j < board.height; j++) {
                board.getSpace(i, j).doCPActions(this);

            }

        }


    }

    public void wonGame() {
    }

}



