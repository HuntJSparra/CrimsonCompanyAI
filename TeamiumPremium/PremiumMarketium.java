package TeamiumPremium;

import ProjectTwoEngine.BuyMonsterMove;
import ProjectTwoEngine.CastleID;
import ProjectTwoEngine.GameRules;
import ProjectTwoEngine.GameState;
import ProjectTwoEngine.Monster;
import ProjectTwoEngine.Move;
import ProjectTwoEngine.PlaceMonsterMove;
import ProjectTwoEngine.Player;
import ProjectTwoEngine.PlayerID;
import ProjectTwoEngine.RespondMove;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

/**
 * April 1st, 2021
 *
 * @author Jan Fic
 */
public class PremiumMarketium implements Player {

    private CardCounter cardCounter;

    PlayerID id;
    PlayerID opponentId;

    int MAX_DEPTH = 5;
    int MAX_CARLO = 30;
    
    public PremiumMarketium() {
        this.cardCounter = new CardCounter();
    }

    @Override
    public void begin(GameState init_state) {
        // id and opponentId are based on the hidden information
        if (init_state.getHidden(PlayerID.TOP) == null) {
            this.id = PlayerID.BOT;
            this.opponentId = PlayerID.TOP;
        } else {
            this.id = PlayerID.TOP;
            this.opponentId = PlayerID.BOT;
        }

        // Card count!
        for (Monster monster : init_state.getPublicMonsters()) {
            this.cardCounter.cardDrawn(monster);
        }

        // The next card update is handled by startOppTurn()
        //    and getBuyMonster()
        // The idea behind this is that there will be a new next
        //    card (i.e. new drawn card) at the start of each turn
    }

    @Override
    public BuyMonsterMove getBuyMonster(GameState state) {
        return getBuyMonsterForPlay(state, this.id);
    }

    public BuyMonsterMove getBuyMonsterForPlay(GameState state, PlayerID play) {
        this.cardCounter.cardDrawn(state.getNextMonster());
        Monster bestCard = null;
        float bestValue = -1;
        Monster lowestNonSlayer = Monster.DRAGON; // Can be bad if it is a high value monster
        for (Monster publicMonster : state.getPublicMonsters()) {
            if (!publicMonster.equals(Monster.SLAYER) && publicMonster.value < lowestNonSlayer.value) {
                lowestNonSlayer = publicMonster;
            }
            if (evaluateCard(state, publicMonster, play) > bestValue) {
                bestCard = publicMonster;
            }
        }

        int price = (int) Math.min(bestValue, state.getCoins(this.opponentId) + 1);

        if (state.getCoins(this.id) >= price) {
            return new BuyMonsterMove(this.id, price, bestCard);
        } else {
            return new BuyMonsterMove(id, 1, lowestNonSlayer);
        }
    }

    @Override
    public void startOppTurn(GameState state) {
        this.cardCounter.cardDrawn(state.getNextMonster());
    }

    @Override
    public RespondMove getRespond(GameState state, Monster mon, int price) {
        return getRespondForPlay(state, mon, price, this.id);
    }

    public RespondMove getRespondForPlay(GameState state, Monster mon, int price, PlayerID play) {
        boolean steal = evaluateCard(state, mon, play) < price * 2 && state.getCoins(play) >= price;
        return new RespondMove(play, !steal, mon);
    }

    @Override
    public void stolenMonster(GameState state) {

    }

    @Override
    public PlaceMonsterMove getPlace(GameState state, Monster mon) {
        return (PlaceMonsterMove)act(state);
    }

    @Override
    public String getPlayName() {
        return "Premium Marketium";
    }

    /* Helper Functions */
    /**
     * Evaluates card "value" so we can compare to other cards. Helps with
     * stealing.
     *
     * @param state
     * @param mon
     */
    private float evaluateCard(GameState state, Monster mon, PlayerID play) {

        float monsterVal = mon.value;
        PlayerID playsOpponent = play == this.id ? this.opponentId : this.id;
        if (mon == Monster.SLAYER) {
            for (CastleID value : CastleID.values()) {
                if (state.getMonsters(value, playsOpponent).size() >= 4 || state.getMonsters(value, play).size() >= 4) {
                    continue;
                }
                int slayers = 0;
                for (Monster monster : state.getMonsters(value, play)) {
                    if (monster == Monster.SLAYER) {
                        slayers++;
                    }
                }
                int dragons = 0;
                for (Monster monster : state.getMonsters(value, playsOpponent)) {
                    if (monster == Monster.DRAGON) {
                        dragons++;
                    }
                }
                if (slayers < dragons) {
                    monsterVal = 7;
                }
            }
        }

        return monsterVal;
    }

    private CastleID randomOpponentDragonPlacement(GameState state) {
        if (state.getHidden(this.opponentId) != null) {
            return state.getHidden(this.opponentId);
        }

        Random rng = new Random();
        List<CastleID> possibleDragonCastles = new ArrayList<>();

        for (CastleID castleID : CastleID.values()) {
            if (state.getMonsters(castleID, this.id).size() < 4 || state.getMonsters(castleID, this.opponentId).size() < 4) {
                possibleDragonCastles.add(castleID);
            }
        }

        return possibleDragonCastles.get(rng.nextInt(possibleDragonCastles.size()));
    }

    private Move act(GameState state) {
        List<Move> legalMoves = GameRules.getLegalMoves(state);

        if(!(legalMoves.get(0) instanceof PlaceMonsterMove)) {
            legalMoves = new ArrayList<>();
            if(legalMoves.get(0) instanceof BuyMonsterMove) {
                legalMoves.add(getBuyMonsterForPlay(state, this.id));
            }
            else { // is a Respond Move
                BuyMonsterMove lastMove = (BuyMonsterMove) state.getLastMove();
                int price = lastMove.getPrice();
                Monster mon = lastMove.getMonster();
                legalMoves.add(getRespondForPlay(state, mon, price, this.id));
            }
        } 
        
        HashMap<Move, Integer> bestMoveCounts = new HashMap<Move, Integer>();

        for (int carlo = 0; carlo < MAX_CARLO; carlo++) {
            state.setDeck(cardCounter.createDeck());
            state.setHidden(opponentId, randomOpponentDragonPlacement(state));

            double alpha = Double.NEGATIVE_INFINITY;
            double beta = Double.POSITIVE_INFINITY;

            Move bestMove = legalMoves.get(0);
            double bestScore = alpha;
            for (Move move : legalMoves) {
                List<Monster> backupDeck = new ArrayList(state.getDeck()); // Shallow-copy!

                GameState nextState = GameRules.makeMove(state, move);
                double moveScore = 0;
                if (move instanceof BuyMonsterMove) {
                    moveScore = minimize(nextState, alpha, beta, MAX_DEPTH);
                } else if (move instanceof RespondMove) {
                    RespondMove respondMove = (RespondMove) move;
                    if (respondMove.isPass()) {
                        moveScore = maximize(nextState, alpha, beta, MAX_DEPTH);
                    } else {
                        moveScore = minimize(nextState, alpha, beta, MAX_DEPTH);
                    }
                } else if (move instanceof PlaceMonsterMove) {
                    moveScore = minimize(nextState, alpha, beta, MAX_DEPTH);
                }

                state.setDeck(backupDeck); // Because GameRules.makeMove modifies the deck (state copy constructor is shallow)

                // System.out.println(moveScore);
                if (moveScore > bestScore) {
                    alpha = moveScore;
                    bestScore = moveScore;
                    bestMove = move;
                }
            }

            Integer oldCount = bestMoveCounts.getOrDefault(bestMove, 0);
            bestMoveCounts.put(bestMove, oldCount + 1);
        }

        // Select move with most wins
        int bestWins = -1;
        Move bestMove = null;
        for (Move move : bestMoveCounts.keySet()) {
            if (bestMoveCounts.get(move) > bestWins) {
                bestMove = move;
                bestWins = bestMoveCounts.get(move);
            }
        }

        return bestMove;
    }

    private double minimize(GameState state, double alpha, double beta, int depthRemaining) {
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
        if (depthRemaining < 0 || state.isGameOver()) {
            return evaluateState(state);
        }

        List<Move> legalMoves = GameRules.getLegalMoves(state);

        if(!(legalMoves.get(0) instanceof PlaceMonsterMove)) {
            legalMoves = new ArrayList<>();
            if(legalMoves.get(0) instanceof BuyMonsterMove) {
                legalMoves.add(getBuyMonsterForPlay(state, this.opponentId));
            }
            else { // is a Respond Move
                BuyMonsterMove lastMove = (BuyMonsterMove) state.getLastMove();
                int price = lastMove.getPrice();
                Monster mon = lastMove.getMonster();
                legalMoves.add(getRespondForPlay(state, mon, price, this.opponentId));
            }
        }

        double bestScore = beta; // MIN = beta
        for (Move move : legalMoves) {
            List<Monster> backupDeck = new ArrayList(state.getDeck()); // Shallow-copy!

            GameState nextState = GameRules.makeMove(state, move);
            double moveScore = 0;
            if (move instanceof BuyMonsterMove) {
                moveScore = maximize(nextState, alpha, beta, depthRemaining - 1); // MIN = mAXmimize()
            } else if (move instanceof RespondMove) {
                RespondMove respondMove = (RespondMove) move;
                if (respondMove.isPass()) {
                    moveScore = minimize(nextState, alpha, beta, depthRemaining - 1); // MIN = mINimize()
                } else {
                    moveScore = maximize(nextState, alpha, beta, depthRemaining - 1); // MIN = mAXimize()
                }
            } else if (move instanceof PlaceMonsterMove) {
                moveScore = maximize(nextState, alpha, beta, depthRemaining - 1); // MIN = maximize()
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

    private double maximize(GameState state, double alpha, double beta, int depthRemaining) {
        if (depthRemaining < 0 || state.isGameOver() ) {
            return evaluateState(state);
        }

        List<Move> legalMoves = GameRules.getLegalMoves(state);

        if(!(legalMoves.get(0) instanceof PlaceMonsterMove)) {
            legalMoves = new ArrayList<>();
            if(legalMoves.get(0) instanceof BuyMonsterMove) {
                legalMoves.add(getBuyMonsterForPlay(state, this.id));
            }
            else { // is a Respond Move
                BuyMonsterMove lastMove = (BuyMonsterMove) state.getLastMove();
                int price = lastMove.getPrice();
                Monster mon = lastMove.getMonster();
                legalMoves.add(getRespondForPlay(state, mon, price, this.id));
            }
        }

        double bestScore = alpha; // MIN = beta
        for (Move move : legalMoves) {
            List<Monster> backupDeck = new ArrayList(state.getDeck()); // Shallow-copy!

            GameState nextState = GameRules.makeMove(state, move);
            double moveScore = 0;
            if (move instanceof BuyMonsterMove) {
                moveScore = minimize(nextState, alpha, beta, depthRemaining - 1); // MIN = mAXmimize()
            } else if (move instanceof RespondMove) {
                RespondMove respondMove = (RespondMove) move;
                if (respondMove.isPass()) {
                    moveScore = maximize(nextState, alpha, beta, depthRemaining - 1); // MIN = mINimize()
                } else {
                    moveScore = minimize(nextState, alpha, beta, depthRemaining - 1); // MIN = mAXimize()
                }
            } else if (move instanceof PlaceMonsterMove) {
                moveScore = minimize(nextState, alpha, beta, depthRemaining - 1); // MIN = maximize()
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
                for (Monster monster : state.getMonsters(castleID, this.opponentId)) {
                    opponentTotal += monster.value;
                }

                double winningProbabilityHueristic;
                if (ourTotal + opponentTotal == 0) {
                    winningProbabilityHueristic = 0.5;
                } else {
                    winningProbabilityHueristic = ourTotal / (ourTotal + opponentTotal);
                }

                score += winningProbabilityHueristic;
            }
        }

        return score;
    }

}
