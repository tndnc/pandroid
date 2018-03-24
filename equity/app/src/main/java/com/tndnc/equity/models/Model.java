package com.tndnc.equity.models;

import android.content.res.AssetManager;

import com.tndnc.equity.GameApplication;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

public class Model{
    private Grid grid;
    private int nbActors;
    private IPiece Pieces[];
    private int nbmoves;
    private long gameTime;
    private String moveSequence;

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
        System.out.println("Actor : "+idActor+", Pref : "+idPreference);
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

    public Document readXMLFile(InputStream fname) {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setNamespaceAware(false);
            factory.setValidating(false);
            DocumentBuilder builder = factory.newDocumentBuilder();
            return builder.parse(fname);
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }


    public int getGameTime(){
        return (int) ((Calendar.getInstance().getTimeInMillis()- gameTime)/1000);
    }

    public Model(Level l) {
        this.nbActors = l.getSize();
        this.grid = new Grid(this.nbActors);
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

//    public Model(int i, GameApplication app) {
//        moveSequence = "";
//        System.out.println("model Start");
//        gameTime = Calendar.getInstance().getTimeInMillis();
//        String fname = "levels.xml";
//        AssetManager ass = app.getAssets();
//        try {
//            String[] strings = app.getAssets().list("");
//            for(String s : strings){
//                System.out.println(s);
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        try {
//            Document document = readXMLFile(ass.open(fname));
//
//            Element racine = document.getDocumentElement();
//            NodeList racineNoeuds = racine.getChildNodes();
//            Node noeud = racineNoeuds.item(i); //le niveau que l'on recupere . noeud =level
//            int size = Integer.parseInt(noeud.getAttributes().getNamedItem("size").getNodeValue());
//            nbActors = size;
//            grid = new Grid(size);
//            System.out.println(size);
//            Pieces = new IPiece[size*(size+1)+1];
//            for(int j =0;j<size;j++){
//                Pieces[j] =  new Actor(j, 0,j);
//                System.out.println("added actor");
//            }
//            NodeList pieceNoeuds = noeud.getChildNodes();
//            for (int k = 0; k < pieceNoeuds.getLength(); k++) {
//                String line = pieceNoeuds.item(k).getAttributes().getNamedItem("line").getNodeValue();
//                String order = pieceNoeuds.item(k).getAttributes().getNamedItem("order").getNodeValue();
//                char [] orderAschar = order.toCharArray();
//                for(int l = 0;l<orderAschar.length;l++){
//                    Pieces[l+(k+1)*size] = new Preference(l+(k+1)*size,l+1,Integer.parseInt(line)-1,orderAschar[l]);
//                    //int id, int ncol, int nlig,int value
//                    System.out.println("added Pref");
//                }
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        for(IPiece p : Pieces){
//            if(p instanceof Preference){
//                System.out.println(p.getId()+","+p.getPos().getCol()+","+p.getPos().getLig());
//            }
//        }
//        for(int y = 0;y<nbActors*(nbActors+1);y++){
//            grid.set(Pieces[y].getPos(), y);
//        }
//    }

    public int getNbActors() {
        return nbActors;
    }

    //checking if p2 is jealous of P1
    private boolean isJealous(Preference[] P1,Preference[] P2){
        if (P1[0] == null){
            return false;
        }
        Preference P2pref = null;
        Preference P1pref = null;
        for (Preference pref:P1){
            System.out.print(" id1 : "+pref.getId());
            if(pref.getSelectedby() != -1){
                P1pref = pref;
            }
        }
        System.out.println();
        for(Preference pref:P2){
            System.out.print(" id2 : "+pref.getId());
            if(pref.getSelectedby() != -1){
                P2pref = pref;
            }
        }
        System.out.println();
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
                    System.out.println("before : "+isJealous(beforePiece,tmpPiece));
                    System.out.println("after : "+isJealous(beforePiece,tmpPiece));
                    arejealous = arejealous || isJealous(beforePiece,tmpPiece) || isJealous(afterPiece,tmpPiece);

                }
            }
            System.out.println(arejealous);
            return !arejealous;
        }
        return false;
    }

    public int getNbmoves() {
        return nbmoves;
    }

//    public void setNbmoves(int nbmoves) {
//        this.nbmoves = nbmoves;
//    }
}
