package ProjectOneEngine;

public class GameRules{

    static PlayerID otherPlayer(PlayerID p){
	if (p == PlayerID.TOP){
	    return PlayerID.BOT;
	    }
	else{
	    return PlayerID.TOP;
	}
    }

    static boolean noStones(PlayerID p, GameState state){
	for( int i=0; i<6; i++){
	    if( state.getStones(p, i) > 0){
		return false;
	    }
	}
	return true;
    }

    static boolean isGameOver(GameState state){
        boolean done = false;
	if (noStones( PlayerID.TOP, state) && noStones(PlayerID.BOT, state)){
	    done = true;
	}
	return done;
    }

    public static GameState makeMove(GameState state, Move mv){
	PlayerID play = mv.getPlayer();
	if( state.getCurPlayer() == play){
	    return makeMove(state, mv.getBin());
	}
	else
	    return null;
    }
			    
    
    public static GameState makeMove(GameState state, int bin){
	GameState new_state = new GameState(state);
	
	PlayerID cur_player = state.getCurPlayer();
	new_state.setCurPlayer( otherPlayer(cur_player) );

	int stones = new_state.getStones(cur_player, bin);

	if(stones == 0){
	    return null;
	}

	//Sowing the Stones
	new_state.setStones(cur_player, bin, 0);
	PlayerID drop_player = cur_player;
	int drop_bin = bin;
	while(stones > 0){
	    if(drop_bin < 5){
		drop_bin = drop_bin + 1;
	    }
	    else{
		drop_player = otherPlayer(drop_player);
		drop_bin = 0;
	    }
	    stones = stones - 1;
	    new_state.addStones(drop_player, drop_bin, 1);
	}

	//Capture the Stones
	boolean capturing = true;
	while (capturing){
	    if (cur_player != drop_player){
		int end_stones = new_state.getStones(drop_player, drop_bin);
		if ((end_stones == 2) || (end_stones ==3)){
		    new_state.setStones(drop_player, drop_bin, 0);
		    new_state.addHome(cur_player, end_stones);
		    if(drop_bin > 0){
			drop_bin = drop_bin - 1;
		    }
		    else{
			capturing = false;
		    }
		}
		else{
		    capturing = false;
		}
	    }
	    else{
		capturing = false;
	    }   
	}

	if( noStones( otherPlayer(cur_player), new_state) ){
	    new_state.makeGameOver();
	    int left_over_stones = 0;
	    for(int i=0; i<6; i++){
		left_over_stones +=  new_state.getStones(cur_player, i);
		new_state.setStones(cur_player, i, 0);
	    }
	    new_state.addHome(otherPlayer(cur_player), left_over_stones);
	}
	
	return new_state;

    }
}
