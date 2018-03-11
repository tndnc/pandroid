package com.example.gualti.equity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

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

        switch (Levelbutton.getId()) {
            case R.id.button1:
                bundle.putInt("ActualXMLLevel", 0);
                GameActivity.putExtras(bundle);
                startActivity(GameActivity);
                app.setPartie(1);
                break;
            case R.id.button2:
                bundle.putInt("ActualXMLLevel", 1);
                GameActivity.putExtras(bundle);
                startActivity(GameActivity);
                app.setPartie(0);
                break;
            case R.id.button3:
                bundle.putInt("ActualXMLLevel", 2);
                GameActivity.putExtras(bundle);
                startActivity(GameActivity);
                app.setPartie(2);
                break;
            default:
                break;
        }
    }
}
