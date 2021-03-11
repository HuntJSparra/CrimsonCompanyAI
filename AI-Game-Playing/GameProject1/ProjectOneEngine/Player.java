package ProjectOneEngine;

public interface Player{

    // This should return the move that your AI chooses to make
    // The input is the current state of the game.
    public Move getMove(GameState state);

    // This should return a string identifying the player
    // For example, you might return "BobAI version 1.1";
    public String getPlayName();

}
