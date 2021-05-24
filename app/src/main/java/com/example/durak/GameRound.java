package com.example.durak;

import android.content.Context;
import android.content.res.Resources;
import android.util.Log;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.Arrays;

public class GameRound {

    Context context;
    ArrayList<Integer> remainingCards;
    ArrayList<Integer> computer;
    Integer trumpID;
    String trump;
    Integer selectedCard;

    GameRound(Context context, ArrayList<Integer> remainingCards) {
        this.context = context;
        this.remainingCards = remainingCards;
        for (int i = 0; i < remainingCards.size() / 2; i++) {
            int ind = (int) (Math.random() * remainingCards.size());
            Integer t = remainingCards.get(i);
            remainingCards.set(i, remainingCards.get(ind));
            remainingCards.set(ind, t);
        }
        computer = firstSixCards();
        int trumpInd = -1;
        do {
            trumpInd++;
            trumpID = remainingCards.get(trumpInd);
        } while (trumpID == R.drawable.s14 || trumpID == R.drawable.h14 ||
                        trumpID == R.drawable.d14 || trumpID == R.drawable.c14);
        trump = context.getResources().getResourceEntryName(trumpID).substring(0, 1);
        remainingCards.remove(trumpInd); remainingCards.add(trumpID);
    }

    public ArrayList<Integer> firstSixCards() {
        ArrayList<Integer> res = new ArrayList<>();
        for (int i = 0; i < 6; i++) {
            res.add(remainingCards.get(0));
            remainingCards.remove(0);
        }
        return res;
    }

    public ArrayList<Integer> computerFirstMove() {
        ArrayList<Integer> res = new ArrayList<>();
        for (int i = 0; i < computer.size() - 1; i++) {
            String card1 = context.getResources().getResourceEntryName(computer.get(i)).substring(1);
            for (int j = i + 1; j < computer.size(); j++) {
                String card2 = context.getResources().getResourceEntryName(computer.get(j)).substring(1);
                if (card1.equals(card2)) {
                    res.add(computer.get(i)); res.add(computer.get(j));
                    computer.remove(res.get(0)); computer.remove(res.get(1));
                    return res;
                }
            }
        }
        int ind = (int) (Math.random() * computer.size());
        res.add(computer.get(ind));
        computer.remove(ind);
        return res;
    }

    public boolean checkCardSelection(Integer compCard, Integer humCard) {
        String playerCard = context.getResources().getResourceEntryName(humCard);
        String enemyCard = context.getResources().getResourceEntryName(compCard);
        boolean isPlayerCardTrump = playerCard.substring(0, 1).equals(trump);
        boolean isEnemyCardTrump = enemyCard.substring(0, 1).equals(trump);
        boolean isPlayerCardBigger = Integer.parseInt(
                playerCard.substring(1)) > Integer.parseInt(enemyCard.substring(1)
        );
        if (!isEnemyCardTrump && !isPlayerCardTrump &&
                (!playerCard.substring(0, 1).equals(enemyCard.substring(0, 1)) || !isPlayerCardBigger)) {
            return false;
        }
        return !isEnemyCardTrump || (isPlayerCardTrump && isPlayerCardBigger);
    }

    public Integer computerMove(Integer playerCard) {
        for (int i = 0; i < computer.size(); i++) {
            Integer card = computer.get(i);
            if (checkCardSelection(playerCard, card)) return card;
        }
        return null;
    }

    public boolean sameWeight(Integer card1, Integer card2) {
        String weight1 = context.getResources().getResourceEntryName(card1).substring(1);
        String weight2 = context.getResources().getResourceEntryName(card2).substring(1);
        return weight1.equals(weight2);
    }
}
