package com.example.gualti.equity;

import android.app.Application;
import android.content.SharedPreferences;

import java.util.UUID;


public class GameApplication extends Application {
    private Model theGame;
    private int i;
    private String uniqueId;
    private String userAge;
    private String userFormation;

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
        theGame = new Model(i, this);
        System.err.println(uniqueId);

    }

    public void setPartie(int part) {
        System.out.println("i : "+i);
        this.i = part;
        theGame = new Model(i, this);
        theGame.setNbmoves(0);
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


}