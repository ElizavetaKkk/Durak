package com.example.durak;

import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;

public class GameRoundTest {
    GameRound gameRound;

    @Test
    public void checkCardsWeight() {
        gameRound = new GameRound();
        Assert.assertTrue(gameRound.checkCardsWeight("c09", "s09"));
        Assert.assertTrue(gameRound.checkCardsWeight("h14", "c14"));
        Assert.assertFalse(gameRound.checkCardsWeight("d11", "h12"));
        Assert.assertFalse(gameRound.checkCardsWeight("s07", "d13"));
    }

    @Test
    public void checkCardsSelection() {
        gameRound = new GameRound();
        ArrayList<Character> typeOfCards= new ArrayList<>();
        typeOfCards.add('c'); typeOfCards.add('s'); typeOfCards.add('h'); typeOfCards.add('d');
        Character trumpSuit = gameRound.getTrumpSuit();
        typeOfCards.remove(trumpSuit);
        Assert.assertTrue(gameRound.checkCardSelection("c09", "c11"));
        Assert.assertFalse(gameRound.checkCardSelection("s14", "s10"));
        Assert.assertFalse(gameRound.checkCardSelection(
                trumpSuit + "11", typeOfCards.get(0) + "12")
        );
        Assert.assertTrue(
                gameRound.checkCardSelection(trumpSuit + "08", trumpSuit + "10")
        );
        Assert.assertTrue(
                gameRound.checkCardSelection(typeOfCards.get(1) + "09", trumpSuit + "06")
        );
    }

    @Test
    public void computerAttacks() {
        for (int i = 0; i < 5; i++) {
            gameRound = new GameRound();
            gameRound.computerAttacks();
            ArrayList<String> activeCompCards = gameRound.getActiveCompCards();
            if (activeCompCards.size() == 2) {
                Assert.assertEquals(activeCompCards.get(0).substring(1), activeCompCards.get(1).substring(1));
            }
        }
    }
}
