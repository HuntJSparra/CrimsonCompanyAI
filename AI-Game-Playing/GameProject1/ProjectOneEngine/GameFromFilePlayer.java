package ProjectOneEngine;

import java.io.FileReader;
import java.io.BufferedReader;
import java.io.IOException;

public class GameFromFilePlayer implements Player{

    BufferedReader game_file;
    String file_name;
    String ln;
    
    public Move getMove(GameState state) {
	PlayerID cur_player = state.getCurPlayer();
	Move move_from_file = null;

	try{
	    ln = game_file.readLine();
	    while( ! ln.contains(cur_player.name()) ||
		   ! ln.contains("moves") ){
		ln = game_file.readLine();
	    }
	    ln = ln.replaceAll("[^0-9]", "");
	    move_from_file = new Move( Integer.parseInt(ln), cur_player);
	}
	catch(IOException e){
	    System.out.println("ERROR READING MOVE FROM FILE!");
	}
	return move_from_file;
    }

    public String getPlayName(){
	return "Replay from file: " + file_name;
    }

    public GameFromFilePlayer(String f_name) {
	file_name = f_name;
	try {
	    FileReader reader = new FileReader(f_name);
	    game_file = new BufferedReader( reader);
	}
	catch(IOException e){
	    System.out.println("ERROR OPENING FILE: " + f_name );
	}
    }
	
	
}
	
