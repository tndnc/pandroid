package com.tndnc.equity.models;


import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;


public class Model{
    private Grid grid;
    private int nbActors;
    private IPiece Pieces[];
    private int nbmoves;
    private long gameTime;
    private String moveSequence;
    private String levelName;
    private Level nextLevel;

    public String getMoveSequence(){
        return moveSequence;
    }

    public IPiece getPiece(int i){
        return Pieces[i];
    }

    public Integer getIdByPos(Position pos) {
        if (!grid.isEmpty(pos)) {
            return grid.getPieceGridID(pos);
        } else {
            return null;
        }
    }

    public void ModelSetSelected(int idActor,int idPreference){
        for(int i = 0; i<Pieces.length;i++){
            if(Pieces[i]instanceof Preference){
                Preference pref =(Preference) Pieces[i];
                if(pref.getId() == idPreference){
                    if(pref.getSelectedby() == -1){
                        pref.setSelectedby(idActor);
                        moveSequence += idActor+",";
                        nbmoves ++;
                    }else{
                        pref.setSelectedby(-1);
                    }
                }else{
                    if(pref.getSelectedby() == idActor){
                        pref.setSelectedby(-1);
                    }
                }
            }
        }
    }


    public Map<Integer, Integer> multipleSelections(){
        List<Integer> prefs = new ArrayList<Integer>();
        for(int i = 0; i<Pieces.length;i++){
            if(Pieces[i]instanceof Preference){
                Preference pref =(Preference) Pieces[i];
                if(pref.getSelectedby() != -1){
                    prefs.add(pref.getValue());
                }
            }
        }
        Map<Integer, Integer> counts = new HashMap<>();
        for (Integer i : prefs) {
            if (counts.containsKey(i)) {
                counts.put(i, counts.get(i) + 1);
            } else {
                counts.put(i, 1);
            }
        }
        return  counts;
    }

    public int getGameTime(){
        return (int) ((Calendar.getInstance().getTimeInMillis()- gameTime)/1000);
    }

    public Model(Level l) {
        this.nbActors = l.getSize();
        this.grid = new Grid(this.nbActors);
        this.levelName = l.getId();
        this.nextLevel = l.getNextLevel();
        Pieces = new IPiece[this.nbActors * (this.nbActors+1) +1];
        for (int j = 0; j < this.nbActors; j++) {
            Pieces[j] = new Actor(j, 0, j);
        }

        List<List<Integer>> allprefs = l.getPreferences();
        for (int i = 0; i < allprefs.size(); i++) {
            List<Integer> prefs = allprefs.get(i);

            for (int j = 0; j < prefs.size(); j++) {
                Pieces[j+(i+1)*this.nbActors] = new Preference(j+(i+1)*this.nbActors,
                        j+1, i, prefs.get(j));
            }
        }

        for(int y = 0;y<nbActors*(nbActors+1);y++){
            grid.set(Pieces[y].getPos(), y);
        }

        this.nbmoves = 0;
        gameTime = Calendar.getInstance().getTimeInMillis();
    }

    public int getNbActors() {
        return nbActors;
    }

    public boolean isJealous(Actor actor){
        boolean res = false;
        /*
        int j = 1;
        Preference[] beforePiece = new Preference[nbActors];
        Preference[] tmpPiece = new Preference[nbActors];
        Preference[] afterPiece = new Preference[nbActors];

        for(int k = 0;k<nbActors;k++){
            if(j >= nbActors*2){
                beforePiece[k] = (Preference) Pieces[j-(nbActors - k)];
            }
            if (j <= Pieces.length-(nbActors*2)){
                afterPiece[k] = (Preference)Pieces[j+(nbActors)+k];
            }
            tmpPiece[k] = (Preference) Pieces[j+k];
        }
        res = isJealous(beforePiece,tmpPiece) || isJealous(afterPiece,tmpPiece);
        */
        return res;

    }


    //checking if p2 is jealous of P1
    private boolean isJealous(Preference[] P1,Preference[] P2){
        if (P1[0] == null){
            return false;
        }
        Preference P2pref = null;
        Preference P1pref = null;
        for (Preference pref:P1){
            if(pref.getSelectedby() != -1){
                P1pref = pref;
            }
        }
        for(Preference pref:P2){
            if(pref.getSelectedby() != -1){
                P2pref = pref;
            }
        }
        for(Preference pref:P2){
            if(pref.getValue() == P1pref.getValue()){
                if(pref.getPos().getCol() < P2pref.getPos().getCol()){
                    return true;
                }
            }
        }
        return false;
    }

    //win every time is 4 pieces are selected
    public boolean endOfGame(){
        int cpt = 0;
        ArrayList<Integer> results = new ArrayList<>();
        for(int i = 0; i<Pieces.length;i++){
            if(Pieces[i]instanceof Preference){
                Preference pref =(Preference) Pieces[i];
                if(pref.getSelectedby() != -1){
                    if(results.contains(pref.getValue())){
                        return false;
                    }
                    results.add(pref.getValue());
                    cpt++;
                }
            }
        }if(cpt == nbActors){
            boolean arejealous = false;
            for(int j =nbActors;j<=Pieces.length-nbActors;j++){
                if(j % nbActors == 0 ){
                    Preference[] beforePiece = new Preference[nbActors];
                    Preference[] tmpPiece = new Preference[nbActors];
                    Preference[] afterPiece = new Preference[nbActors];

                    for(int k = 0;k<nbActors;k++){
                        if(j >= nbActors*2){
                            beforePiece[k] = (Preference) Pieces[j-(nbActors - k)];
                        }
                        if (j <= Pieces.length-(nbActors*2)){
                            afterPiece[k] = (Preference)Pieces[j+(nbActors)+k];
                        }
                        tmpPiece[k] = (Preference) Pieces[j+k];
                    }
                    arejealous = arejealous || isJealous(beforePiece,tmpPiece) || isJealous(afterPiece,tmpPiece);

                }
            }
            return !arejealous;
        }
        return false;
    }
    public int getNbmoves() {
        return nbmoves;
    }
    public String getLevelName() {
        return this.levelName;
    }
    public Level getNextLevel() {
        return this.nextLevel;
    }
}
