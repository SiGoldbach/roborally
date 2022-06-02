package Server;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.*;

public class ServerPacketThread implements Runnable {

    private ServerMain server;
    private final LinkedList<String> packetList;
    private boolean hasPackets = false;

    private PrintWriter out;

    private int userCount;
    private int currentID;
    private List<ServerColumn> clientColumn = new ArrayList<ServerColumn>();

    private int roomCount;
    private int currentRoomID;
    private List<ServerRooms> roomColumn = new ArrayList<ServerRooms>();

    public ServerPacketThread(ServerMain server){
        this.server = server;
        packetList = new LinkedList<String>();

        currentID = 0;
        currentRoomID = 0;

        userCount = 0;
        roomCount = 0;
    }

    public void addPacket(String packetIn){
        packetList.push(packetIn);
        hasPackets = true;

        run();
    }

    public int assignID(Socket uSocket){
        currentID++;
        userCount++;
        clientColumn.add(new ServerColumn(currentID, 0, uSocket));

        return currentID;
    }

    private String createRoom(ServerColumn host, int hostID, int hostRID){
        String response = "";
        if(hostRID == 0){
            currentRoomID++;
            roomCount++;
            host.roomIDSet(currentRoomID);
            roomColumn.add(new ServerRooms(currentRoomID, new ServerGame(8, hostID), host));

            response = "CREATED-" + currentRoomID;
        }
        else{
            response = "DISPLAY-You are in a room";
        }

        return response;
    }

    private String moveVerify(int user, int room, String move){
        ServerRooms gameRoom = getRoom(room);
        String moveVerification = 0 + "-" + user + "-" + room + "-" + "DISPLAY-NOT IN A ROOM";

        if(gameRoom != null){
            ServerGame game = gameRoom.gameGet();
            moveVerification = game.moveChecker(user, room, move);
        }

        return moveVerification;
    }

    private ServerRooms getRoom(int room){
        int verifyRID = 0;
        ServerRooms currentRoom = null;
        int a = 0;
        while (a < roomCount) {
            currentRoom = roomColumn.get(a);
            if(currentRoom.roomIDGet() == room){
                verifyRID = room;
                break;
            }
            a++;
        }

        if(verifyRID > 0){
            return currentRoom;
        }
        else{
            // Could not verify the room. E.g it doesn't exist
            // Returns null
            return null;
        }
    }

    private String joinRoom(int room, ServerColumn joiner, int joinerID){
        ServerRooms currentRoom = getRoom(room);
        String joinResult = "";

        if(joiner.roomIDGet() == 0){
            if(currentRoom != null){
                if(currentRoom.roomFullGet() < 2){
                    currentRoom.addUser(joiner, joinerID);

                    // Sets the joiners new room
                    joiner.roomIDSet(room);
                    joinResult = "JOINED-" + joinerID; // Returns true indicating success.
                }
                else{
                    joinResult = "DISPLAY-Room is full";
                }
            }
            else{
                // Could not verify the room. E.g it doesn't exist
                joinResult = "DISPLAY-Room doesn't exist";
            }
        }
        else{
            // Already in a room, so cant join another one.
            joinResult = "DISPLAY-You are in a room";
        }

        return joinResult;
    }

    private String userResign(int room, int resignerID){
        ServerRooms resignRoom = getRoom(room);
        String resignStatus = 0 + "-" + resignerID + "-" + room + "-" + "DISPLAY-FAILED";

        if(resignRoom != null){
            ServerGame resignGame = resignRoom.gameGet();
            resignGame.resign(resignerID);
            resignStatus = 1 + "-" + resignerID + "-" + room + "-" + "RESIGN-" + resignerID;
        }

        return resignStatus;
    }

    private String startGame(int room, int userID){
        ServerRooms currentRoom = getRoom(room);
        String gameState = 0 + "-" + userID + "-" + room + "-" + "DISPLAY-FAILED TO START";

        if(currentRoom != null){
            ServerGame game = currentRoom.gameGet();

            if(game.newGameStart()){ // If succesfull start, return gamestate.
                gameState = 1 + "-" + userID + "-" + room + "-" + "STARTGAME-" + game.turnNow();
            }
            else{ // Else wait for other player.
                gameState = 0 + "-" + userID + "-" + room + "-" + "DISPLAY-WAIT FOR PLAYER";
            }
        }
        // If if doesn't run, could not verify the room. E.g it doesn't exist

        return gameState;
    }

    private String refreshRoomList(){
        String rooms = "ROOMS-";

        Iterator i = roomColumn.iterator();
        while(i.hasNext()){
            ServerRooms iRoom = (ServerRooms) i.next();
            int iRoomID = iRoom.roomIDGet();
            int iRoomCount = iRoom.roomFullGet();

            if(rooms != "ROOMS-"){
                rooms += "," + iRoomID + "/" + iRoomCount;
            }
            else{
                rooms += iRoomID + "/" + iRoomCount;
            }
        }
        return rooms;
    }

    // Leaves the room, removing all data from room and game.
    private String leaveRoom(int verifyUID, int verifyRID){
        String leaveResult = null;

        ServerRooms leaveRoom = getRoom(verifyRID);
        if(leaveRoom != null){
            leaveRoom.removeUser(verifyUID);
            leaveResult = 2 + "-" + verifyUID + "-" + verifyRID + "-" + "LEAVE-" + verifyUID;

            if(leaveRoom.roomFullGet() == 0){
                // Pop the room from the list, deleting it.
                roomColumn.remove(leaveRoom); // Untested.
                roomCount--;
                leaveResult = null;
            }
        }

        return leaveResult;
    }

    // Leaves the server, removing all data from the server.
    private void exitServer(ServerColumn user, int verifyUID, int verifyRID){
        clientColumn.remove(user);
        userCount--;
    }

    public boolean getRun(int runUID){
        // Method to check if user is still on server.
        // If he isn't, stops running the thread.

        int a = 0;
        boolean returnResult = false;
        ServerColumn currentUser = null;
        while (a < userCount) {
            currentUser = clientColumn.get(a);
            if(currentUser.userIDGet() == runUID){
                returnResult = true;
                break;
            }
            a++;
        }

        return returnResult;
    }

    // Gets the user.
    private ServerColumn userGet(int verifyUID){
        ServerColumn currentUser = null;
        int a = 0;
        while (a < userCount) {
            currentUser = clientColumn.get(a);
            if(currentUser.userIDGet() == verifyUID){
                verifyUID = verifyUID;
                break;
            }
            a++;
        }
        return currentUser;
    }

    private String packetVerification(String packet){
        String countPacket = packet;
        int count = countPacket.length() - countPacket.replace("-", "").length();

        if(count >= 2){
            String[] temp = packet.split("-", 3);

            int currentUID = TryParse.Parse(temp[0]);
            String packetCmnd = temp[1];
            String packetMsg = temp[2];

            // Gets the current user column so we can use it later if we need to.
            int verifyUID = 0;
            int verifyRID = 0;
            ServerColumn currentUser = null;
            int a = 0;
            while (a < userCount) {
                currentUser = clientColumn.get(a);
                if(currentUser.userIDGet() == currentUID){
                    verifyUID = currentUID;
                    verifyRID = currentUser.roomIDGet();
                    break;
                }
                a++;
            }

            String result = null;

            if(verifyUID > 0){
                switch(packetCmnd){
                    case "KEY":
                        //currentUser.keySet(Base64.getDecoder().decode(packetMsg));
                        //result = null;
                        break;
                    case "REFRESH":
                        // Send list of all rooms to user.
                        result = 0 + "-" + verifyUID + "-" + verifyRID + "-" + refreshRoomList();
                        break;
                    case "CREATE":
                        // Create a room and generate room ID
                        result = 0 + "-" + verifyUID + "-" + verifyRID + "-" + createRoom(currentUser, verifyUID, verifyRID);
                        break;
                    case "JOIN":
                        // Join the room ID if possible
                        result = 1 + "-" + verifyUID + "-" + TryParse.Parse(packetMsg) + "-" + joinRoom(TryParse.Parse(packetMsg), currentUser, verifyUID);
                        break;
                    case "START":
                        // Start the game in the room, if one start received, maybe wait for 2? Undecided
                        result = startGame(verifyRID, verifyRID);
                        break;
                    case "MSG":
                        // Send msg to opponent
                        result = 2 + "-" + verifyUID + "-" + verifyRID + "-" + "MSG-" +packetMsg;
                        System.out.println(" MSG sent in game room" + verifyRID);
                        break;
                    case "MOVE":
                        //Check userID if it's their turn, also check if valid move
                        result = moveVerify(verifyUID, verifyRID, packetMsg);
                        System.out.println(result);
                        break;
                    case "RESIGN":
                        // End game
                        result = userResign(verifyRID, verifyUID);
                        break;
                    case "LEAVER":
                        // Leave the room
                        result = leaveRoom(verifyUID, verifyRID);
                        currentUser.roomIDSet(0);
                        break;
                    case "EXIT":
                        // Leave the server, all user data is removed.
                        result = leaveRoom(verifyUID, verifyRID);
                        exitServer(currentUser, verifyUID, verifyRID);
                        break;
                    default:
                        System.out.println("Unrecognized packet from USERID#" + verifyUID);
                        break;
                }
            }
            else{
                System.out.println("Could not verify userID " + currentUID);
            }
            // Debug purposes only

            return result;
        }
        else{
            // ServerClientThread can send the socket information if necessary.
            // Not included for now.
            System.out.println("Extremely unrecognized packet");
            return null;
        }
    }

    @Override // Overrides parent class
    public void run() { // Needed for implements runnable

        while(hasPackets){
            hasPackets = false;

            String nextPacket = packetList.pop();
            System.out.println(nextPacket);

            String verify = packetVerification(nextPacket);
            if(verify != null){
                String[] packetSplit = verify.split("-", 4);
                int code = TryParse.Parse(packetSplit[0]);
                int senderUID = TryParse.Parse(packetSplit[1]);
                int senderRID = TryParse.Parse(packetSplit[2]);
                String packetSnd = packetSplit[3];

                // Switch on code, who to send data to.
                // 0, user only
                // 1, both people in room
                // 2, opponent only
                // 3, everyone. Unused.
                ServerColumn oppUser = null;
                ServerColumn myUser = null;
                ServerRooms myRoom = null;

                Socket myUserSock = null;
                Socket oppUserSock = null;

                myUser = userGet(senderUID);
                switch(code){
                    case 0: // Sends to user only.
                        myUserSock = myUser.socketGet();
                        break;
                    case 1: // Sends to both in room.
                        myUserSock = myUser.socketGet();

                        if(senderRID > 0){
                            if(myUser.roomIDGet() == senderRID){
                                // Gets the other user in the room.
                                myRoom = getRoom(senderRID);
                                oppUser = myRoom.otherUserGet(senderUID);
                                if(oppUser != null){
                                    oppUserSock = oppUser.socketGet();
                                }
                            }
                        }
                        break;
                    case 2: // Sends to opponent only.
                        if(senderRID > 0){
                            if(myUser.roomIDGet() == senderRID){
                                // Gets the other user in the room.
                                myRoom = getRoom(senderRID);
                                oppUser = myRoom.otherUserGet(senderUID);

                                if(oppUser != null){
                                    oppUserSock = oppUser.socketGet();
                                }
                            }
                        }
                        break;
                    case 3: // Sends to everyone connected to the server. Unused so far.
                        break;
                    default:
                        break;
                }

                if(myUserSock != null){
                    PrintWriter out = null;

                    try {
                        out = new PrintWriter(myUserSock.getOutputStream(), true);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    if(out != null){
                        out.write(packetSnd + "\r\n");
                        out.flush();
                    }
                }

                if(oppUserSock != null){
                    PrintWriter out = null;

                    try {
                        out = new PrintWriter(oppUserSock.getOutputStream(), true);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    if(out != null){
                        out.write(packetSnd + "\r\n");
                        out.flush();
                    }
                }

                /* // Method to send to everyone. Unused.
                for(ServerClientThread thatClient : server.getClients()){
                    PrintWriter thatClientOut = thatClient.getWriter();
                    if(thatClientOut != null){
                        thatClientOut.write(packetSnd + "\r\n");
                        thatClientOut.flush();
                    }
                }
                 */
            }
        }
    }
}