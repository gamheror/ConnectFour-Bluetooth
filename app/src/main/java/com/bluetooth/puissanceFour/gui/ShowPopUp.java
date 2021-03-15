package com.bluetooth.puissanceFour.gui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.bluetooth.puissanceFour.adapters.GridAdapt;

public class ShowPopUp extends Activity {

    PopupWindow popUp;
    LinearLayout layout;
    TextView tv;
    LinearLayout.LayoutParams params;
    LinearLayout mainLayout;
    Button home;
    Button replay;
    boolean click = true;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public void start(final GridAdapt grid){
        popUp = new PopupWindow(this);
        layout = new LinearLayout(this);
        mainLayout = new LinearLayout(this);
        tv = new TextView(this);
        home = new Button(this);
        home.setText("Back Home");
        home.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                Intent intent_home = new Intent(getApplicationContext(), MainActivity.class);
                startActivityForResult(intent_home, 48);
            }

        });

        replay = new Button(this);
        replay.setText("Play again");
        replay.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                grid.initGrid();
            }

        });

        params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        layout.setOrientation(LinearLayout.VERTICAL);
        tv.setText(GridAdapt.msg);
        layout.addView(tv, params);
        popUp.setContentView(layout);
        // popUp.showAtLocation(layout, Gravity.BOTTOM, 10, 10);
        mainLayout.addView(home, params);
        setContentView(mainLayout);
    }
}