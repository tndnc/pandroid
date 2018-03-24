package com.tndnc.equity.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.LinearSnapHelper;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.tndnc.equity.GameApplication;
import com.tndnc.equity.CardListAdapter;
import com.tndnc.equity.R;

public class LevelSelectActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_level_select);

        RecyclerView mRecyclerView = findViewById(R.id.level_card_list);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this,
                LinearLayoutManager.HORIZONTAL, false);
        mRecyclerView.setLayoutManager(mLayoutManager);

        LinearSnapHelper snapHelper = new LinearSnapHelper();

        snapHelper.attachToRecyclerView(mRecyclerView);
        // specify an adapter (see also next example)
        RecyclerView.Adapter mAdapter = new CardListAdapter(
                (GameApplication) this.getApplication());
        mRecyclerView.setAdapter(mAdapter);
    }


    public void StartGame(View Levelbutton){
//        Intent GameActivity = new Intent(this, GameActivity.class);
//        Bundle bundle = new Bundle();
//        app = (GameApplication) this.getApplication();
//        Random randomgen = new Random();
//        int val = 0;

//        switch (Levelbutton.getId()) {
//            case R.id.button1:
//                val = randomgen.nextInt(7)+1;
//                bundle.putInt("ActualXMLLevel", val);
//                GameActivity.putExtras(bundle);
//                startActivity(GameActivity);
//                app.setPartie(val);
//                break;
//            case R.id.button2:
//                val = randomgen.nextInt(7)+7;
//                bundle.putInt("ActualXMLLevel", val);
//                GameActivity.putExtras(bundle);
//                startActivity(GameActivity);
//                app.setPartie(val);
//                break;
//            case R.id.button3:
//                val = randomgen.nextInt(7)+14;
//                bundle.putInt("ActualXMLLevel", val);
//                GameActivity.putExtras(bundle);
//                startActivity(GameActivity);
//                app.setPartie(val);
//                break;
//            default:
//                break;
//        }
    }
}
