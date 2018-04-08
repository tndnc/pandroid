package com.tndnc.equity;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.util.SparseArray;

import com.tndnc.equity.googleSheets.GoogleSheetsWriteUtil;
import com.tndnc.equity.models.Level;
import com.tndnc.equity.utils.LevelLoader;
import com.tndnc.equity.models.Model;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


public class GameApplication extends Application {
    private Model theGame;
    private String uniqueId;
    private GoogleSheetsWriteUtil sheetsWriteUtil;
    private SparseArray<List<Level>> levelsBySize;
    private SparseArray<LevelListAdapter> cardAdapters;
//    private List<Level> allLevels;


    @Override
    public void onCreate() {
        super.onCreate();

        SharedPreferences userProfile = getSharedPreferences("user_profile", Context.MODE_PRIVATE);
        boolean userProfileSaved = userProfile.getBoolean("user_profile_saved", false);
        if (!userProfileSaved) {
            uniqueId = UUID.randomUUID().toString();
            SharedPreferences.Editor mEditor = userProfile.edit();
            mEditor.putString("uuid", uniqueId).apply();
        } else {
            uniqueId = userProfile.getString("uuid", "ANON");
        }

        sheetsWriteUtil = new GoogleSheetsWriteUtil();
        try {
            GoogleSheetsWriteUtil.setup(this);
        } catch (GeneralSecurityException | IOException e) {
            Log.d("App", "Error during GoogleSheet service init");
            e.printStackTrace();
        }

        // init levelsBySize container
        this.levelsBySize = new SparseArray<>();
//        this.allLevels = new ArrayList<>();
        for (int i = 3; i <= 7; i++) {
            this.levelsBySize.put(i, new ArrayList<Level>());
        }
        // load levelsBySize in container
        LevelLoader.loadAllLevels(this);
        // init list adapters for card views
        this.cardAdapters = new SparseArray<>();
        for (int i = 3; i <= 7; i++) {
            this.cardAdapters.put(i, new LevelListAdapter(this.getLevelsBySize().get(i)));
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

    public SparseArray<List<Level>> getLevelsBySize() {
        return this.levelsBySize;
    }

    public LevelListAdapter getLevelListAdapter(int numberOfAgents) {
        return this.cardAdapters.get(numberOfAgents);
    }

    public GoogleSheetsWriteUtil getSheetsWriteUtil() {
        return sheetsWriteUtil;
    }

}