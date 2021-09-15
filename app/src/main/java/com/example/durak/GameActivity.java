package com.example.durak;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.ViewParent;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class GameActivity extends AppCompatActivity implements RecyclerViewAdapter.ItemClickListener {

    private RecyclerViewAdapter adapter;
    private RecyclerViewAdapter adapterActive;
    private GameRound gameRound;
    private ImageView buttonContinue;
    private ImageView imageViewTrump;
    private TextView textView;
    private ArrayList<Integer> activeCards;
    private View selectedCardView;
    private Integer selectedCard;
    private int selectedCardPos;
    private int padding;
    private boolean hasTrumpBeenRemoved;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        LinearLayoutManager layoutManager = new LinearLayoutManager(
                this, LinearLayoutManager.HORIZONTAL, false
        );
        LinearLayoutManager layoutManagerActive = new LinearLayoutManager(
                this, LinearLayoutManager.HORIZONTAL, false
        );
        padding = (int) (5 * getResources().getDisplayMetrics().density + 0.5f);
        buttonContinue = findViewById(R.id.imageViewContinue);
        textView = findViewById(R.id.textView);
        activeCards = new ArrayList<>();

        gameRound = new GameRound();
        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(layoutManager);
        ArrayList<String> playerCards = gameRound.humCards();
        ArrayList<Integer> playerCardsImg = new ArrayList<>();
        for (int i = 0; i < playerCards.size(); i++) {
            playerCardsImg.add(
                    getResources().getIdentifier(playerCards.get(i), "drawable", getPackageName())
            );
        }
        adapter = new RecyclerViewAdapter(this, playerCardsImg, 0);
        adapter.setClickListener(this);
        recyclerView.setAdapter(adapter);

        RecyclerView recyclerViewActive = findViewById(R.id.recyclerViewActive);
        recyclerViewActive.setLayoutManager(layoutManagerActive);
        adapterActive = new RecyclerViewAdapter(this, new ArrayList<>(), 1);
        adapterActive.setClickListener(this);
        recyclerViewActive.setAdapter(adapterActive);

        imageViewTrump = findViewById(R.id.imageViewTrump);
        imageViewTrump.setImageResource(
                getResources().getIdentifier(gameRound.getTrump(), "drawable", getPackageName())
        );
        hasTrumpBeenRemoved = false;
        String counting = "У Противника - " + gameRound.compSize() +
                ", Осталось - " + gameRound.remainingCardsSize();
        textView.setText(counting);
        if (!gameRound.isHumAttacker()) computerStart();
        else lockButton();
    }

    @Override
    public void onItemClick(View view, int position) {
        ViewParent parent = view.getParent();
        if (((RecyclerView) parent).getId() == R.id.recyclerView) {
            onClickPlayerCards(view, position);
        } else {
            onClickActiveCards(view, position);
        }
    }

    public void onClickPlayerCards(View view, int position) {
        Integer card = adapter.selectCard(position);
        if (selectedCard != card) {
            if (selectedCard != null) {
                selectedCardView.setPadding(padding, padding, padding, padding);
            }
            selectedCard = card; selectedCardView = view; selectedCardPos = position;
            view.setPadding(0, 0, 0, 0);
        } else {
            if (gameRound.isHumAttacker()) {
                if (adapterActive.size() > 1) {
                    Toast toast = Toast.makeText(
                            this, "Нельзя выбирать больше 2 карт за раз", Toast.LENGTH_SHORT
                    );
                    toast.show();
                } else {
                    String cardName = getResources().getResourceEntryName(card);
                    if (adapterActive.size() != 0) {
                        if (!gameRound.checkCardsWeight(
                                getResources().getResourceEntryName(adapterActive.selectCard(0)),
                                cardName)) {
                            Toast toast = Toast.makeText(
                                    this,
                                    "Нельзя выбирать карты разных достоинств",
                                    Toast.LENGTH_SHORT
                            );
                            toast.show();
                            return;
                        }
                    }
                    gameRound.addActiveHumCard(cardName);
                    adapterActive.addCard(card);
                    adapter.removeCard(position);
                    view.setPadding(padding, padding, padding, padding);
                    selectedCard = null;
                    selectedCardView = null;
                    selectedCardPos = -1;
                    unlockButton();
                }
            }
        }
    }

    public void onClickActiveCards(View view, int position) {
        if (gameRound.isHumAttacker()) {
            Integer card = adapterActive.selectCard(position);
            adapterActive.removeCard(position);
            adapter.addCard(card);
            gameRound.removeActiveHumCard(getResources().getResourceEntryName(card));
            if (gameRound.activeHumCardsSize() == 0) lockButton();
        } else {
            if (selectedCard != null) {
                String selectedCardName = getResources().getResourceEntryName(selectedCard);
                if (gameRound.checkCardSelection(
                        getResources().getResourceEntryName(adapterActive.selectCard(position)),
                        selectedCardName)) {
                    gameRound.addActiveHumCard(selectedCardName);
                    selectedCardView.setPadding(padding, padding, padding, padding);
                    adapter.removeCard(selectedCardPos);
                    adapterActive.addCardTop(position, selectedCard);
                    activeCards.add(selectedCard);
                    view.setClickable(false);
                    selectedCard = null;
                    selectedCardView = null;
                    selectedCardPos = -1;
                } else {
                    Toast toast = Toast.makeText(
                            this,
                            "Данная карта не может отбить карту противника",
                            Toast.LENGTH_SHORT
                    );
                    toast.show();
                }
            }
        }
    }

    public void computerStart() {
        gameRound.computerAttacks();
        ArrayList<String> activeCompCards = gameRound.getActiveCompCards();
        for (int i = 0; i < activeCompCards.size(); i++) {
            if (adapter.size() > i) {
                adapterActive.addCard(getResources().getIdentifier(activeCompCards.get(i),
                                "drawable", getPackageName()));
            }
        }
        checkTrumpCard();
        String counting = "У Противника - " + gameRound.compSize() +
                ", Осталось - " + gameRound.remainingCardsSize();
        textView.setText(counting);
    }

    public void onClickContinue(View view) throws InterruptedException {
        if (gameRound.isHumAttacker()) {
            gameRound.humAttacks();
            compContinues(view);
        }
        else playerContinues(view);
    }

    public void playerContinues(View view) throws InterruptedException {
        ArrayList<Integer> compActiveCards = adapterActive.getAllItems();
        if (activeCards.size() != compActiveCards.size()) {
            gameRound.humPickUpCards();
        } else gameRound.humDefended();
        adapterActive.removeAll();
        loosingOrWinning();
        adapter.removeAll();
        ArrayList<String> humCards = gameRound.humCards();
        for (int i = 0; i < humCards.size(); i++) {
            adapter.addCard(getResources().getIdentifier(humCards.get(i),
                    "drawable", getPackageName()));
        }
        checkTrumpCard();
        String counting = "У Противника - " + gameRound.compSize() +
                ", Осталось - " + gameRound.remainingCardsSize();
        textView.setText(counting);
        activeCards = new ArrayList<>();
        if (!gameRound.isHumAttacker()) computerStart();
    }

    public void compContinues(View view) throws InterruptedException {
        adapterActive.removeAll();
        activeCards = new ArrayList<>();
        checkTrumpCard();
        String counting = "У Противника - " + gameRound.compSize() +
                ", Осталось - " + gameRound.remainingCardsSize();
        textView.setText(counting);
        loosingOrWinning();
        adapter.removeAll();
        ArrayList<String> humCards = gameRound.humCards();
        for (int i = 0; i < humCards.size(); i++) {
            adapter.addCard(getResources().getIdentifier(humCards.get(i),
                    "drawable", getPackageName()));
        }
        if (!gameRound.isHumAttacker()) computerStart();
        else lockButton();
    }

    private void loosingOrWinning() throws InterruptedException {
        int res = gameRound.whoWon();
        switch (res) {
            case 0:
                showToast("Ничья");
                break;
            case 1:
                showToast("Вы победили!");
                break;
            case 2:
                showToast("Вы проиграли");
                break;
        }
    }

    private void showToast(String text) throws InterruptedException {
        Toast toast = Toast.makeText(this, text, Toast.LENGTH_SHORT);
        toast.show();
        Thread.sleep(1500);
        finish();
    }

    private void lockButton() {
        buttonContinue.setColorFilter(
                Color.rgb(123, 123, 123), android.graphics.PorterDuff.Mode.MULTIPLY
        );
        buttonContinue.setClickable(false);
    }

    private void unlockButton() {
        buttonContinue.clearColorFilter();
        buttonContinue.setClickable(true);
    }

    public void onClickExit(View view) {
        finish();
    }

    private void checkTrumpCard() {
        if (!hasTrumpBeenRemoved && gameRound.remainingCardsSize() == 0) {
            imageViewTrump.setImageDrawable(null);
            hasTrumpBeenRemoved = true;
        }
    }
}