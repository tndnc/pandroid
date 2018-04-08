package com.tndnc.equity.models;

import java.util.List;

public class Level {

    private String id;
    private int size;
    private List<List<Integer>> preferences;
    private Level nextLevel;

    public Level(String id, int size, List<List<Integer>> prefs) {
        this.id = id;
        this.size = size;
        this.preferences = prefs;
    }

    public void setNextLevel(Level l) {
        this.nextLevel = l;
    }

    public Level getNextLevel() {
        return this.nextLevel;
    }

    public String getId() {
        return id;
    }

    public int getSize() {
        return size;
    }

    public List<List<Integer>> getPreferences() {
        return preferences;
    }

    @Override
    public String toString() {
        return "[" + this.id + "/" + this.size + "]";
    }
}
