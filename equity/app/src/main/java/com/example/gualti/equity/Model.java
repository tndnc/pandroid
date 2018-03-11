package com.example.gualti.equity;

import android.content.res.AssetManager;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.IOException;
import java.io.InputStream;
import java.security.cert.TrustAnchor;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

public class Model{
    private Grid grid;
    private int nbActors;
    private IPiece Pieces[];

    public Model() {
        Pieces = new IPiece[21];
        nbActors =4;
        grid = new Grid(nbActors);
        Pieces[0] = new Actor(0, 0,0);
        Pieces[1] = new Actor(1, 0,1);
        Pieces[2] = new Actor(2, 0,2);
        Pieces[3] = new Actor(3, 0,3);

        Pieces[4] =  new Preference(4,1,0,2);
        Pieces[5] =  new Preference(5,2,0,3);
        Pieces[6] = new Preference(6,3,0,1);
        Pieces[7] = new Preference(7,4,0,4);

        Pieces[8] = new Preference(8,1,1,2);
        Pieces[9] = new Preference(9,2,1,3);
        Pieces[10] = new Preference(10,3,1,4);
        Pieces[11] = new Preference(11,4,1,1);

        Pieces[12] = new Preference(12,1,2,3);
        Pieces[13] = new Preference(13,2,2,2);
        Pieces[14] = new Preference(14,3,2,1);
        Pieces[15] = new Preference(15,4,2,4);

        Pieces[16] = new Preference(16,1,3,4);
        Pieces[17] = new Preference(17,2,3,3);
        Pieces[18] = new Preference(18,3,3,2);
        Pieces[19] = new Preference(19,4,3,1);


        for(int i = 0;i<20;i++){
            grid.set(Pieces[i].getPos(), i);
        }

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


    public Model(int i, GameApplication app) {
        System.out.println("model Start");
        String fname = "levels.xml";
        AssetManager ass = app.getAssets();
        try {
            String[] strings = app.getAssets().list("");
            for(String s : strings){
                System.out.println(s);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            Document document = readXMLFile(ass.open(fname));

            Element racine = document.getDocumentElement();
            NodeList racineNoeuds = racine.getChildNodes();
            Node noeud = racineNoeuds.item(i); //le niveau que l'on recupere . noeud =level
            int size = Integer.parseInt(noeud.getAttributes().getNamedItem("size").getNodeValue());
            nbActors = size;
            grid = new Grid(size);
            System.out.println(size);
            Pieces = new IPiece[size*(size+1)+1];
            for(int j =0;j<size;j++){
                Pieces[j] =  new Actor(j, 0,j);
                System.out.println("added actor");
            }
            NodeList pieceNoeuds = noeud.getChildNodes();
            for (int k = 0; k < pieceNoeuds.getLength(); k++) {
                String line = pieceNoeuds.item(k).getAttributes().getNamedItem("line").getNodeValue();
                String order = pieceNoeuds.item(k).getAttributes().getNamedItem("order").getNodeValue();
                char [] orderAschar = order.toCharArray();
                for(int l = 0;l<orderAschar.length;l++){
                    Pieces[l+(k+1)*size] = new Preference(l+(k+1)*size,l+1,Integer.parseInt(line)-1,orderAschar[l]);
                    //int id, int ncol, int nlig,int value
                    System.out.println("added Pref");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        for(IPiece p : Pieces){
            if(p instanceof Preference){
                System.out.println(p.getId()+","+p.getPos().getCol()+","+p.getPos().getLig());
            }
        }
        for(int y = 0;y<nbActors*(nbActors+1);y++){
            grid.set(Pieces[y].getPos(), y);
        }
    }

    public int getNbActors() {
        return nbActors;
    }

    public Integer getLig(int id) {
        return Pieces[id].getPos().getLig();
    }

    public Integer getCol(int id) {
        return Pieces[id].getPos().getCol();
    }


    //checking if p2c is jealous of P1
    public boolean isJealous(Preference[] P1,Preference[] P2){
        if (P1[0] == null){
            System.out.println("first step");
            return false;
        }
        Preference P2pref = null;
        Preference P1pref = null;
        for (Preference pref:P1){
            System.out.println("id1 : "+pref.getId());
            if(pref.getSelectedby() != -1){
                P1pref = pref;
            }
        }
        for(Preference pref:P2){
            System.out.println("id2 : "+pref.getId());
            if(pref.getSelectedby() != -1){
                P2pref = pref;
            }
        }
        for(Preference pref:P2){
            if(pref.getValue() == P1pref.getValue()){
                if(pref.getPos().getCol() > P2pref.getPos().getCol()){
                    return true;
                }
            }
        }
        return false;
    }

    //win every time is 4 pieces are selected
    public boolean endOfGame(){
        int cpt = 0;
        for(int i = 0; i<Pieces.length;i++){
            if(Pieces[i]instanceof Preference){
                Preference pref =(Preference) Pieces[i];
                if(pref.getSelectedby() != -1){
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
                            System.out.println("t");
                            beforePiece[k] = (Preference) Pieces[j-(k)];
                        }
                        if (j <= Pieces.length-nbActors){
                            afterPiece[k] = (Preference)Pieces[j+(k)];
                        }
                        tmpPiece[k] = (Preference) Pieces[j+k];
                        System.out.println("j");
                    }
                    arejealous = arejealous || isJealous(beforePiece,tmpPiece) || isJealous(afterPiece,tmpPiece);

                }
            }
            System.out.println(arejealous);
            return !arejealous;
        }
        return false;
    }
}
