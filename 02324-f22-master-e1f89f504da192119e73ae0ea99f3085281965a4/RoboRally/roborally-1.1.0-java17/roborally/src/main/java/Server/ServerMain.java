package Server;

import java.io.IOException;
import java.net.ServerSocket;

public class ServerMain {

    private List<ServerClientThread> clientList;

    private int sPort;

    public ServerMain(int portNumber){
        this.sPort = portNumber;
    }

    public static void main(String[] args) {
        int portNumber = 64510;
        ServerMain server = new ServerMain(portNumber);

        ServerSocket hostSocket = server.startServer();
        ServerPacketThread myPacketThread = server.startPackets();

        server.acceptConnections(hostSocket, myPacketThread);
    }

    private ServerSocket startServer(){
        clientList = new ArrayList<ServerClientThread>(); // List keeps track of client threads

        ServerSocket hostSocket = null;
        try {
            hostSocket = new ServerSocket(sPort);
            System.out.println("Server started on: " + hostSocket.getLocalSocketAddress()); // Prints server address
        } catch (IOException e) {
            System.err.println("Failed to listen on port: " + sPort);
            System.exit(1);
        }

        return hostSocket;
    }

    private ServerPacketThread startPackets(){
        ServerPacketThread myPacketThread = new ServerPacketThread(this);
        Thread thread = new Thread(myPacketThread);
        thread.start();

        return myPacketThread;
    }

    private void acceptConnections(ServerSocket hostSocket, ServerPacketThread myPacketThread){
        System.out.println("Now accepting connections");
        while(true){
            try{
                Socket uSocket = hostSocket.accept();
                System.out.println("New connection: " + uSocket.getRemoteSocketAddress()); // Prints new connections address
                ServerClientThread newClient = new ServerClientThread(this, uSocket, myPacketThread);
                Thread thread = new Thread(newClient);
                thread.start();
                clientList.add(newClient);
            } catch (IOException e){
                System.out.println("Failed accepting client on");
            }
        }
    }

    public List<ServerClientThread> getClients(){
        return clientList;
    }
}
