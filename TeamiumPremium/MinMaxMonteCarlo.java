package TeamiumPremium;

import ProjectTwoEngine.*;

// TODO: Abstract out minimize() and maximize(); CardCounter update; dragon

// (P1 Turn start)
// P1: BuyMonsterMove (card AND price)
// P2: Respond
//  ?: Placement
// (P2 Turn)

public class MinMaxMonteCarlo {
    PlayerID id;
    PlayerID opponentId;

    CardCounter cardCounter;

    public MinMaxMonteCarlo() {
        RECORD PLAYER ID
    }

    @Override
    //This function is called when the game starts
    public void begin(GameState init_state) {
        // Card count!
    }

    @Override
    //This function is called when the player must select a monster to buy
    public BuyMonsterMove getBuyMonster(GameState state) {
        return act(state);
    }

    @Override
    //This function is called at the start of your opponent's turn
    public void startOppTurn(GameState state) {

    }

    @Override
    //This function is called when your opponent tried to buy a monster
    //If you steal, you will get the chosen monster
    //... but hand your opponent the price in coins
    public RespondMove getRespond(GameState state, Monster mon, int price) {
        return act(state);
    }

    @Override
    //This function is called when the opponent pays the price to steal
    // ... the monster chosen by the player
    public void stolenMonster(GameState state) { }

    @Override
    //This function is called when the player successfully buys a monster
    //... and needs to place the monster at a castle
    public PlaceMonsterMove getPlace(GameState state, Monster mon) {
        return act(state);
    }

    @Override
    public String getPlayName() {
        return "Professor Ium Moriartium";
    }

    // ADD DECK TO STATE
    // ADD DRAGON TO STATE
    private Move act(GameState state) {

        Move bestMove;
        double bestScore = double.POSITIVE_INFINITY;

        List<Move> legalMoves = GameRules.getLegalMoves(state);
        for (Move move : legalMoves) {
            List<Monster> backupDeck = state.getDeck().clone(); // Shallow-copy!

            GameState nextState = GameRules.makeMove(state, move);
            double moveScore;
            if (move instanceof BuyMonsterMove) {
                moveScore = minimize(nextState);
            } else if (move instanceof RespondMove) {
                RespondMove respondMove = move;
                if (move.isPass()) {
                    moveScore = maximize(nextState);
                } else {
                    moveScore = minimize(nextState);
                }
            } else if (move instanceof PlaceMonsterMove) {
                moveScore = minimize(nextState);
            }

            state.setDeck(backupDeck); // Because GameRules.makeMove modifies the deck (state copy constructor is shallow)
        
            if (moveScore > bestScore) {
                bestScore = moveScore;
                bestMove = move;
            }
        }

        return bestMove;
    }

    private int minimize(GameState state) {
        // End condition
            // Game is over

        // Pruning
            // Alpha-beta (eventually....)
            // Won 2 castles
            // Lost 2 castles

        // Check end condition
        // GetLegalMoves
        // Get the type (to figure out what action to do)
        // Min or Max

        if (state.isGameOver()) {
            return evaluateState(state);
        }

        List<Move> legalMoves = GameRules.getLegalMoves(state);
        Move bestMove;
        double bestScore = Double.POSITIVE_INFINITY;
        for (Move move : legalMoves) {
            List<Monster> backupDeck = state.getDeck().clone(); // Shallow-copy!

            GameState nextState = GameRules.makeMove(state, move);
            double moveScore;
            if (move instanceof BuyMonsterMove) {
                moveScore = maximize(nextState);
            } else if (move instanceof RespondMove) {
                RespondMove respondMove = move;
                if (move.isPass()) {
                    moveScore = minimize(nextState);
                } else {
                    moveScore = maximize(nextState);
                }
            } else if (move instanceof PlaceMonsterMove) {
                moveScore = maximize(nextState);
            }

            state.setDeck(backupDeck); // Because GameRules.makeMove modifies the deck (state copy constructor is shallow)
        
            if (moveScore < bestScore) {
                bestScore = moveScore;
                bestMove = move;
            }
        }

        return bestScore;
    }

    private int maximize(GameState state) {
        if (state.isGameOver()) {
            return evaluateState(state);
        }

        List<Move> legalMoves = GameRules.getLegalMoves(state);
        Move bestMove;
        double bestScore = Double.NEGATIVE_INFINITY;
        for (Move move : legalMoves) {
            List<Monster> backupDeck = state.getDeck().clone(); // Shallow-copy!

            GameState nextState = GameRules.makeMove(state, move);
            double moveScore;
            if (move instanceof BuyMonsterMove) {
                moveScore = minimize(nextState);
            } else if (move instanceof RespondMove) {
                RespondMove respondMove = move;
                if (move.isPass()) {
                    moveScore = maximize(nextState);
                } else {
                    moveScore = minimize(nextState);
                }
            } else if (move instanceof PlaceMonsterMove) {
                moveScore = minimize(nextState);
            }

            state.setDeck(backupDeck); // Because GameRules.makeMove modifies the deck (state copy constructor is shallow)
        
            if (moveScore > bestScore) {
                bestScore = moveScore;
                bestMove = move;
            }
        }

        return bestScore;
    }

    private double evaluateState(GameState state) {
        double score = 0;

        for (CastleID castleID : CastleID.values()) {
            if (state.getCastleWon(castleID) == this.id) {
                score += 1;
            } else {
                double ourTotal = 0;
                for (Monster monster : state.getMonsters(castleID, this.id)) {
                    ourTotal += monster.value;
                }

                double opponentTotal = 0;
                for (Monster monster: state.getMonsters(castleID, this.opponentId)) {
                    opponentTotal += monster.value;
                }
                
                double winningProbabilityHueristic
                if (ourTotal+opponentTotal == 0) {
                    winningProbabilityHueristic = 0.5;
                } else {
                    winningProbabilityHueristic = ourTotal / (ourTotal+opponentTotal);
                }

                score += winningProbabilityHueristic;
            }
        }

        return score;
    }
}