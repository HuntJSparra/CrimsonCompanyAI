// To play with your AI player, you need to do two things:
// 1) Add an import statement to import your AI player
// 2) Change the lines inside start that say:
//      TOP_Player =
//      BOT_Player =
//
// Note: You can replay a game from a text file by setting
//        File_Name to be a String with the name of the file

package ProjectOneEngine;

// Here I am importing the player CrazyBaab.
// You can change this to import your player
import ArmyOfBaab.Baab;
import ArmyOfBaab.LittleBaab;
import ArmyOfBaab.BabyBaab;
import ArmyOfBaab.CleverBaab;
import ArmyOfBaab.CarefulBaab;
import ArmyOfBaab.WiseOldBaab;


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

public class AIGameApp extends Application{
    // IMPORTANT: File_Name = null
    //      signals that we are NOT replaying a game from a file  
    static final String File_Name = null;
    // static final String File_Name = "transcript.txt";
    
    Player TOP_Player;
    Player BOT_Player;
    
    Canvas test_canvas;
    Stage primary;
    GameState state;
    
    // You can raise this delay to slow down the AI moves
    final double DELAY_TIME = 0.5;

    public void start(Stage primaryStage){
	//IMPORTANT : Change these lines to change who is playing!
	TOP_Player = new WiseOldBaab();
	BOT_Player = new LittleBaab();  // null means "Human Player"

	//IMPORTANT : If there is a File_Name
	//     Then we will always display the game from the file!
	if( File_Name != null){
	    TOP_Player = new GameFromFilePlayer(File_Name);
	    BOT_Player = new GameFromFilePlayer(File_Name);
	}


	//Set up the names in the state object
	String nameTop;
	String nameBot;
	if (TOP_Player != null){
	    nameTop = TOP_Player.getPlayName();
	}
	else{
	    nameTop = "Human Player";
	}
	if (BOT_Player != null){
	    nameBot = BOT_Player.getPlayName();
	}
	else{
	    nameBot = "Human Player";
	}


	state = new GameState(nameTop, nameBot );
        primary = primaryStage;
	String title = "TOP: " + nameTop + " ";
	title = title + "  vs  BOT: " + nameBot;
        primaryStage.setTitle(title);
        Group root = new Group();
        test_canvas = new Canvas(1200, 800);
 
        root.getChildren().add(test_canvas);
        Scene mainScene;
        mainScene = new Scene(root, 1200, 800, Color.BEIGE);

	//This is Badly Coupled with the Graphics Code!! <sorry>
	// ... Really Ugly <sigh>
	//*********************************
	mainScene.setOnMouseClicked(new EventHandler<MouseEvent>() {
		public void handle(MouseEvent event) {
		    if((state.isTopTurn() && TOP_Player == null) ||
		       (!state.isTopTurn() && BOT_Player == null)){
			
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
				    humanTurn(5 - bin_num_bot);
				}
			    }
			}
			else if (!state.isTopTurn()){
			    if ( (y > mar + 3*boxY) && (y < mar + 5*boxY) ){
				if((bin_num_bot >= 0) && (bin_num_bot < 6)){
				    humanTurn(bin_num_bot);
				}
			    }
			}
		    }
		}
	    });
	//**************************************
		  
        primaryStage.setScene(mainScene);
        primaryStage.show();

	Timeline quickTimer = new Timeline(new KeyFrame(Duration.seconds(DELAY_TIME), new EventHandler<ActionEvent>() {
		
		public void handle(ActionEvent event) {
		    nextAITurn();
		}
	    }));
	
	quickTimer.setCycleCount(Timeline.INDEFINITE);
	quickTimer.play();		

	    
	GameDisplayGraphics.displayState(test_canvas, state);

    }


    void nextAITurn(){
	if (! GameRules.isGameOver(state)){
	    if(( state.isTopTurn() && TOP_Player != null) || ( !state.isTopTurn() && BOT_Player !=null)){
		PlayerID cur_player = state.getCurPlayer();
		GameState copy_state = new GameState(state);
		Move nextMove = null;
	    
		if (cur_player == PlayerID.TOP){
		    nextMove = TOP_Player.getMove(copy_state);
		}
		else{
		    nextMove = BOT_Player.getMove(copy_state);
		}
		
		if ((nextMove == null) || (GameRules.makeMove(state,nextMove) == null)){
		    if(cur_player == PlayerID.TOP){
			System.out.println("ILLEGAL MOVE TOP CONCEDES!!");
			state = GameState.concedeState(cur_player);
		    }
		    else{
			System.out.println("ILLEGAL MOVE BOTTOM CONCEDES!!");
			state = GameState.concedeState(cur_player);
		    }
		}
		
		int bin_num = nextMove.getBin();

	      	System.out.print("Player " + cur_player.name() + "moves : ");
		System.out.println(bin_num);
		state = GameRules.makeMove(state, bin_num);
		if (state.isGameOver()){
		    int t_sc = state.getHome(PlayerID.TOP);
		    int b_sc = state.getHome(PlayerID.BOT);
		    
		    if( t_sc > b_sc ){
			System.out.println("TOP Wins: " + TOP_Player.getPlayName());
		    }
		    if(t_sc < b_sc){	
			System.out.println("BOT Wins: " + BOT_Player.getPlayName());
		    }
		    if(b_sc == t_sc){
			System.out.println("The Game is a Draw!");
		    }

		}
		GameDisplayGraphics.displayState(test_canvas, state);
	    }
	}
    }

    void humanTurn(int bin_num){
	if (! GameRules.isGameOver(state)){
	    PlayerID cur_player = state.getCurPlayer();

	    if ((GameRules.makeMove(state, bin_num) != null)){
		System.out.print("Player " + cur_player.name() + " moves : ");
		System.out.println(bin_num);
		state = GameRules.makeMove(state, bin_num);
		GameDisplayGraphics.displayState(test_canvas, state);
	    }
	}
    }
	
}

	
