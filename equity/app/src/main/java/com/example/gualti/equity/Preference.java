package com.example.gualti.equity;

/**
 * Created by gualti on 24/02/2018.
 */

public class Preference implements IPiece {
    private int id;
    private Position pos;
    private int selectedby;
    private int value;

    public Preference(int id, int ncol, int nlig,int value) {
        this.id = id;
        pos = new Position(ncol, nlig);
        selectedby = -1;
        this.value = value;
    }


    public int getId() {
        return id;
    }

    public Position getPos() {
        return pos;
    }

    public void setPos(Position pos) {
        this.pos = pos;
    }

    public void setSelectedby(int id){
        selectedby = id;
    }

    public int getSelectedby(){
        return selectedby;
    }

    public int getValue() {
        return value;
    }
}
