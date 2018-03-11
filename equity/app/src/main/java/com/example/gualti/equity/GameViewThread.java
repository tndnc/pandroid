package com.example.gualti.equity;

import android.graphics.Canvas;
import android.view.SurfaceHolder;

/**
 * Created by gualti on 24/02/2018.
 */

public class GameViewThread extends Thread {
    private SurfaceHolder holder;
    private GameView view;
    private boolean running = false;

    public GameViewThread(SurfaceHolder holder, GameView view) {
        this.holder = holder;
        this.view = view;
    }

    public void setRunning(boolean b) {
        this.running = b;
    }


    @Override
    public void run() {
        Canvas c;
        while (this.running) {
            c = holder.lockCanvas();
            if (c != null) {
                this.view.draw(c);
                this.holder.unlockCanvasAndPost(c);
            }
        }
    }
}

