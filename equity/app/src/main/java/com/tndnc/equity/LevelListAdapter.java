package com.tndnc.equity;

import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

import com.tndnc.equity.models.Level;
import com.tndnc.equity.views.LevelButtonView;

import java.util.List;

public class LevelListAdapter extends RecyclerView.Adapter<LevelListAdapter.ViewHolder> {

    private List<Level> levels;

    static class ViewHolder extends RecyclerView.ViewHolder {
        LevelButtonView buttonView;
        TextView levelName;
        ViewHolder(ConstraintLayout root, TextView l, LevelButtonView v) {
            super(root);
            levelName = l;
            buttonView = v;
        }
    }

    LevelListAdapter(List<Level> levels) {
        this.levels = levels;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ConstraintLayout rootLayout = (ConstraintLayout) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.level_item, parent, false);

        TextView levelName = (TextView) rootLayout.getChildAt(0);
        LevelButtonView buttonView = (LevelButtonView) rootLayout.getChildAt(1);


        return new ViewHolder(rootLayout, levelName, buttonView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Level l = this.levels.get(position);
//        holder.buttonView.setText(String.valueOf(position+1));
        holder.buttonView.setLevel(l);
        holder.levelName.setText("Level " + String.valueOf(position+1));
    }

    @Override
    public int getItemCount() {
        return this.levels.size();
    }
}
