package ProjectOneEngine;

import java.util.List;
import java.util.ArrayList;

public class GameState {
    ArrayList<Integer> top_bins;
    ArrayList<Integer> bot_bins;
    int top_home;
    int bot_home;
    boolean top_turn;
    boolean game_over;
    String top_name;
    String bot_name;

    static public GameState concedeState(PlayerID play){
	GameState state = new GameState();

	for (int i = 0; i<6; i++){
	    state.setStones(PlayerID.TOP, i, 0);
	    state.setStones(PlayerID.BOT, i, 0);
	}

	if(play == PlayerID.TOP){
	    state.addHome(PlayerID.BOT, 48);
	}
	else{
	    state.addHome(PlayerID.TOP, 48);
	}
	return state;
    }
    
    void setStones(PlayerID play_id, int bin_num, int stones){
	if (play_id == PlayerID.TOP){
	    top_bins.set(bin_num, stones);
	}
	if (play_id == PlayerID.BOT){
	    bot_bins.set(bin_num, stones);
	}
    }

    void addStones(PlayerID play_id, int bin_num, int stones){
	if (play_id == PlayerID.TOP){
	    int old = top_bins.get(bin_num).intValue();
	    top_bins.set(bin_num, old + stones);
	}
	if (play_id == PlayerID.BOT){
	    int old = bot_bins.get(bin_num).intValue();
	    bot_bins.set(bin_num, old + stones);
	}
    }

    void addHome(PlayerID play_id, int stones){
	if (play_id == PlayerID.TOP){
	    top_home = top_home + stones;
	}
	if (play_id == PlayerID.BOT){
	    bot_home = bot_home + stones;
	}
    }

    
    public int getStones(PlayerID play_id, int bin_num){
	int answer = -1;
	
	if (play_id == PlayerID.TOP){
	    answer = top_bins.get(bin_num).intValue();
	}
	if (play_id == PlayerID.BOT){
	    answer = bot_bins.get(bin_num).intValue();
	}

	return answer;
    }

    public int getHome(PlayerID play_id){
	int answer = -1;
	
	if (play_id == PlayerID.TOP){
	    answer = top_home;
	}
	if (play_id == PlayerID.BOT){
	    answer = bot_home;
	}
	return answer;
    }

    public String getTopName(){
	return top_name;
    }

    public String getBotName(){
	return bot_name;
    }

    public String getPlayName(PlayerID play){
	if( play == PlayerID.TOP ){
	    return top_name;
	}
	else{
	    return bot_name;
	}
    }

    public boolean isTopTurn(){
	return top_turn;
    }

    public boolean isGameOver(){
	return game_over;
    }

    void makeGameOver(){
	game_over = true;
    }

    public PlayerID getCurPlayer(){
	if (top_turn){
	    return PlayerID.TOP;
	}
	else{
	    return PlayerID.BOT;
	}
    }

    void setCurPlayer(PlayerID play){
	if (play == PlayerID.TOP){
	    top_turn = true;
	}
	else{
	    top_turn = false;
	}
    }
    
    public GameState(){
	top_bins = new ArrayList<Integer>();
	bot_bins = new ArrayList<Integer>();

	top_name = "Human Player";
	bot_name = "Human Player";
	
	for(int i=0; i<6; i++){
	    top_bins.add(4);
	    bot_bins.add(4);
	}

	top_home = 0;
	bot_home = 0;
	top_turn = false;
	game_over = false;
    }

    public GameState(String nameT, String nameB){
	top_bins = new ArrayList<Integer>();
	bot_bins = new ArrayList<Integer>();

	top_name = nameT;
	bot_name = nameB;

	for(int i=0; i<6; i++){
	    top_bins.add(4);
	    bot_bins.add(4);
	}

	top_home = 0;
	bot_home = 0;
	top_turn = false;
	game_over = false;
    }
    

    // Deep Copy Constructor
    public GameState(GameState old_state){
      	top_bins = new ArrayList<Integer>();
	bot_bins = new ArrayList<Integer>();

	top_name = old_state.top_name;
	bot_name = old_state.bot_name;

	for(int i=0; i<6; i++){
	    top_bins.add( old_state.getStones(PlayerID.TOP, i));
	    bot_bins.add( old_state.getStones(PlayerID.BOT, i));
	}

	top_home = old_state.getHome(PlayerID.TOP);
	bot_home = old_state.getHome(PlayerID.BOT);
	top_turn = old_state.isTopTurn();
	game_over = old_state.isGameOver();
    } 

				
}
