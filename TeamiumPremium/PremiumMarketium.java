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

    int MAX_DEPTH = 15;
    int MAX_CARLO = 30;

    public PremiumMarketium() {
        this.cardCounter = new CardCounter();
    }

    @Override
    public void begin(GameState init_state) {
        // id and opponentId are based on the hidden information
        //System.out.println("BEGIN METHOD: TOP: " + init_state.getHidden(PlayerID.TOP) + " BOT: " + init_state.getHidden(PlayerID.BOT));

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
        //System.out.println(this.id + " " + this.opponentId);
        this.cardCounter.cardDrawn(state.getNextMonster());
        return getBuyMonsterForPlay(state, this.id);
    }

    public BuyMonsterMove getBuyMonsterForPlay(GameState state, PlayerID play) {
        PlayerID playsOpponent = (play == this.id) ? this.opponentId : this.id;
        Monster bestCard = null;
        float bestValue = -1;
        Monster lowestNonSlayer = Monster.DRAGON; // Can be bad if it is a high value monster
        for (Monster publicMonster : state.getPublicMonsters()) {
            if (!publicMonster.equals(Monster.SLAYER) && publicMonster.value < lowestNonSlayer.value) {
                lowestNonSlayer = publicMonster;
            }
            if (evaluateCard(state, publicMonster, play) > bestValue) {
                bestCard = publicMonster;
                bestValue = evaluateCard(state, publicMonster, play);
            }
        }

        boolean playHasSignificantLead = state.getCoins(play) >= state.getCoins(playsOpponent) + 3;

        int priceForOpponent = state.getCoins(playsOpponent);
        if (!playHasSignificantLead) priceForOpponent++;

        int price = (int) Math.min(bestValue, Math.min(priceForOpponent, state.getCoins(play)));

//        System.out.println("CORRECT PLAYER: " + GameRules.getLegalMoves(state).get(0).getPlayer());
//        System.out.println("PLAYER: " + play);
//        System.out.println(state);
        if (state.getCoins(play) >= price) {
            BuyMonsterMove m = new BuyMonsterMove(play, price, bestCard);
//            System.out.println("IS LEGAL : " + GameRules.isLegalMove(state, m));
//            System.out.println("price: " + price + " card: " + bestCard.name);
            return m;
        } else {
            BuyMonsterMove m = new BuyMonsterMove(play, 1, lowestNonSlayer);
//            System.out.println("IS LEGAL : " + GameRules.isLegalMove(state, m));
//            System.out.println("price: " + price + " card: " + lowestNonSlayer.name);
            return m;
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
        PlayerID playsOpponent = (play == this.id) ? this.opponentId : this.id;
        boolean steal
                = evaluateCard(state, mon, play) >= price * 2
                && (evaluateCard(state, mon, play) < evaluateCard(state, mon, playsOpponent)
                //&& evaluateCard(state, mon, play) > evaluateCard(state, mon, playsOpponent)
                || evaluateCard(state, mon, playsOpponent) > price)
                //&& evaluateCard(state, mon, play) > price

                //&& state.getCoins(play) >= state.getCoins(playsOpponent);
                && state.getCoins(play) >= price;

        // ways to steal
        // eval(opp) > price    // punish opponent for quick deals
        // eval(us) > price     // standard steal
        // eval(us) > eval(opp) // picky stealing
        // eval(us) < eval(opp) // sabotage focused
        //System.out.println("Steal Eval: " + evaluateCard(state, mon, play) + " | " + evaluateCard(state, mon, playsOpponent));
        return new RespondMove(play, !steal, mon);
    }

    @Override
    public void stolenMonster(GameState state) {

    }

    @Override
    public PlaceMonsterMove getPlace(GameState state, Monster mon) {
        return (PlaceMonsterMove) act(state);
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
        PlayerID playsOpponent = (play == this.id) ? this.opponentId : this.id;
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
            if (state.getMonsters(castleID, this.id).size() < 4 && state.getMonsters(castleID, this.opponentId).size() < 4) {
                possibleDragonCastles.add(castleID);
            }
        }

        System.out.println("Possible Drag Castles: " + possibleDragonCastles);

        return possibleDragonCastles.get(rng.nextInt(possibleDragonCastles.size()));
    }

    private Move act(GameState state) {
        List<Move> legalMoves = GameRules.getLegalMoves(state);

        if (!(legalMoves.get(0) instanceof PlaceMonsterMove)) {
            legalMoves = new ArrayList<>();
            if (legalMoves.get(0) instanceof BuyMonsterMove) {
                legalMoves.add(getBuyMonsterForPlay(state, this.id));
            } else { // is a Respond Move
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

        Move firstMove = legalMoves.get(0);
        if (!(firstMove instanceof PlaceMonsterMove)) {
            legalMoves = new ArrayList<>();
            if (firstMove instanceof BuyMonsterMove) {
                legalMoves.add(getBuyMonsterForPlay(state, this.opponentId));
            } else { // is a Respond Move
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
                if (this.id == GameRules.getLegalMoves(nextState).get(0).getPlayer()) {
                    moveScore = maximize(nextState, alpha, beta, depthRemaining - 1); // MIN = maximize()
                } else {
                    moveScore = minimize(nextState, alpha, beta, depthRemaining - 1); // MIN = minimize()
                }
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
        if (depthRemaining < 0 || state.isGameOver()) {
            return evaluateState(state);
        }

        List<Move> legalMoves = GameRules.getLegalMoves(state);

        Move firstMove = legalMoves.get(0);

        if (!(firstMove instanceof PlaceMonsterMove)) {
            legalMoves = new ArrayList<>();
            if (firstMove instanceof BuyMonsterMove) {
                legalMoves.add(getBuyMonsterForPlay(state, this.id));
            } else { // is a Respond Move
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
                if (this.id == GameRules.getLegalMoves(nextState).get(0).getPlayer()) {
                    moveScore = maximize(nextState, alpha, beta, depthRemaining - 1); // MIN = maximize()
                } else {
                    moveScore = minimize(nextState, alpha, beta, depthRemaining - 1); // MIN = minimize()
                }
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
                double opponentTotal = 0;
                double opponentDragons = 0, opponentSlayers = 0, ourDragons = 0, ourSlayers = 0;

                List<Monster> ourMonsters = new ArrayList<>(state.getMonsters(castleID, this.id));
                List<Monster> opponentMonsters = new ArrayList<>(state.getMonsters(castleID, this.opponentId));

                if (state.getHidden(this.id) == castleID) {
                    ourMonsters.add(Monster.DRAGON);
                }
                if (state.getHidden(this.opponentId) == castleID) {
                    opponentMonsters.add(Monster.DRAGON);
                }

                for (Monster monster : ourMonsters) {
                    if (monster == Monster.DRAGON) {
                        ourDragons++;
                    } else if (monster == Monster.SLAYER) {
                        ourSlayers++;
                    } else {
                        ourTotal += monster.value;
                    }
                }
                for (Monster monster : opponentMonsters) {
                    if (monster == Monster.DRAGON) {
                        opponentDragons++;
                    } else if (monster == Monster.SLAYER) {
                        opponentSlayers++;
                    } else {
                        opponentTotal += monster.value;
                    }
                }
                
                ourTotal += 6 * (ourDragons - opponentSlayers) + ourSlayers;
                opponentTotal += 6 * (opponentDragons - ourSlayers) + opponentSlayers;

                double winningProbabilityHueristic;
                if (ourTotal + opponentTotal == 0) {
                    winningProbabilityHueristic = 0.5;
                } else {
                    winningProbabilityHueristic = (1 + ourTotal) / (ourTotal + opponentTotal + 1);
                }

                score += winningProbabilityHueristic;
            }
        }

        return score;
    }

}
