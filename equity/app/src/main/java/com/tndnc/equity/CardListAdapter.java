package com.tndnc.equity;

import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.CardView;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.TextView;

import com.tndnc.equity.models.Level;
import com.tndnc.equity.views.LevelButtonView;

import java.util.List;

public class CardListAdapter extends RecyclerView.Adapter<CardListAdapter.ViewHolder> {
    private GameApplication app;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    static class ViewHolder extends RecyclerView.ViewHolder {
        // each data is a card that holds a list of levels of a given size.
        CardView cardView;
        TextView levelSizeView, pageNumberView;
        RecyclerView levelListView;
        ViewHolder(CardView cardView, TextView levelSizeView, RecyclerView levelListView, TextView pageNumberView) {
            super(cardView);
            this.cardView = cardView;
            this.levelListView = levelListView;
            this.pageNumberView = pageNumberView;
            this.levelSizeView = levelSizeView;
        }
    }

    public CardListAdapter(GameApplication app) {
        this.app = app;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public CardListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        CardView cardView = (CardView) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.level_list_card, parent, false);

        ConstraintLayout rootLayout = (ConstraintLayout) cardView.getChildAt(0);
        TextView levelSizeView = (TextView) rootLayout.getChildAt(0);
        RecyclerView levelListView = (RecyclerView) rootLayout.getChildAt(1);
        TextView pageNumberView = (TextView) rootLayout.getChildAt(2);

        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(app, 2);
        levelListView.setLayoutManager(mLayoutManager);

        return new ViewHolder(cardView, levelSizeView, levelListView, pageNumberView);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        TextView t = holder.levelSizeView;
        // Number of agents starts at 3 and position at 0.
        int number_of_agent = position+3;
        t.setText(String.valueOf(number_of_agent));

        holder.levelListView.setAdapter(app.getLevelListAdapter(number_of_agent));

        // set page number
        TextView pageNumber = holder.pageNumberView;
        pageNumber.setText(getPageNumber(position));
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return app.getLevels().size();
    }

    private String getPageNumber(int position) {
        return String.valueOf(position+1) + " / " + String.valueOf(this.getItemCount());
    }
}
