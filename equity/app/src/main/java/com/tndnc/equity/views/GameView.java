package com.tndnc.equity.views;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Color;
import android.graphics.Rect;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.tndnc.equity.GameApplication;
import com.tndnc.equity.GameViewThread;
import com.tndnc.equity.R;
import com.tndnc.equity.activities.GameActivity;
import com.tndnc.equity.models.Actor;
import com.tndnc.equity.models.IPiece;
import com.tndnc.equity.models.Model;
import com.tndnc.equity.models.Position;
import com.tndnc.equity.models.Preference;

import java.io.IOException;
import java.io.InputStream;

public class GameView extends SurfaceView implements SurfaceHolder.Callback{

    private GameViewThread th;
    private GameApplication app;
    private int ch, cw;
    private Integer pieceselect = null;
    private int tempx, tempy;
    private int xdebut, ydebut;
    private int deltaX, deltaY;
    private int min, max;
    private int pieceSize,gridTop;
    private Bitmap fire,oil,water,uranium,plant,gold,power;


    Rect dst = new Rect();


    public GameView(Context context, AttributeSet attrs) {
        super(context, attrs);
        getHolder().addCallback(this);
        app = (GameApplication) (context.getApplicationContext());
        fire = BitmapFactory.decodeResource(getResources(), R.drawable.fire);
        oil = BitmapFactory.decodeResource(getResources(), R.drawable.oil);
        water = BitmapFactory.decodeResource(getResources(), R.drawable.water_drop);
        uranium = BitmapFactory.decodeResource(getResources(), R.drawable.radioactive);
        plant = BitmapFactory.decodeResource(getResources(), R.drawable.green_herb);
        power = BitmapFactory.decodeResource(getResources(), R.drawable.power_bolt);
        gold = BitmapFactory.decodeResource(getResources(), R.drawable.gold_bar);

        if(fire == null){
            System.err.println("FAIL");
        }

    }


    public void surfaceCreated(SurfaceHolder h) {
        th = new GameViewThread(getHolder(), this);
        th.setRunning(true);
        th.start();
    }

    public void surfaceChanged(SurfaceHolder sh, int f, int w, int h) {
        ch = h;
        cw = w;
    }

    public void surfaceDestroyed(SurfaceHolder h) {
        boolean retry = true;
        th.setRunning(false);
        while (retry) {
            try {
                th.join();
                retry = false;
            } catch (InterruptedException e) {
            }
        }
    }


    public void draw(Canvas c) {
        super.draw(c);
        Paint paint = new Paint();
        paint.setColor(ContextCompat.getColor(getContext(),R.color.colorPrimary));
        c.drawRect(0,0,getRight(),getBottom(),paint);
        IPiece currentPiece;
        int currentCol;
        int currentLig;

        int i = 0;
        int left ,right ,top ,bottom;

        Model gameModel = app.getGameModel();
        int nbActor = gameModel.getNbActors();
        pieceSize = c.getWidth() /(nbActor+1);
        gridTop = c.getHeight()/2 -pieceSize*nbActor/2;

        //Dessin des Pieces
        while (gameModel.getPiece(i) != null) {

            currentPiece = gameModel.getPiece(i);
            currentCol = currentPiece.getPos().getCol();
            currentLig = currentPiece.getPos().getLig();

            left = pieceSize * currentCol;
            top = gridTop + pieceSize * currentLig;

            if (currentPiece instanceof Actor) {
                right =  pieceSize + pieceSize * currentCol;
                bottom = gridTop + pieceSize + pieceSize * currentLig;
                dst.set(left + 20, top + 20, right - 20, bottom - 20);
                paint.setColor(Color.RED + currentPiece.getId()*10000);
                c.drawRect(dst,paint);
            } else {
                Preference pref = (Preference) currentPiece;
                right = pieceSize + pieceSize * currentCol;
                bottom = gridTop + pieceSize + pieceSize * currentLig;
                paint.setColor(ContextCompat.getColor(getContext(),R.color.colorPrimaryLight));
                c.drawCircle((right-left)/2 + left,(top-bottom)/2 + bottom,this.getWidth()/(nbActor*3),paint);
                dst.set(left, top, right, bottom);
                //System.out.println(((Preference) currentPiece).getValue());
                switch (((Preference) currentPiece).getValue()){
                    case 1:c.drawBitmap(power,null,dst,null);
                        break;
                    case 2:c.drawBitmap(water,null,dst,null);
                        break;
                    case 3:c.drawBitmap(plant,null,dst,null);
                        break;
                    case 4:c.drawBitmap(uranium,null,dst,null);
                        break;
                    case 5:c.drawBitmap(fire,null,dst,null);
                        break;
                    case 6:c.drawBitmap(oil,null,dst,null);
                        break;
                    case 7:c.drawBitmap(gold,null,dst,null);
                        break;
                }

                if(!(pref.getSelectedby()== -1)){
                    Position posAgent = new Position( 0,currentLig);
                    paint.setColor(Color.RED + gameModel.getIdByPos(posAgent)*10000);
                    paint.setStyle(Paint.Style.STROKE);
                    paint.setStrokeWidth(20);
                    c.drawCircle((right-left)/2 + left,(top-bottom)/2 + bottom,this.getWidth()/(nbActor*3 ),paint);
                    paint.setStyle(Paint.Style.FILL);
                }

            }

            i++;
        }
    }


    public boolean onTouchEvent(MotionEvent event) {
        Model gameModel = app.getGameModel();
        int nbActor = gameModel.getNbActors();
        int pos_x = cw /(nbActor+1);
        int pos_y = cw /(nbActor+1);
        int x = (int) event.getX();
        int y = (int) event.getY()-gridTop;
        int i = x / pos_x;
        int j = y / pos_y;
        Position pos = new Position(i, j);
        Position posAgent = new Position( 0,j);
        int action = event.getAction();
        switch (action) {
            case MotionEvent.ACTION_DOWN: {
                if( y > 0 && y < pieceSize*nbActor ){
                    if (gameModel.getIdByPos(pos) != null) {
                        int selectedPrefId = gameModel.getIdByPos(pos);
                        if(gameModel.getPiece(selectedPrefId) instanceof Preference){
                            gameModel.ModelSetSelected(gameModel.getIdByPos(posAgent),selectedPrefId);
                            xdebut = (int) event.getX();
                            ydebut = (int) event.getY();
                            pieceselect = selectedPrefId;
                            min = 0;
                            max = 6;
                        }
                    }
                }
                return true;
            }

            case MotionEvent.ACTION_MOVE: {
                tempx = (int) event.getX();
                tempy = (int) event.getY();
                deltaX = (xdebut - tempx);
                deltaY = (ydebut - tempy);

                return true;
            }

            case MotionEvent.ACTION_UP: {
                Context context = this.getContext();
                ((GameActivity) context).onWin();
                tempx = 0;
                tempy = 0;
                deltaX = 0;
                deltaY = 0;
                pieceselect = null;
                return true;
            }

            default:
                return true;
        }
    }

}
