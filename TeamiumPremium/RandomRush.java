package TeamiumPremium;

import ProjectTwoEngine.*;

import java.util.Random;
import java.util.List;

/*

Strategy: rush one castle to focus on the two remaining castles
Perhaps put a dragon slayer on the remaining castle where our dragon
is not

*/

public class RandomRush implements Player {
    Random rand;
    PlayerID play;
    PlayerID opponent;

    CastleID ourDragonCastle;

    CastleID rushCastle;

    //This function is called when the game starts
    public void begin(GameState initState) {
        // learn where our secret dragon is
        // and decide which castle to rush
        // right from the get-go
	    rand = new Random();
        this.play = (initState.getHidden(PlayerID.TOP) == null) ? PlayerID.BOT : PlayerID.TOP;
        this.opponent = (this.play == PlayerID.TOP) ? PlayerID.BOT : PlayerID.TOP;
        this.ourDragonCastle = initState.getHidden(this.play);

        int i = 0;
        this.rushCastle = CastleID.values()[i];
        while (this.rushCastle == this.ourDragonCastle) {
            i++;
            this.rushCastle = CastleID.values()[i];
        }

        System.out.println("I am player " + this.play + ", and my dragon is in " + this.ourDragonCastle + ".");
    }

    //This function is called when the player must select a monster to buy
    public BuyMonsterMove getBuyMonster(GameState state) {
        List<Monster> monsters = state.getPublicMonsters();

        Monster buyMonster = monsters.get(0);

        // choose the cheapest monster
        if (state.getCastleWon(this.rushCastle) == null) {
            Monster cheapestMonster = monsters.get(0);
            for (Monster monster: monsters) {
                if (cheapestMonster.value > monster.value) cheapestMonster = monster;
            }
            buyMonster = cheapestMonster;
        }
        else {
            for (Monster monster: monsters) {
                if (buyMonster.value < monster.value) buyMonster = monster;
            }
        }
        

        System.out.println(this.play + ": I am trying to buy a " + buyMonster + ", which has a value of " + buyMonster.value + ".");

        // propose buying monster at 1/2 its value
        int price = (int) Math.ceil((float) buyMonster.value / 2f);
        price = Math.min(price, state.getCoins(this.play));

        if (state.getCoins(this.play) > state.getCoins(this.opponent) + 3) {
            price = state.getCoins(this.opponent) + 1;
        }

        BuyMonsterMove move = new BuyMonsterMove(this.play, price, buyMonster);
        System.out.println(this.play + ": I decided to try to buy it for " + price + " coins.");
        if (!GameRules.isLegalMove(state, move)) System.out.println("Concede.");
        return move;
    }

    //This function is called at the start of your opponent's turn
    public void startOppTurn(GameState state){
        System.out.println(this.play + ": " + this.opponent + "'s turn is starting.");
        return;
        // what would we want to do here
    }

    //This function is called when your opponent tried to buy a monster
    //If you steal, you will get the chosen monster
    //... but hand your opponent the price in coins
    public RespondMove getRespond(GameState state, Monster monster, int price){
        System.out.println(this.play + ": I am being asked whether I want to steal " + monster + " for " + price  + " coins.");
        
        int myPrice = (int) Math.floor(monster.value / 2);
        if (price > state.getCoins(this.play)) {
            price = state.getCoins(this.play);
        }

        boolean steal = (myPrice > price || state.getCoins(this.play) > state.getCoins(this.opponent) + 3)
                        && (state.getCoins(this.play) >= price);

        if (steal) System.out.println(this.play + ": I decided to steal, and I have " + state.getCoins(this.play));
        else System.out.println(this.play + ": I decided not to steal.");
    
        return new RespondMove(this.play, !steal, monster);
    }
    
    //This function is called when the opponent pays the price to steal
    // ... the monster chosen by the player
    public void stolenMonster(GameState state){

    }

    //This function is called when the player successfully buys a monster
    //... and needs to place the monster at a castle
    public PlaceMonsterMove getPlace(GameState state, Monster monster){
        System.out.println(this.play + ": I am being asked where I want to put my new " + monster + ".");
        
        PlaceMonsterMove move;

        if (state.getCastleWon(this.rushCastle) == null) {
            // our rush castle is still in play; put the new monster there
            move = new PlaceMonsterMove(this.play, this.rushCastle, monster);
        }
        
        else {
            List<Move> moves = GameRules.getLegalMoves(state);
            int i = rand.nextInt(moves.size());
	        move = (PlaceMonsterMove) moves.get(i);
        }

        if (!GameRules.isLegalMove(state, move)) System.out.println("Concede.");
        
        return move;
    }

    public String getPlayName() {
	    return "Random Rush";
    }
}

   
