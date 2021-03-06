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
package dk.dtu.compute.se.pisd.roborally.fileaccess;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import dk.dtu.compute.se.pisd.roborally.controller.FieldAction;
import dk.dtu.compute.se.pisd.roborally.fileaccess.model.BoardTemplate;
import dk.dtu.compute.se.pisd.roborally.fileaccess.model.PlayerTemplate;
import dk.dtu.compute.se.pisd.roborally.fileaccess.model.SpaceTemplate;
import dk.dtu.compute.se.pisd.roborally.model.Board;
import dk.dtu.compute.se.pisd.roborally.model.Player;
import dk.dtu.compute.se.pisd.roborally.model.Space;

import java.io.*;

/**
 * ...
 *
 * @author Ekkart Kindler, ekki@dtu.dk
 */
public class LoadBoard {

    private static final String BOARDSFOLDER = "boards";
    private static final String DEFAULTBOARD = "defaultboard";
    private static final String JSON_EXT = "json";

    public static Board loadBoard(String stringOrBoard) {
        if (stringOrBoard == null) {
            stringOrBoard = DEFAULTBOARD;
        }

        ClassLoader classLoader = LoadBoard.class.getClassLoader();
        InputStream inputStream = null;
        if(stringOrBoard.length() < 50){
            inputStream = classLoader.getResourceAsStream(BOARDSFOLDER + "/" + stringOrBoard + "." + JSON_EXT);
        }
        else{
            inputStream = new ByteArrayInputStream(stringOrBoard.getBytes());
        }
        //Trying to find the bug by specifying the exact file instead of taking variable input.
        //InputStream = classLoader.getResourceAsStream("C:\\Users\\Sebastian\\IdeaProjects\\roborallyTryingJson\\02324-f22-master-e1f89f504da192119e73ae0ea99f3085281965a4\\RoboRally\\roborally-1.1.0-java17\\roborally\\boards\\defaultboard.json");

        if (inputStream == null) {
            System.out.println("InputStream is null");
            // TODO these constants should be defined somewhere
            return null;
        }

        // In simple cases, we can create a Gson object with new Gson():
        GsonBuilder simpleBuilder = new GsonBuilder().
        registerTypeAdapter(FieldAction.class, new Adapter<FieldAction>());
        Gson gson = simpleBuilder.create();

        Board result;
        // FileReader fileReader = null;
        JsonReader reader = null;
        try {
            // fileReader = new FileReader(filename);
            
            reader = gson.newJsonReader(new InputStreamReader(inputStream));
            BoardTemplate template = gson.fromJson(reader, BoardTemplate.class);
            System.out.println(template.phase);

            result = new Board(template.width, template.height);
            for (SpaceTemplate spaceTemplate : template.spaces) {
                Space space = result.getSpace(spaceTemplate.x, spaceTemplate.y);
                if (space != null) {
                    space.getActions().addAll(spaceTemplate.actions);
                    space.getWalls().addAll(spaceTemplate.walls);
                }
            }
            for (PlayerTemplate playerTemplate : template.players) {
                System.out.println(playerTemplate.isCurrent);
                Player player = new Player(result, playerTemplate.color, playerTemplate.name);
                player.setCheckpointNumber(playerTemplate.CheckpointAmount);
                player.setHeading(player.getHeading());
                player.setSpace(result.getSpace(playerTemplate.x, playerTemplate.y));
                result.getPlayers().add(player);
                if (playerTemplate.isCurrent)
                    System.out.println("Player is current");
                result.setCurrentPlayer(player);

            }
            result.setPhase(template.phase);
            reader.close();
            return result;
        } catch (IOException e1) {
            if (reader != null) {
                try {
                    reader.close();
                    inputStream = null;
                } catch (IOException e2) {
                    System.out.println("Problem with loading files");
                }
            }
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e2) {
                    System.out.println("Problem with loading files");
                }
            }
        }
        return null;
    }

    public static String saveBoard(Board board, String name) {
        System.out.println(name);
        BoardTemplate template = new BoardTemplate();
        template.width = board.width;
        template.height = board.height;
        template.phase = board.getPhase();


        //Loop for saving the spaces
        for (int i = 0; i < board.width; i++) {
            for (int j = 0; j < board.height; j++) {
                Space space = board.getSpace(i, j);
                if (!space.getWalls().isEmpty() || !space.getActions().isEmpty()) {
                    SpaceTemplate spaceTemplate = new SpaceTemplate();
                    spaceTemplate.x = space.x;
                    spaceTemplate.y = space.y;
                    spaceTemplate.actions.addAll(space.getActions());
                    spaceTemplate.walls.addAll(space.getWalls());
                    template.spaces.add(spaceTemplate);
                }
            }
        }
        //Loop for saving the players
        for (int i = 0; i < board.getPlayers().size(); i++) {
            PlayerTemplate playerTemplate = new PlayerTemplate();
            playerTemplate.heading = board.getPlayers().get(i).getHeading();
            playerTemplate.CheckpointAmount = board.getPlayers().get(i).getCheckpointNumber();
            playerTemplate.x = board.getPlayers().get(i).getSpace().x;
            playerTemplate.y = board.getPlayers().get(i).getSpace().y;
            playerTemplate.name = board.getPlayers().get(i).getName();
            playerTemplate.color = board.getPlayers().get(i).getColor();
            if (board.getPlayers().get(i).equals(board.getCurrentPlayer()))
                playerTemplate.isCurrent = true;
            else
                playerTemplate.isCurrent = false;

            template.players.add(playerTemplate);
        }


        ClassLoader classLoader = LoadBoard.class.getClassLoader();
        // TODO: this is not very defensive, and will result in a NullPointerException
        //       when the folder "resources" does not exist! But, it does not need
        //       the file "simpleCards.json" to exist!
        String filename =
                classLoader.getResource(BOARDSFOLDER).getPath() + "/" + name + "." + JSON_EXT;
        System.out.println(filename);


        // In simple cases, we can create a Gson object with new:
        //
        //   Gson gson = new Gson();
        //
        // But, if you need to configure it, it is better to create it from
        // a builder (here, we want to configure the JSON serialisation with
        // a pretty printer):
        GsonBuilder simpleBuilder = new GsonBuilder().
        registerTypeAdapter(FieldAction.class, new Adapter<FieldAction>()).
        setPrettyPrinting();
        Gson gson = simpleBuilder.create();

        FileWriter fileWriter = null;
        JsonWriter writer = null;
        try {
            fileWriter = new FileWriter(filename);
            writer = gson.newJsonWriter(fileWriter);
            gson.toJson(template, template.getClass(), writer);
            writer.close();
        } catch (IOException e1) {
            System.out.println("e1");
            if (writer != null) {
                try {
                    writer.close();
                    fileWriter = null;
                } catch (IOException e2) {
                    System.out.println("Cant close writer properly ");
                }
            }
            if (fileWriter != null) {
                try {
                    fileWriter.close();
                } catch (IOException e2) {
                    System.out.println("Cant write to file");
                }
            }
        }
        return gson.toJson(template, template.getClass());
    }
}
