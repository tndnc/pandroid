package com.tndnc.equity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import java.util.Random;

public class LevelSelectActivity extends AppCompatActivity {

    GameApplication app;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_level_select);

    }
    public void StartGame(View Levelbutton){
        Intent GameActivity = new Intent(this, GameActivity.class);
        Bundle bundle = new Bundle();
        app = (GameApplication) this.getApplication();
        Random randomgen = new Random();
        int val = 0;

        switch (Levelbutton.getId()) {
            case R.id.button1:
                val = randomgen.nextInt(7)+1;
                bundle.putInt("ActualXMLLevel", val);
                GameActivity.putExtras(bundle);
                startActivity(GameActivity);
                app.setPartie(val);
                break;
            case R.id.button2:
                val = randomgen.nextInt(7)+7;
                bundle.putInt("ActualXMLLevel", val);
                GameActivity.putExtras(bundle);
                startActivity(GameActivity);
                app.setPartie(val);
                break;
            case R.id.button3:
                val = randomgen.nextInt(7)+14;
                bundle.putInt("ActualXMLLevel", val);
                GameActivity.putExtras(bundle);
                startActivity(GameActivity);
                app.setPartie(val);
                break;
            default:
                break;
        }
    }
}
