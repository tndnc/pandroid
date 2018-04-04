package com.tndnc.equity;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.SparseArray;

import com.tndnc.equity.models.Level;
import com.tndnc.equity.utils.LevelLoader;
import com.tndnc.equity.models.Model;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


public class GameApplication extends Application {
    private Model theGame;
    private String uniqueId;
//    private String userAge;
//    private String userFormation;

    private SparseArray<List<Level>> levels;
    private SparseArray<LevelListAdapter> cardAdapters;

    @Override
    public void onCreate() {
        super.onCreate();

        SharedPreferences userProfile = getSharedPreferences("user_profile", Context.MODE_PRIVATE);
        boolean userProfileSaved = userProfile.getBoolean("user_profile_saved", false);
        if (!userProfileSaved) {
            uniqueId = UUID.randomUUID().toString();
            SharedPreferences.Editor mEditor = userProfile.edit();
            mEditor.putString("uuid", uniqueId).apply();
        }


        // init levels container
        this.levels = new SparseArray<>();
        for (int i = 3; i <= 7; i++) {
            this.levels.put(i, new ArrayList<Level>());
        }
        // load levels in container
        LevelLoader.loadAllLevels(this);
        // init list adapters for card views
        this.cardAdapters = new SparseArray<>();
        for (int i = 3; i <= 7; i++) {
            this.cardAdapters.put(i, new LevelListAdapter(this.getLevels().get(i)));
        }
    }

    public void setPartie(Model m) {
        theGame = m;
    }


    public Model getGameModel() {
        return theGame;
    }

    public String getUniqueId(){
        return uniqueId;
    }

    public SparseArray<List<Level>> getLevels() {
        return this.levels;
    }

    public LevelListAdapter getLevelListAdapter(int numberOfAgents) {
        return this.cardAdapters.get(numberOfAgents);
    }
}