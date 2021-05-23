package com.example.durak;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder>{

    private ArrayList<Integer> mData;
    private LayoutInflater mInflater;
    private ItemClickListener mClickListener;
    private int type;

    RecyclerViewAdapter(Context context, ArrayList<Integer> data, int type) {
        this.mInflater = LayoutInflater.from(context);
        this.mData = data;
        this.type = type;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        if (type == 0) view = mInflater.inflate(R.layout.layout_listitem, parent, false);
        else view = mInflater.inflate(R.layout.layout_activecard, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        int card = mData.get(position);
        holder.myImageView.setImageResource(card);
        holder.myImageView.clearColorFilter();
        if (type == 1) {
            holder.myImageViewTop.setImageDrawable(null);
            holder.myImageViewTop.clearColorFilter();
            holder.itemView.setClickable(true);
        }
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position, List<Object> payloads) {
        if (!payloads.isEmpty()) {
            holder.myImageViewTop.setImageResource((Integer)payloads.get(0));
            holder.myImageView.setColorFilter(
                    Color.rgb(123, 123, 123), android.graphics.PorterDuff.Mode.MULTIPLY
            );
            holder.myImageViewTop.setColorFilter(
                    Color.rgb(123, 123, 123), android.graphics.PorterDuff.Mode.MULTIPLY
            );
        } else {
            super.onBindViewHolder(holder, position, payloads);
        }
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView myImageView;
        ImageView myImageViewTop;

        ViewHolder(View itemView) {
            super(itemView);
            if (type == 0) {
                myImageView = itemView.findViewById(R.id.imageView);
            } else {
                myImageView = itemView.findViewById(R.id.imageViewActive);
                myImageViewTop = itemView.findViewById(R.id.imageViewActiveTop);
            }
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (mClickListener != null) mClickListener.onItemClick(view, getAdapterPosition());
        }
    }

    public void addCard(int item) {
        mData.add(item);
        notifyItemInserted(mData.size() - 1);
    }

    public void addCardTop(int position, int item) {
        notifyItemChanged(position, item);
    }

    public Integer selectCard(int position) {
        return mData.get(position);
    }

    public void removeCard(int position) {
        mData.remove(position);
        notifyItemRemoved(position);
    }

    public void removeAll() {
        while (mData.size() != 0) {
            removeCard(0);
        }
    }

    public void addAll(ArrayList<Integer> cards) {
        for (int i = 0; i < cards.size(); i++) addCard(cards.get(i));
    }



    ArrayList<Integer> getAllItems() {
        return mData;
    }

    int size() {
        return mData.size();
    }

    void setClickListener(ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }
}
