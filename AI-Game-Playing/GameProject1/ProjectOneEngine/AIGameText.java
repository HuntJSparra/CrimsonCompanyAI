//This class allows you to play a game between two AI without JavaFX
//The moves for the game are sent to a text file
package ProjectOneEngine;

import java.io.FileWriter;
import java.io.BufferedWriter;
import java.io.IOException;
import java.util.Scanner;

public class AIGameText{
    // Change this file name to change where the moves are written
    static final String FILE_NAME = "transcript.txt";
    
    static Player TOP_Player;
    static Player BOT_Player;
    
    static GameState state;
    
    public static void main(String[] args) throws IOException {
	//IMPORTANT : Change these lines to change who is playing!
	TOP_Player = new RandomPlayer();
	BOT_Player = new RandomPlayer();	
      
	state = new GameState(TOP_Player.getPlayName(), BOT_Player.getPlayName());

	BufferedWriter file_out = new BufferedWriter(new FileWriter( FILE_NAME ));
	file_out.write("Top Player is: ");
	file_out.write(TOP_Player.getPlayName());
	file_out.newLine();
	

	System.out.println("File created successfully");
	

	while (! GameRules.isGameOver(state)){
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
			file_out.write("ILLEGAL MOVE TOP CONCEDES!!");
			file_out.newLine();
			state = GameState.concedeState(cur_player);
		    }
		    else{
			System.out.println("ILLEGAL MOVE BOTTOM CONCEDES!!");
			file_out.write("ILLEGAL MOVE TOP CONCEDES!!");
			file_out.newLine();
			state = GameState.concedeState(cur_player);
		    }
		}
		
		int bin_num = nextMove.getBin();

	      	file_out.write("Player " + cur_player.name() + " moves : ");
		file_out.write(String.valueOf(bin_num) );
		file_out.newLine();
		state = GameRules.makeMove(state, bin_num);
	}

	int score_top = state.getHome(PlayerID.TOP);
	int score_bot = state.getHome(PlayerID.BOT);
	file_out.write("Game Over");
	file_out.newLine();
	file_out.write("Top Player Score: ");
	file_out.write(String.valueOf(score_top));
	file_out.newLine();
	file_out.write("Bot Player Score: ");
	file_out.write(String.valueOf(score_bot));
	file_out.newLine();
	
	if (score_top  > score_bot){
	    file_out.write("TOP PLAYER WINS!");
	    file_out.newLine();
	}
	if (score_top < score_bot){
	    file_out.write("BOT PLAYER WINS!");
	    file_out.newLine();
	}
	if (score_top == score_bot){
	    file_out.write("GAME IS A DRAW!");
	    file_out.newLine();
	}  
	file_out.close();
	       
    }

	
}

	
