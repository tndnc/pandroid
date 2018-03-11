package com.example.gualti.equity;

import android.app.Application;


public class GameApplication extends Application {
    private Model theGame;
    public int i;
    public int nbmove = 0;

    public Model getGameModel() {
        return theGame;
    }

    @Override
    public void onCreate() {
        System.out.println("i : "+i);
        super.onCreate();
        theGame = new Model(i, this);

    }

    public void setPartie(int part) {
        System.out.println("i : "+i);
        this.i = part;
        theGame = new Model(i, this);
    }

}