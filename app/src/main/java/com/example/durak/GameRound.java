package com.example.durak;

import java.util.ArrayList;
import java.util.Collections;

public class GameRound {
    private ArrayList<String> remainingCards;
    private ArrayList<String> computer;
    private ArrayList<String> player;
    private ArrayList<String> activeCompCards;
    private ArrayList<String> activeHumCards;
    private String trump;
    private char trumpSuit;
    private boolean isHumAttacker;

    GameRound() {
        String cards = "s06 s07 s08 s09 s10 s11 s12 s13 s14 h06 h07 h08 h09 h10 h11 h12 h13 h14 " +
                "d06 d07 d08 d09 d10 d11 d12 d13 d14 c06 c07 c08 c09 c10 c11 c12 c13 c14";
        remainingCards = new ArrayList<>();
        activeCompCards = new ArrayList<>(); activeHumCards = new ArrayList<>();
        Collections.addAll(remainingCards, cards.split(" "));
        Collections.shuffle(remainingCards);
        computer = firstSixCards(); player = firstSixCards();
        int ind = -1;
        do {
            ind++;
            trump = remainingCards.get(ind);
        } while (trump.equals("s14") || trump.equals("h14") ||
                trump.equals("d14") || trump.equals("c14"));
        remainingCards.remove(ind); remainingCards.add(trump);
        trumpSuit = trump.charAt(0);
        int minCompTrump = minTrump(computer, 15);
        isHumAttacker = minCompTrump != minTrump(player, minCompTrump);
    }

    private ArrayList<String> firstSixCards() {
        ArrayList<String> res = new ArrayList<>();
        for (int i = 0; i < 6; i++) {
            res.add(remainingCards.remove(0));
        }
        return res;
    }

    private void refillCompCards() {
        int n = 6 - computer.size();
        for (int i = 0; i < n; i++) {
            if (remainingCards.size() == 0) return;
            else computer.add(remainingCards.remove(0));
        }
    }

    private void refillHumCards() {
        int n = 6 - player.size();
        for (int i = 0; i < n; i++) {
            if (remainingCards.size() == 0) return;
            else player.add(remainingCards.remove(0));
        }
    }

    private int minTrump(ArrayList<String> cards, int currMinTrump) {
        for (int i = 0; i < 6; i++) {
            String el = cards.get(i);
            int weight = Integer.parseInt(el.substring(1));
            if (el.charAt(0) == trumpSuit && weight < currMinTrump) currMinTrump = weight;
        }
        return currMinTrump;
    }

    public void computerAttacks() {
        activeCompCards.clear(); activeHumCards.clear();
        for (int i = 0; i < computer.size() - 1; i++) {
            String card1 = computer.get(i);
            String card1Weight = card1.substring(1);
            for (int j = i + 1; j < computer.size(); j++) {
                String card2 = computer.get(j);
                if (card1Weight.equals(card2.substring(1))) {
                    activeCompCards.add(card1); activeCompCards.add(card2);
                    computer.remove(card1); computer.remove(card2);
                    return;
                }
            }
        }
        int ind = (int) (Math.random() * computer.size());
        activeCompCards.add(computer.remove(ind));
    }

    private void computerDefends() {
        for (int i = 0; i < activeHumCards.size(); i++) {
            String card = computerMove(activeHumCards.get(i));
            if (card == null) {
                computer.addAll(activeHumCards);
                activeHumCards.clear();
                refillCompCards(); refillHumCards();
                return;
            }
            activeCompCards.add(card);
        }
        computer.removeAll(activeCompCards);
        activeHumCards.clear();
        refillCompCards(); refillHumCards();
        isHumAttacker = false;
    }

    private String computerMove(String humCard) {
        for (int i = 0; i < computer.size(); i++) {
            String card = computer.get(i);
            if (checkCardSelection(humCard, card)) return card;
        }
        return null;
    }

    public boolean checkCardSelection(String attackCard, String defendCard) {
        boolean isDefendCardTrump = defendCard.charAt(0) == trumpSuit;
        boolean isAttackCardTrump = attackCard.charAt(0) == trumpSuit;
        boolean isDefendCardBigger = Integer.parseInt(
                defendCard.substring(1)) > Integer.parseInt(attackCard.substring(1)
        );
        if (!isAttackCardTrump && !isDefendCardTrump &&
                (defendCard.charAt(0) != attackCard.charAt(0) || !isDefendCardBigger)) return false;
        return !isAttackCardTrump || (isDefendCardTrump && isDefendCardBigger);
    }

    public void humAttacks() {
        activeCompCards.clear();
        player.removeAll(activeHumCards);
        computerDefends();
    }

    public void humPickUpCards() {
        player.addAll(activeCompCards);
        activeCompCards.clear(); activeHumCards.clear();
        refillCompCards(); refillHumCards();
    }

    public void humDefended() {
        player.removeAll(activeHumCards);
        activeCompCards.clear(); activeHumCards.clear();
        refillCompCards(); refillHumCards();
        isHumAttacker = true;
    }

    public int whoWon() {
        if (player.size() == 0) {
            if (computer.size() == 0) return 0;
            else return 1;
        } if (computer.size() == 0) return 2;
        return -1;
    }

    public boolean checkCardsWeight(String firstCard, String secondCard) {
        return Integer.parseInt(firstCard.substring(1)) == Integer.parseInt(secondCard.substring(1));
    }

    public ArrayList<String> humCards() {
        return player;
    }

    public boolean isHumAttacker() {
        return isHumAttacker;
    }

    public ArrayList<String> getActiveCompCards() {
        return activeCompCards;
    }

    public String getTrump() {
        return trump;
    }

    public int compSize() {
        return computer.size();
    }

    public int remainingCardsSize() {
        return remainingCards.size();
    }

    public int activeHumCardsSize() {
        return activeHumCards.size();
    }

    public void addActiveHumCard(String card) {
        activeHumCards.add(card);
    }

    public void removeActiveHumCard(String card) {
        activeHumCards.remove(card);
    }

    public char getTrumpSuit() {
        return trumpSuit;
    }
}
