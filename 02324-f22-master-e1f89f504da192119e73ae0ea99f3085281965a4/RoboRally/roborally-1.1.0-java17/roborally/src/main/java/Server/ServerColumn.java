package Server;

import java.net.Socket;
import java.security.Key;

public class ServerColumn {
    private int userID;
    private int roomID;
    private Socket userSocket;

    private Key clientPubKey;

    public ServerColumn(int userID, int roomID, Socket userSocket){
        this.userID = userID;
        this.roomID = roomID;
        this.userSocket = userSocket;
    }

    public void keySet(Key myPubKey){
        clientPubKey = myPubKey;
    }

    public Key keyGet(){
        return clientPubKey;
    }

    public int userIDGet(){
        return userID;
    }

    public int roomIDGet(){
        return roomID;
    }

    public void roomIDSet(int setRID){
        roomID = setRID;
    }

    public Socket socketGet(){
        return userSocket;
    }
}