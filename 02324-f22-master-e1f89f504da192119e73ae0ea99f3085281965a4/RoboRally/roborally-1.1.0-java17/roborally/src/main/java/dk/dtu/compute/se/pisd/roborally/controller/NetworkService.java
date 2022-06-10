package dk.dtu.compute.se.pisd.roborally.controller;

import dk.dtu.compute.se.pisd.roborally.model.Board;

import java.io.IOException;

public class NetworkService implements Runnable{

    private int gameRoomNumber;
    private int playerNumber;
    private String recieveMessage;

    private GameController gameController;

    public NetworkService(GameController gameController, int gameRoomNumber, int playerNumber, String recieveMessage){
        this.gameController = gameController;
        this.gameRoomNumber = gameRoomNumber;
        this.playerNumber = playerNumber;
        this.recieveMessage = recieveMessage;
    }

    @Override
    public void run() {

        while(true){
            String response = "";
            try {
                response = new ServerClientController().refresh(gameRoomNumber, playerNumber);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            String[] responseArr = response.split("-");

            if(!responseArr[0].equals(recieveMessage)){
                gameController.networkMsg.setValue(response);
                break;
            }
        }
    }
}
