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

import dk.dtu.compute.se.pisd.designpatterns.observer.Observer;
import dk.dtu.compute.se.pisd.designpatterns.observer.Subject;

import dk.dtu.compute.se.pisd.roborally.RoboRally;

import dk.dtu.compute.se.pisd.roborally.fileaccess.LoadBoard;
import dk.dtu.compute.se.pisd.roborally.model.Board;
import dk.dtu.compute.se.pisd.roborally.model.Phase;
import dk.dtu.compute.se.pisd.roborally.model.Player;

import dk.dtu.compute.se.pisd.roborally.view.PopUpBoxView;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ChoiceDialog;
import org.apache.catalina.Server;
import org.jetbrains.annotations.NotNull;

import javax.imageio.IIOException;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutionException;

/**
 * ...
 *
 * @author Ekkart Kindler, ekki@dtu.dk
 */
public class AppController extends PopUpBoxView implements Observer {

    final private List<Integer> PLAYER_NUMBER_OPTIONS = Arrays.asList(2, 3, 4, 5, 6);
    final private List<String> PLAYER_COLORS = Arrays.asList("red", "green", "blue", "orange", "grey", "magenta");

    final private RoboRally roboRally;

    private GameController gameController;

    public AppController(@NotNull RoboRally roboRally) {
        this.roboRally = roboRally;
    }

    public void connectToGame() throws IOException, InterruptedException {
        String username = new PopUpBoxView().gameInstance("Enter username", "Confirm ");
        String gamename = new PopUpBoxView().gameInstance("Enter gamename", "Confirm ");

        String response = new ServerClientController().connectToGame(gamename, username);

        String[] responseArr = response.split("-");

        String boardJson = responseArr[3];
        System.out.println("JSON " + boardJson);
        gameController = new GameController(LoadBoard.loadBoard(boardJson));

        int totalPlayers = Integer.parseInt(responseArr[2]);
            for (int i = 0; i < totalPlayers; i++) {
                Player player = new Player(gameController.board, PLAYER_COLORS.get(i), "Player " + (i + 1));
                gameController.board.addPlayer(player);
                player.setSpace(gameController.board.getSpace(i % gameController.board.width, i));
            }


        gameController.board.setMyGameRoomNumber(Integer.parseInt(responseArr[0]));
        gameController.board.setMyPlayerNumber(Integer.parseInt(responseArr[1]) - 1);

        gameController.startProgrammingPhase();

        roboRally.createBoardView(gameController);

        try {
            gameController.startWaitingPhase();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }

    public void saveGame() {
        String name = new PopUpBoxView().gameInstance("Save game as: ", "Game saved as:");
        String toServer = LoadBoard.saveBoard(gameController.board, name);
        try {


            new ServerClientController().saveBoard(toServer, name);
        } catch (InterruptedException e) {
            System.out.println("Problems with server connection");// XXX needs to be implemented eventually
        } catch (IOException e) {
            System.out.println("Problems with IOE");

        }
    }

    /**
     * This method needs to be flexible, since it needs to be able to load a template to play on.
     * But also an ongoing game the players did not finish last time therefore there will be a check.
     * Whether the players have been instantiated or not. If they have not the popup box will appear and ask,
     * and set the game to programming phase but. But if there are not the game should just continue.
     */
    public void hostGame() throws IOException, InterruptedException {

        //gameController = new GameController(LoadBoard.loadBoard(new PopUpBoxView().gameInstance("Load game", "Game loaded")));
        String chosenBoard = new PopUpBoxView().loadGame(new ServerClientController().possibleBoards());
        String boardJson = new ServerClientController().getBoard(chosenBoard);
        gameController = new GameController(LoadBoard.loadBoard(boardJson));

        System.out.println(gameController.board.getPlayers().size());
        if (gameController.board.getPlayers().size() == 0) {
            ChoiceDialog<Integer> dialog = new ChoiceDialog<>(PLAYER_NUMBER_OPTIONS.get(0), PLAYER_NUMBER_OPTIONS);
            dialog.setTitle("Player number");
            dialog.setHeaderText("Select number of players");
            Optional<Integer> result = dialog.showAndWait();
            int no = result.get();
            for (int i = 0; i < no; i++) {
                Player player = new Player(gameController.board, PLAYER_COLORS.get(i), "Player " + (i + 1));
                gameController.board.addPlayer(player);
                player.setSpace(gameController.board.getSpace(i % gameController.board.width, i));
            }

            String username = new PopUpBoxView().gameInstance("Enter username", "Confirm ");
            String gamename = new PopUpBoxView().gameInstance("Enter gamename", "Confirm ");

            String response = new ServerClientController().hostGame(gamename, username, boardJson, gameController.board.getPlayers().size());

            String[] responseArr = response.split("-");

            gameController.board.setMyGameRoomNumber(Integer.parseInt(responseArr[0]));
            gameController.board.setMyPlayerNumber(Integer.parseInt(responseArr[1]) - 1);

            // XXX: V2
            // board.setCurrentPlayer(board.getPlayer(0));
            gameController.startProgrammingPhase();
        }

        switch (gameController.board.getPhase()) {
            case ACTIVATION -> gameController.finishProgrammingPhase();
            case PROGRAMMING, INITIALISATION -> gameController.startProgrammingPhase();
        }

        roboRally.createBoardView(gameController);
        // XXX needs to be implememted eventually
        // for now, we just create a new game

        try {
            gameController.startWaitingPhase();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }

    /**
     * Stop playing the current game, giving the user the option to save
     * the game or to cancel stopping the game. The method returns true
     * if the game was successfully stopped (with or without saving the
     * game); returns false, if the current game was not stopped. In case
     * there is no current game, false is returned.
     *
     * @return true if the current game was stopped, false otherwise
     */
    public boolean stopGame() {
        if (gameController != null) {

            // here we save the game (without asking the user).
            saveGame();

            gameController = null;
            roboRally.createBoardView(null);
            return true;
        }
        return false;
    }

    public void exit() {
        if (gameController != null) {
            Alert alert = new Alert(AlertType.CONFIRMATION);
            alert.setTitle("Exit RoboRally?");
            alert.setContentText("Are you sure you want to exit RoboRally?");
            Optional<ButtonType> result = alert.showAndWait();

            if (!result.isPresent() || result.get() != ButtonType.OK) {
                return; // return without exiting the application
            }
        }

        // If the user did not cancel, the RoboRally application will exit
        // after the option to save the game
        if (gameController == null || stopGame()) {
            Platform.exit();
        }
    }

    public boolean isGameRunning() {
        return gameController != null;
    }


    @Override
    public void update(Subject subject) {
        // XXX do nothing for now
    }

}
