package com.tndnc.equity.views;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.AppCompatButton;
import android.util.AttributeSet;
import android.view.View;

import com.tndnc.equity.GameApplication;
import com.tndnc.equity.activities.GameActivity;
import com.tndnc.equity.models.Level;
import com.tndnc.equity.models.Model;


public class LevelButtonView extends AppCompatButton {

    private Level l;

    public LevelButtonView(Context context, AttributeSet attrs) {
        super(context, attrs);

        this.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View view) {
                Model m = new Model(l);
                Context ctx = view.getContext();
                Activity activity = (Activity) ctx;
                GameApplication app = (GameApplication) activity.getApplication();
                app.setPartie(m);
                Intent intent = new Intent(ctx, GameActivity.class);
                ctx.startActivity(intent);
            }
        });
    }

    public void setLevel(Level l) {
        this.l = l;
    }
}
