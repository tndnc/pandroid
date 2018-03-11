package com.example.gualti.equity;

/**
 * Created by gualti on 24/02/2018.
 */

public class Grid {

    private Integer grid[][];


    public Grid(int nbActors) {
        grid = new Integer[nbActors + 1][nbActors];
        for (int i = 0; i < nbActors + 1; i++) {
            for (int j = 0; j < nbActors; j++) {
                grid[i][j] = null;
            }
        }
    }

    public boolean isEmpty(Position pos) {
        if (grid[pos.getCol()][pos.getLig()] == null) {
            return true;
        } else {
            return false;
        }
    }

    public Integer getPieceGridID(Position pos) {
        return grid[pos.getCol()][pos.getLig()];
    }

    public void set(Position pos, int gridId) {
        grid[pos.getCol()][pos.getLig()] = gridId;
    }

    public void unset(Position pos) {
        grid[pos.getCol()][pos.getLig()] = null;
    }
}
