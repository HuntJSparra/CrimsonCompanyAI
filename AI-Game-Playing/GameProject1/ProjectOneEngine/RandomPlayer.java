package ProjectOneEngine;
import java.util.Random;

public class RandomPlayer implements Player{

    public Move getMove(GameState state){

	Random rand = new Random();
	boolean done = false;
	PlayerID cur_player = state.getCurPlayer();
	Move rand_move = null;

	while ( ! done ){
	    int bin = rand.nextInt(6);
	    rand_move = new Move(bin, cur_player);
	    if (GameRules.makeMove(state, rand_move) != null){
		done = true;
	    }
	}
	return rand_move;
    }

    public String getPlayName(){
	return "Completely Random Player";
    }
}
	
