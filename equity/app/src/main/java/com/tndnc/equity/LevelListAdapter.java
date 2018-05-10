package com.tndnc.equity;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

import com.tndnc.equity.models.Level;
import com.tndnc.equity.views.LevelButtonView;

import org.w3c.dom.Text;

import java.util.List;

public class LevelListAdapter extends RecyclerView.Adapter<LevelListAdapter.ViewHolder> {

    private List<Level> levels;

    static class ViewHolder extends RecyclerView.ViewHolder {
        LevelButtonView buttonView;
        TextView levelName, levelComplete;
        ViewHolder(ConstraintLayout root, TextView l, LevelButtonView v, TextView lc) {
            super(root);
            levelName = l;
            levelComplete = lc;
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
        TextView levelComplete = (TextView) rootLayout.getChildAt(1);
        LevelButtonView buttonView = (LevelButtonView) rootLayout.getChildAt(2);

        return new ViewHolder(rootLayout, levelName, buttonView, levelComplete);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Level l = this.levels.get(position);
        holder.buttonView.setLevel(l);
        holder.levelName.setText("Level " + String.valueOf(position+1));
        Context ctx = holder.levelComplete.getContext();
        SharedPreferences prefs = ctx.getSharedPreferences("level_completion",
                Context.MODE_PRIVATE);

        if (prefs.getString(l.getId()+"_sel_pref", "0").length() > 1) {
            // Level in progress
            holder.levelComplete.setText(R.string.in_progress);
            holder.levelComplete.setTextColor(ctx.getResources().getColor(R.color.colorAccentRed));
            return;
        }
        if(prefs.getBoolean(l.getId(), false))
        {
            holder.levelComplete.setText(R.string.level_done);
            holder.levelComplete.setTextColor(ctx.getResources().getColor(R.color.white));
        }

    }

    @Override
    public int getItemCount() {
        return this.levels.size();
    }
}
