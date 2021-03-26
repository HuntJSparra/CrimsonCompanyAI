package TeamiumPremium;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import ProjectTwoEngine.*;

import TeamiumPremium.CardCounter;

// TODO: Abstract out minimize() and maximize(); Shuffle legal moves;
//        handle slayers and dead dragons in evaluation and pruning.
//        Current heuristics penalize low card (including slayer)
//        usage.

// (P1 Turn start)
// P1: BuyMonsterMove (card AND price)
// P2: Respond
//  ?: Placement
// (P2 Turn)

public class MonteCarlo implements Player {
    PlayerID id;
    PlayerID opponentId;

    CardCounter cardCounter;

    int TOTAL_SEEDS = 1000;

    public MonteCarlo() {
        cardCounter = new CardCounter();

        // id and opponentId are set by begin()
    }

    @Override
    //This function is called when the game starts
    public void begin(GameState init_state) {
        // id and opponentId are based on the hidden information
        if (init_state.getHidden(PlayerID.TOP) == null) {
            this.id         = PlayerID.BOT;
            this.opponentId = PlayerID.TOP;
        } else {
            this.id         = PlayerID.TOP;
            this.opponentId = PlayerID.BOT;
        }

        // Card count!
        for (Monster monster : init_state.getPublicMonsters()) {
            cardCounter.cardDrawn(monster);
        }

        // The next card update is handled by startOppTurn()
        //    and getBuyMonster()
        // The idea behind this is that there will be a new next
        //    card (i.e. new drawn card) at the start of each turn
    }

    @Override
    //This function is called when the player must select a monster to buy
    public BuyMonsterMove getBuyMonster(GameState state) {
        cardCounter.cardDrawn(state.getNextMonster());
        return (BuyMonsterMove)act(state);
    }

    @Override
    //This function is called at the start of your opponent's turn
    public void startOppTurn(GameState state) {
        cardCounter.cardDrawn(state.getNextMonster());
    }

    @Override
    //This function is called when your opponent tried to buy a monster
    //If you steal, you will get the chosen monster
    //... but hand your opponent the price in coins
    public RespondMove getRespond(GameState state, Monster mon, int price) {
        return (RespondMove)act(state);
    }

    @Override
    //This function is called when the opponent pays the price to steal
    // ... the monster chosen by the player
    public void stolenMonster(GameState state) { }

    @Override
    //This function is called when the player successfully buys a monster
    //... and needs to place the monster at a castle
    public PlaceMonsterMove getPlace(GameState state, Monster mon) {
        return (PlaceMonsterMove)act(state);
    }

    @Override
    public String getPlayName() {
        return "Professor Ium Moriartium";
    }

    private Move act(GameState state) {
        List<Move> legalMoves = GameRules.getLegalMoves(state);
        HashMap<Move, Integer> moveToWins = new HashMap<Move, Integer>();

        for (int seedNum=0; seedNum<TOTAL_SEEDS; seedNum++) {
            state.setDeck(cardCounter.createDeck());
            state.setHidden(opponentId, randomOpponentDragonPlacement(state));

            for (Move move : legalMoves) {
                int currentWins = moveToWins.getOrDefault(move, 0);
                moveToWins.put(move, currentWins+moveWins(move, state));
            }
        }

        // Select move with most wins
        int bestWins = -1;
        Move bestMove = null;
        for (Move move : moveToWins.keySet()) {
            if (moveToWins.get(move) > bestWins) {
                bestMove = move;
                bestWins = moveToWins.get(move);
            }
        }

        return bestMove;
    }

    private int moveWins(Move move, GameState state) {
        state.setDeck(new ArrayList<Monster>(state.getDeck())); // Shallow-copy

        GameState nextState = GameRules.makeMove(state, move);
        while (!nextState.isGameOver()) {
            List<Move> legalMoves = GameRules.getLegalMoves(nextState);
            Collections.shuffle(legalMoves);

            // Bug with isGameOver();
            if (legalMoves.size() == 0) {
                break;
            }
            // System.out.println("Castles:");
            // for (CastleID castleId : CastleID.values()) {
            //     System.out.println(nextState.getCastleWon(castleId)+"\t"+nextState.getMonsters(castleId, PlayerID.TOP)+"\t"+nextState.getMonsters(castleId, PlayerID.BOT));
            // }

            Move nextMove = legalMoves.get(0);
            // System.out.println(nextMove);

            nextState = GameRules.makeMove(nextState, nextMove);
        }

        int castlesWon = 0;
        for (CastleID castleId : CastleID.values()) {
            if (state.getCastleWon(castleId) == this.id) {
                castlesWon += 1;
            }
        }

        return castlesWon;
    }

    // Returns a random CastleID (does not factor in actual probability)
    private CastleID randomOpponentDragonPlacement(GameState state) {
        Random rng = new Random();
        CastleID[] ids = CastleID.values();
        return ids[ rng.nextInt(ids.length) ];
    }
}