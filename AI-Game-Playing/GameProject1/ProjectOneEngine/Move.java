package ProjectOneEngine;

public class Move {
    // Bins are numbered from Zero to Five
    // Bin Zero is on the left side as the player faces the board
    int bin;
    PlayerID play;

    public int getBin(){
	return bin;
    }

    public PlayerID getPlayer(){
	return play;
    }

    public Move (int bin_num, PlayerID play_id){
	bin = bin_num;
	play = play_id;
    }
}
