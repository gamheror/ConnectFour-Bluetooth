package com.bluetooth.connectFour;

import androidx.appcompat.app.AppCompatActivity;

import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


public class MainActivity extends AppCompatActivity {

    private TextView mStatusTv;
    private Button mActivateBtn;
    private BluetoothAdapter mBluetoothAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mStatusTv = (TextView) findViewById(R.id.tv_status);
        mActivateBtn = (Button) findViewById(R.id.btn_enable);
        mBluetoothAdapter	= BluetoothAdapter.getDefaultAdapter();

        Button b_exit = (Button) findViewById(R.id.exit);

        //start the bluetooth activity to connect and start the game
        Button b_play = (Button) findViewById(R.id.play);
        b_play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent_play = new Intent(getApplicationContext(), BluetoothActivity.class);
                startActivityForResult(intent_play, 42);
            }
        });

        //permit the player to disable or enable the bluetooth depending on its state
        mActivateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mBluetoothAdapter.isEnabled()) {
                    mBluetoothAdapter.disable();

                    showDisabled();
                } else {
                    Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);

                    startActivityForResult(intent, 1000);
                }
            }
        });

        if (mBluetoothAdapter.isEnabled()) {
            showEnabled();
        } else {
            showDisabled();
        }

        b_exit.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                finish();
            }
        });

        //create a filter when the bluetooth's state change
        IntentFilter filter = new IntentFilter();
        filter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        registerReceiver(mReceiver, filter);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 48)
            Toast.makeText(this, "Code 48 récupéré", Toast.LENGTH_LONG).show();
    }

    /**
     * display the state of the bluetooth when its enable
     */
    private void showEnabled() {
        mStatusTv.setText("Bluetooth is On");
        mStatusTv.setTextColor(Color.GREEN);

        mActivateBtn.setText("Disable");
        mActivateBtn.setEnabled(true);
    }

    /**
     * display the state of the bluetooth when its disable
     */
    private void showDisabled() {
        mStatusTv.setText("Bluetooth is Off");
        mStatusTv.setTextColor(Color.RED);

        mActivateBtn.setText("Enable");
        mActivateBtn.setEnabled(true);
    }

    /**
     * receive message thanks to the filter set before in the OnCreate
     */
    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            if (BluetoothAdapter.ACTION_STATE_CHANGED.equals(action)) {
                final int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR);

                if (state == BluetoothAdapter.STATE_ON) {
                    showEnabled();
                }
            }
        }
    };

}