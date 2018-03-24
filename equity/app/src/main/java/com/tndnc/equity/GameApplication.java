package com.tndnc.equity;

import android.app.Application;
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
    private int i;
    private String uniqueId;
    private String userAge;
    private String userFormation;

    private SparseArray<List<Level>> levels;
    private SparseArray<LevelListAdapter> cardAdapters;

    @Override
    public void onCreate() {
        SharedPreferences mPrefs = getSharedPreferences("unique_id_f",0);
        String mString = mPrefs.getString("uniqueId", "0");
        if(mString.equals("0")){
            uniqueId = UUID.randomUUID().toString();
            SharedPreferences.Editor mEditor = mPrefs.edit();
            mEditor.putString("tag", uniqueId).apply();
        }
        super.onCreate();
//        theGame = new Model(i, this);
        System.err.println(uniqueId);

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

//    public void setPartie(int part) {
//        System.out.println("i : "+i);
//        this.i = part;
//        theGame = new Model(i, this);
//        theGame.setNbmoves(0);
//    }
    public void setPartie(Model m) {
        theGame = m;
    }
    public Model getGameModel() {
        return theGame;
    }

    public String getUniqueId(){
        return uniqueId;
    }

    public String getUserAge(){
        return userAge;
    }
    public String getUserFormation(){
        return userFormation;
    }

    public void setUserAge(String userAge){
        this.userAge = userAge;
    }
    public void setUserFormation(String userFormation){
        this.userFormation = userFormation;
    }

    public SparseArray<List<Level>> getLevels() {
        return this.levels;
    }

    public LevelListAdapter getLevelListAdapter(int numberOfAgents) {
        return this.cardAdapters.get(numberOfAgents);
    }
}