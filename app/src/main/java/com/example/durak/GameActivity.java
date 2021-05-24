package com.example.durak;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.ViewParent;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class GameActivity extends AppCompatActivity implements RecyclerViewAdapter.ItemClickListener {

    private RecyclerViewAdapter adapter;
    private RecyclerViewAdapter adapterActive;
    private GameRound gameRound;
    private ImageView buttonContinue;
    private TextView textView;
    private boolean isPlayerMoveFirst;
    private ArrayList<Integer> activeCards;
    private View selectedCardView;
    private int selectedCardPos;
    private int padding;

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
        TypedArray cards = getResources().obtainTypedArray(R.array.listOfCards);
        ArrayList<Integer> remainingCards = new ArrayList<>();
        for (int i = 0; i < cards.length(); i++) {
            remainingCards.add(cards.getResourceId(i, -1));
        }
        cards.recycle();
        padding = (int) (5 * getResources().getDisplayMetrics().density + 0.5f);
        buttonContinue = findViewById(R.id.imageViewContinue);
        textView = findViewById(R.id.textView);
        activeCards = new ArrayList<>();

        gameRound = new GameRound(this, remainingCards);

        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new RecyclerViewAdapter(this, gameRound.firstSixCards(), 0);
        adapter.setClickListener(this);
        recyclerView.setAdapter(adapter);

        RecyclerView recyclerViewActive = findViewById(R.id.recyclerViewActive);
        recyclerViewActive.setLayoutManager(layoutManagerActive);
        adapterActive = new RecyclerViewAdapter(this, new ArrayList<>(), 1);
        adapterActive.setClickListener(this);
        recyclerViewActive.setAdapter(adapterActive);

        ImageView imageViewTrump = findViewById(R.id.imageViewTrump);
        imageViewTrump.setImageResource(gameRound.trumpID);
        String counting = "У Противника - " + gameRound.computer.size() +
                ", Осталось - " + gameRound.remainingCards.size();
        textView.setText(counting);
        int firstMove = (int) (Math.random() * 2);
        if (firstMove == 0) isPlayerMoveFirst = true;
        else {
            isPlayerMoveFirst = false;
            computerStart();
        }
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
        if (gameRound.selectedCard != card) {
            if (gameRound.selectedCard != null) {
                selectedCardView.setPadding(padding, padding, padding, padding);
            }
            gameRound.selectedCard = card;
            selectedCardView = view;
            selectedCardPos = position;
            view.setPadding(0, 0, 0, 0);
        } else {
            if (isPlayerMoveFirst) {
                if (adapterActive.size() > 1) {
                    Toast toast = Toast.makeText(
                            this, "Нельзя выбирать больше 2 карт за раз", Toast.LENGTH_SHORT
                    );
                    toast.show();
                } else {
                    if (adapterActive.size() != 0) {
                        if (!gameRound.sameWeight(adapterActive.selectCard(0), card)) {
                            Toast toast = Toast.makeText(
                                    this,
                                    "Нельзя выбирать карты разных достоинств",
                                    Toast.LENGTH_SHORT
                            );
                            toast.show();
                            return;
                        }
                    }
                    adapterActive.addCard(card);
                    adapter.removeCard(position);
                    view.setPadding(padding, padding, padding, padding);
                    gameRound.selectedCard = null;
                    selectedCardView = null;
                    selectedCardPos = -1;
                    buttonContinue.clearColorFilter();
                    buttonContinue.setClickable(true);
                }
            }
        }
    }

    public void onClickActiveCards(View view, int position) {
        if (isPlayerMoveFirst) {
            Integer card = adapterActive.selectCard(position);
            adapterActive.removeCard(position);
            adapter.addCard(card);
        } else {
            if (gameRound.selectedCard != null) {
                if (gameRound.checkCardSelection(adapterActive.selectCard(position), gameRound.selectedCard)) {
                    selectedCardView.setPadding(padding, padding, padding, padding);
                    adapter.removeCard(selectedCardPos);
                    adapterActive.addCardTop(position, gameRound.selectedCard);
                    activeCards.add(gameRound.selectedCard);
                    view.setClickable(false);
                    gameRound.selectedCard = null;
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
        ArrayList<Integer> cardsFromComputer = gameRound.computerFirstMove();
        for (int i = 0; i < cardsFromComputer.size(); i++) {
            if (adapter.size() > i) adapterActive.addCard(cardsFromComputer.get(i));
        }
        String counting = "У Противника - " + gameRound.computer.size() +
                ", Осталось - " + gameRound.remainingCards.size();
        textView.setText(counting);
    }

    public void onClickContinue(View view) throws InterruptedException {
        if (!isPlayerMoveFirst) {
            playerContinues(view);
        } else {
            compContinues(view);
        }
    }

    public void playerContinues(View view) throws InterruptedException {
        loosingOrWinning();
        buttonContinue.setColorFilter(
                Color.rgb(123, 123, 123), android.graphics.PorterDuff.Mode.MULTIPLY
        );
        buttonContinue.setClickable(false);
        ArrayList<Integer> compActiveCards = adapterActive.getAllItems();
        if (activeCards.size() != compActiveCards.size()) {
            adapter.addAll(activeCards);
            adapter.addAll(compActiveCards);
        }
        adapterActive.removeAll();
        int size = adapter.size();
        for (int i = 0; i < 6 - size; i++) {
            if (!gameRound.remainingCards.isEmpty()) {
                adapter.addCard(gameRound.remainingCards.get(0));
                gameRound.remainingCards.remove(0);
            }
        }
        String counting = "У Противника - " + gameRound.computer.size() +
                ", Осталось - " + gameRound.remainingCards.size();
        textView.setText(counting);
        activeCards = new ArrayList<>();
        isPlayerMoveFirst = true;
    }

    public void compContinues(View view) throws InterruptedException {
        ArrayList<Integer> playerCards = adapterActive.getAllItems();
        for (int i = 0; i < playerCards.size(); i++) {
            Integer card = gameRound.computerMove(playerCards.get(i));
            if (card != null) {
                activeCards.add(card);
                adapterActive.addCardTop(i, card);
                gameRound.computer.remove(card);
            } else {
                gameRound.computer.addAll(playerCards);
                gameRound.computer.addAll(activeCards);
                break;
            }
        }
        adapterActive.removeAll();
        int size = gameRound.computer.size();
        for (int i = 0; i < 6 - size; i++) {
            if (!gameRound.remainingCards.isEmpty()) {
                gameRound.computer.add(gameRound.remainingCards.get(0));
                gameRound.remainingCards.remove(0);
            }
        }
        activeCards = new ArrayList<>();
        isPlayerMoveFirst = false;
        String counting = "У Противника - " + gameRound.computer.size() +
                ", Осталось - " + gameRound.remainingCards.size();
        textView.setText(counting);
        loosingOrWinning();
        computerStart();
    }

    public void loosingOrWinning() throws InterruptedException {
        if (adapter.size() == 0) {
            showToast("Поздравляем с победой!");
        } else if (gameRound.computer.size() == 0) {
            showToast("К сожалению, вы проиграли");
        }
    }

    public void showToast(String text) throws InterruptedException {
        Toast toast = Toast.makeText(this, text, Toast.LENGTH_SHORT);
        toast.show();
        Thread.sleep(2000);
        Intent intent = new Intent(GameActivity.this, MainActivity.class);
        startActivity(intent);
    }
}