package Server;

import java.net.Socket;
import java.security.Key;

public class ServerColumn {
    private int userID;
    private int roomID;
    private Socket userSocket;

    public ServerColumn(int userID, int roomID, Socket userSocket){
        this.userID = userID;
        this.roomID = roomID;
        this.userSocket = userSocket;
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