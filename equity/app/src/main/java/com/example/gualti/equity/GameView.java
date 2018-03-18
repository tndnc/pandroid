package com.example.gualti.equity;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Color;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.RatingBar;

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


    Rect dst = new Rect();


    public GameView(Context context, AttributeSet attrs) {
        super(context, attrs);
        getHolder().addCallback(this);
        app = (GameApplication) (context.getApplicationContext());
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

        IPiece currentPiece;
        int currentCol;
        int currentLig;

        int i = 0;
        int left ,right ,top ,bottom;

        Paint paint = new Paint();
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
                paint.setColor(Color.BLUE + (pref.getValue()*30000));
                c.drawCircle((right-left)/2 + left,(top-bottom)/2 + bottom,this.getWidth()/(nbActor*5),paint);

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
