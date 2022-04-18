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
package dk.dtu.compute.se.pisd.roborally.view;

import dk.dtu.compute.se.pisd.designpatterns.observer.Subject;
import dk.dtu.compute.se.pisd.roborally.controller.ConveyorBelt;
import dk.dtu.compute.se.pisd.roborally.controller.FieldAction;
import dk.dtu.compute.se.pisd.roborally.model.Heading;
import dk.dtu.compute.se.pisd.roborally.model.Player;
import dk.dtu.compute.se.pisd.roborally.model.Space;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.StrokeLineCap;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * ...
 *
 * @author Ekkart Kindler, ekki@dtu.dk
 *
 */
public class    SpaceView extends StackPane implements ViewObserver {

    final public static int SPACE_HEIGHT = 60; // 60; // 75;
    final public static int SPACE_WIDTH = 60;  // 60; // 75;

    public final Space space;

    public SpaceView(@NotNull Space space) {
        this.space = space;

        // XXX the following styling should better be done with styles
        this.setPrefWidth(SPACE_WIDTH);
        this.setMinWidth(SPACE_WIDTH);
        this.setMaxWidth(SPACE_WIDTH);

        this.setPrefHeight(SPACE_HEIGHT);
        this.setMinHeight(SPACE_HEIGHT);
        this.setMaxHeight(SPACE_HEIGHT);

        if ((space.x + space.y) % 2 == 0) {
            this.setStyle("-fx-background-color: white;");
        } else {
            this.setStyle("-fx-background-color: black;");
        }

        // updatePlayer();

        // This space view should listen to changes of the space
        space.attach(this);
        update(space);
    }

    public void checkWalls(){
        List<Heading> myWalls = space.getWalls();

        for(Iterator<Heading> i = myWalls.iterator(); i.hasNext(); ) {
            Heading myHeading = i.next();
            drawWall(myHeading);
        }
    }

    public void checkActions(){
        List<FieldAction> myActions = space.getActions();

        for(Iterator<FieldAction> i = myActions.iterator(); i.hasNext(); ) {
            FieldAction myAction = i.next();
            if(myAction.getClass() == ConveyorBelt.class){
                ConveyorBelt myBelt = (ConveyorBelt) myAction;
                Heading myBeltHeading = myBelt.getHeading();
                this.setStyle("-fx-background-image: url(\"ConveyeurIMG.png\");");
            }
        }
    }

    private void drawWall(Heading wallHeading){
        Pane pane = new Pane();
        Rectangle rectangle = new Rectangle(0.0, 0.0, SPACE_WIDTH, SPACE_HEIGHT);
        rectangle.setFill(Color.TRANSPARENT);
        pane.getChildren().add(rectangle);

        switch(wallHeading){
            case SOUTH:
                Line Sline = new Line(2, SPACE_HEIGHT-2, SPACE_WIDTH-2, SPACE_HEIGHT-2);
                Sline.setStroke(Color.RED);
                Sline.setStrokeWidth(5);
                pane.getChildren().add(Sline);
                this.getChildren().add(pane);
                break;
            case NORTH:
                Line Nline = new Line(2, 2, SPACE_WIDTH-2, 2);
                Nline.setStroke(Color.RED);
                Nline.setStrokeWidth(5);
                pane.getChildren().add(Nline);
                this.getChildren().add(pane);
                break;
            case WEST:
                Line Wline = new Line(2, 2, 2, SPACE_HEIGHT-2);
                Wline.setStroke(Color.RED);
                Wline.setStrokeWidth(5);
                pane.getChildren().add(Wline);
                this.getChildren().add(pane);
                break;
            case EAST:
                Line Eline = new Line(SPACE_WIDTH-2, 2, SPACE_WIDTH-2, SPACE_HEIGHT-2);
                Eline.setStroke(Color.RED);
                Eline.setStrokeWidth(5);
                pane.getChildren().add(Eline);
                this.getChildren().add(pane);
                break;
            default:
                break;
        }
    }


    private void updatePlayer() {
        this.getChildren().clear();
        checkWalls();

        Player player = space.getPlayer();
        if (player != null) {
            Polygon arrow = new Polygon(0.0, 0.0,
                    10.0, 20.0,
                    20.0, 0.0 );
            try {
                arrow.setFill(Color.valueOf(player.getColor()));
            } catch (Exception e) {
                arrow.setFill(Color.MEDIUMPURPLE);
            }

            arrow.setRotate((90*player.getHeading().ordinal())%360);
            this.getChildren().add(arrow);
        }
    }

    @Override
    public void updateView(Subject subject) {
        if (subject == this.space) {
            updatePlayer();
        }
    }
}
