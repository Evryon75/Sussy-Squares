package ene_colombetti_amto.sussysquares;

import ene_colombetti_amto.sussysquares.game_elements.Susser;
import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.stage.Stage;

public class PlayField extends Application {

    //Game stage elements, needed across all files
    public static Group root = new Group();
    public static Scene scene = new Scene(root,1400, 800, Color.BLACK);
    public static Line half = new Line(scene.getWidth() / 2, 0, scene.getWidth() / 2, scene.getHeight());

    @Override
    public void start(Stage stage) {

        //Initializing the game elements
        half.setStroke(Color.WHITE);
        half.setStrokeWidth(5);
        root.getChildren().add(half);

        //Declaring and adding to the root the 2 players
        Susser impostor = new Susser(350, 400, 10, Color.RED);
        Susser crewmate = new Susser(1050, 400, 10, Color.CYAN);
        root.getChildren().addAll(impostor, crewmate);

        //Main game loop
        AnimationTimer frames = new AnimationTimer() {
            @Override
            public void handle(long l) {

                //Updating the 2 players
                impostor.update(crewmate);
                crewmate.update(impostor);
            }
        };

        frames.start(); //Starting the game

        //Assigning KeyEvent listeners
        scene.setOnKeyPressed(keyEvent -> {
            switch (keyEvent.getCode()) {
                //Imposter
                case A -> impostor.setLeft(true);
                case S -> impostor.setDown(true);
                case D -> impostor.setRight(true);
                case W -> impostor.setUp(true);
                case SPACE -> impostor.setShoot(true);
                //Crewmate
                case LEFT -> crewmate.setLeft(true);
                case DOWN -> crewmate.setDown(true);
                case RIGHT -> crewmate.setRight(true);
                case UP -> crewmate.setUp(true);
                case ENTER -> crewmate.setShoot(true);
            }
        });
        scene.setOnKeyReleased(keyEvent -> {
            switch (keyEvent.getCode()) {
                //Imposter
                case A -> impostor.setLeft(false);
                case S -> impostor.setDown(false);
                case D -> impostor.setRight(false);
                case W -> impostor.setUp(false);
                case SPACE -> impostor.setShoot(false);
                //Crewmate
                case LEFT -> crewmate.setLeft(false);
                case DOWN -> crewmate.setDown(false);
                case RIGHT -> crewmate.setRight(false);
                case UP -> crewmate.setUp(false);
                case ENTER -> crewmate.setShoot(false);
            }
        });

        //Setting up the stage
        stage.setTitle("Sussy Squares");
        stage.setResizable(false);
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}