package com.tndnc.equity;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.Button;

import com.tndnc.equity.models.Level;
import com.tndnc.equity.views.LevelButtonView;

import java.util.List;

public class LevelListAdapter extends RecyclerView.Adapter<LevelListAdapter.ViewHolder> {

    private List<Level> levels;

    static class ViewHolder extends RecyclerView.ViewHolder {
        // each data is a card that holds a list of levels of a given size.
        LevelButtonView buttonView;
        ViewHolder(LevelButtonView v) {
            super(v);
            buttonView = v;
        }
    }

    LevelListAdapter(List<Level> levels) {
        this.levels = levels;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LevelButtonView buttonView = (LevelButtonView) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.level_button_view, parent, false);

        return new ViewHolder(buttonView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Level l = this.levels.get(position);
        holder.buttonView.setText(String.valueOf(position+1));
        holder.buttonView.setLevel(l);
    }

    @Override
    public int getItemCount() {
        return this.levels.size();
    }
}
