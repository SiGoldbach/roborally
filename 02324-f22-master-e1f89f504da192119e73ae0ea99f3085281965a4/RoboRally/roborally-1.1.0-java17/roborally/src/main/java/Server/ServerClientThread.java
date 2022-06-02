package Server;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class ServerClientThread implements Runnable{

    private ServerMain server;
    private Socket cSocket;
    private ServerPacketThread packetThread;

    private int count;

    private int userID;

    private PrintWriter out;

    public ServerClientThread(ServerMain server, Socket newCSocket, ServerPacketThread packetThread){
        this.server = server;
        this.cSocket = newCSocket;
        this.packetThread = packetThread;
        count = 0;

        userID = 0;
    }

    @Override // Overrides parent class
    public void run(){ // Needed for implements runnable
        userID = packetThread.assignID(cSocket);

        try {
            Scanner in = new Scanner(cSocket.getInputStream());
            this.out = new PrintWriter(cSocket.getOutputStream(), true);

            out.write("USERID-" + userID + "\r\n");
            out.flush();

            // If there's been no new packet for 400 seconds, it terminates the connection.
            // Doesnt work properly
            while(!cSocket.isClosed()){ // While socket still open
                if(in.hasNextLine()){
                    String input = in.nextLine();
                    packetThread.addPacket(userID + "-" + input);
                    System.out.println(input + " | FROM: " + cSocket + " | USERID: " + userID);
                }
                Thread.sleep(250);
            }

            // Tells the server the client is gone, so it can be deleted.
            if(cSocket.isClosed()){
                packetThread.addPacket(userID + "-EXIT-aaa");
                System.out.println("Deleting client | " + userID);
            }

        } catch (IOException | InterruptedException e){
            System.err.println("Server client thread error on " + cSocket);
        }
    }

    public PrintWriter getWriter(){
        return out;
    }
}