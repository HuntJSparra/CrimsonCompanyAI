package TeamiumPremium;

import java.util.ArrayList;
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

public class MinMaxMonteCarlo implements Player {
    PlayerID id;
    PlayerID opponentId;

    CardCounter cardCounter;

    int MAX_DEPTH = 5;

    public MinMaxMonteCarlo() {
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
        state.setDeck(cardCounter.createDeck());
        state.setHidden(opponentId, randomOpponentDragonPlacement(state));

        double alpha = Double.NEGATIVE_INFINITY;
        double beta = Double.POSITIVE_INFINITY;

        List<Move> legalMoves = GameRules.getLegalMoves(state);

        Move bestMove = legalMoves.get(0);
        double bestScore = alpha;
        for (Move move : legalMoves) {
            List<Monster> backupDeck = new ArrayList(state.getDeck()); // Shallow-copy!

            GameState nextState = GameRules.makeMove(state, move);
            double moveScore = 0;
            if (move instanceof BuyMonsterMove) {
                moveScore = minimize(nextState, alpha, beta, MAX_DEPTH);
            } else if (move instanceof RespondMove) {
                RespondMove respondMove = (RespondMove)move;
                if (respondMove.isPass()) {
                    moveScore = maximize(nextState, alpha, beta, MAX_DEPTH);
                } else {
                    moveScore = minimize(nextState, alpha, beta, MAX_DEPTH);
                }
            } else if (move instanceof PlaceMonsterMove) {
                moveScore = minimize(nextState, alpha, beta, MAX_DEPTH);
            }

            state.setDeck(backupDeck); // Because GameRules.makeMove modifies the deck (state copy constructor is shallow)
            
            System.out.println(moveScore);
            if (moveScore > bestScore) {
                alpha = moveScore;
                bestScore = moveScore;
                bestMove = move;
            }
        }

        System.out.println("=======================");

        return bestMove;
    }

    private double minimize(GameState state, double alpha, double beta, int depthRemaining ) {
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

        if (depthRemaining < 0 || state.isGameOver() || isLostCause(state)) {
            return evaluateState(state);
        }

        List<Move> legalMoves = GameRules.getLegalMoves(state);
        double bestScore = beta; // MIN = beta
        for (Move move : legalMoves) {
            List<Monster> backupDeck = new ArrayList(state.getDeck()); // Shallow-copy!

            GameState nextState = GameRules.makeMove(state, move);
            double moveScore = 0;
            if (move instanceof BuyMonsterMove) {
                moveScore = maximize(nextState, alpha, beta, depthRemaining-1); // MIN = mAXmimize()
            } else if (move instanceof RespondMove) {
                RespondMove respondMove =  (RespondMove)move;
                if (respondMove.isPass()) {
                    moveScore = minimize(nextState, alpha, beta, depthRemaining-1); // MIN = mINimize()
                } else {
                    moveScore = maximize(nextState, alpha, beta, depthRemaining-1); // MIN = mAXimize()
                }
            } else if (move instanceof PlaceMonsterMove) {
                moveScore = maximize(nextState, alpha, beta, depthRemaining-1); // MIN = maximize()
            }

            state.setDeck(backupDeck); // Because GameRules.makeMove modifies the deck (state copy constructor is shallow)

            if (moveScore < bestScore) { // MIN = <
                beta = bestScore; // MIN = beta
                bestScore = moveScore;
            }

            if (bestScore <= alpha) { // MIN = <= alpha
                break;
            }
        }

        return bestScore;
    }

    private double maximize(GameState state, double alpha, double beta, int depthRemaining ) {
        if (depthRemaining < 0 || state.isGameOver() || isLostCause(state)) {
            return evaluateState(state);
        }

        List<Move> legalMoves = GameRules.getLegalMoves(state);
        double bestScore = alpha; // MIN = beta
        for (Move move : legalMoves) {
            List<Monster> backupDeck = new ArrayList(state.getDeck()); // Shallow-copy!

            GameState nextState = GameRules.makeMove(state, move);
            double moveScore = 0;
            if (move instanceof BuyMonsterMove) {
                moveScore = minimize(nextState, alpha, beta, depthRemaining-1); // MIN = mAXmimize()
            } else if (move instanceof RespondMove) {
                RespondMove respondMove =  (RespondMove)move;
                if (respondMove.isPass()) {
                    moveScore = maximize(nextState, alpha, beta, depthRemaining-1); // MIN = mINimize()
                } else {
                    moveScore = minimize(nextState, alpha, beta, depthRemaining-1); // MIN = mAXimize()
                }
            } else if (move instanceof PlaceMonsterMove) {
                moveScore = minimize(nextState, alpha, beta, depthRemaining-1); // MIN = maximize()
            }

            state.setDeck(backupDeck); // Because GameRules.makeMove modifies the deck (state copy constructor is shallow)

            if (moveScore > bestScore) { // MIN = <
                alpha = bestScore; // MIN = beta
                bestScore = moveScore;
            }

            if (bestScore >= beta) { // MIN = <= alpha
                break;
            }
        }

        return bestScore;
    }

    private double evaluateState(GameState state) {
        double score = 0;

        for (CastleID castleID : CastleID.values()) {
            if (state.getCastleWon(castleID) == this.id) {
                score += 1;
            } else if (state.getCastleWon(castleID) == this.opponentId) {
                // No points for lost castles
            } else {
                double ourTotal = 0;
                for (Monster monster : state.getMonsters(castleID, this.id)) {
                    ourTotal += monster.value;
                }

                double opponentTotal = 0;
                for (Monster monster: state.getMonsters(castleID, this.opponentId)) {
                    opponentTotal += monster.value;
                }
                
                double winningProbabilityHueristic;
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

    // Returns a random CastleID (does not factor in actual probability)
    private CastleID randomOpponentDragonPlacement(GameState state) {
        Random rng = new Random();
        CastleID[] ids = CastleID.values();
        return ids[ rng.nextInt(ids.length) ];
    }

    private boolean isLostCause(GameState state) {
        // PRUNE 1: Bot has less than half their opponent's coins (and their opponent has more than 3)
        // if ( (state.getCoins(opponentId) > 3) && (2*state.getCoins(id) < state.getCoins(opponentId)) ) {
        //     return true;
        // }

        // (questionable)
        // PRUNE 2: Bot has less than opponent on all castles
        // int losingCastles = 0;
        // for (CastleID castleId : CastleID.values()) {
        //     int playerTotal = 0;
        //     for (Monster monster : state.getMonsters(castleId, this.id)) {
        //         playerTotal += monster.value;
        //     }

        //     int opponentTotal = 0;
        //     for (Monster monster : state.getMonsters(castleId, this.id)) {
        //         opponentTotal += monster.value;
        //     }

        //     if (opponentTotal > playerTotal) {
        //         losingCastles += 1;
        //     }
        // }

        // if (losingCastles > 2) {
        //     return true;
        // }

        return false;
    }
}