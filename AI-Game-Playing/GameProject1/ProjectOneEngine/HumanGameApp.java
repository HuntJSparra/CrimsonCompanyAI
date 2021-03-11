package ProjectOneEngine;

import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import javafx.util.Duration;
import javafx.animation.Timeline;
import javafx.animation.KeyFrame;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;

import java.util.Scanner;

public class HumanGameApp extends Application{
    Canvas test_canvas;
    Stage primary;
    GameState state;
    final double DELAY_TIME = 10.0;

    public void start(Stage primaryStage){
        primary = primaryStage;
        primaryStage.setTitle("A Test");
        Group root = new Group();
        test_canvas = new Canvas(1200, 800);
 
        root.getChildren().add(test_canvas);
        Scene mainScene;
        mainScene = new Scene(root, 1200, 800, Color.BEIGE);

	//This is Badly Coupled with the Graphics Code!! <sorry>
	// ... Really Ugly <sigh>
	// ************************
	mainScene.setOnMouseClicked(new EventHandler<MouseEvent>() {
		public void handle(MouseEvent event) {
		    double x = event.getX();
		    double y = event.getY();

		    int  mar = GameDisplayGraphics.MARGIN;
		    int sizeY = GameDisplayGraphics.SIZE_TALL;
		    int sizeX = GameDisplayGraphics.SIZE_WIDE;

		    double boxX = (sizeX-2*mar)/6;
		    double boxY = (sizeY-2*mar)/6;

		    int bin_num_bot = (int) ((x - mar)/boxX);

		    if (state.isTopTurn()){
			if ( (y > mar + boxY) && (y < mar + 3*boxY) ){
			    if((bin_num_bot >= 0) && (bin_num_bot < 6)){
				nextTurn(5 - bin_num_bot);
			    }
			}
		    }
		    else{
			if ( (y > mar + 3*boxY) && (y < mar + 5*boxY) ){
			    if((bin_num_bot >= 0) && (bin_num_bot < 6)){
				nextTurn(bin_num_bot);
			    }
			}
		    }
		}
	    });
        //************************

	
        primaryStage.setScene(mainScene);
        primaryStage.show();

	state = new GameState();
	GameDisplayGraphics.displayState(test_canvas, state);

    }

	/*
	
	Timeline quickTimer = new Timeline(new KeyFrame(Duration.seconds(DELAY_TIME), new EventHandler<ActionEvent>() {
		
		public void handle(ActionEvent event) {
		    nextTurn();
		}
	    }));
	
	quickTimer.setCycleCount(Timeline.INDEFINITE);
	quickTimer.play();		
	
        */

    void nextTurn(int bin_num){

	System.out.print("Player " + state.getCurPlayer().name() + "moves : ");	
	System.out.println(bin_num);
	state = GameRules.makeMove(state, bin_num);
	GameDisplayGraphics.displayState(test_canvas, state);
    }
}

	
