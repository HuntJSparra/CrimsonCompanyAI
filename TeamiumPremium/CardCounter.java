package TeamiumPremium;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import ProjectTwoEngine.Monster;

public class CardCounter {
    public HashMap<Monster, Integer> numCards;

    public CardCounter() {
        // Needs to be updated if Prof. Lepinski changes
        // DeckFactory.createDeck()
        numCards = new HashMap<Monster, Integer>();

        numCards.put(Monster.DRAGON, 2);
        numCards.put(Monster.SLAYER, 2);

        numCards.put(Monster.GRYPHON, 4);
        numCards.put(Monster.WOLF, 4);

        numCards.put(Monster.GIANT, 5);
        numCards.put(Monster.WARLION, 5);
    }

    // Deep copy constructor
    public CardCounter(CardCounter original) {
        numCards = (HashMap<Monster, Integer>)original.numCards.clone(); // Make sure shallow-copy works for Integer and enums
    }


    public void cardDrawn(Monster drawnMonster) {
        Integer currentValue = numCards.get(drawnMonster);
        numCards.put( drawnMonster, currentValue-1 );
    }

    public int getRemainingMonsterOfType(Monster monster) {
        return numCards.get(monster);
    }

    public List<Monster> createDeck() {
        List<Monster> deck = new ArrayList<Monster>();

        for (Monster monster : numCards.keySet()) {
            Integer monsterCount = numCards.get(monster);
            for (int i=0; i<monsterCount; i++) {
                deck.add(monster);
            }
        }

        Collections.shuffle(deck);

        return deck;
    }
}