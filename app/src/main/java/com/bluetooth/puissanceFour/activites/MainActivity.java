package com.bluetooth.puissanceFour.activites;

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

import com.bluetooth.puissanceFour.BluetoothActivity;
import com.bluetooth.puissanceFour.R;

public class MainActivity extends AppCompatActivity {

    private TextView mStatusTv;
    private Button mActivateBtn;
    private BluetoothAdapter mBluetoothAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mStatusTv 			= (TextView) findViewById(R.id.tv_status);
        mActivateBtn 		= (Button) findViewById(R.id.btn_enable);
        Button b_jouer = (Button) findViewById(R.id.jouer);
        Button b_quitter = (Button) findViewById(R.id.quitter);
        mBluetoothAdapter	= BluetoothAdapter.getDefaultAdapter();

        b_jouer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent_jouer = new Intent(getApplicationContext(), BluetoothActivity.class);
                startActivityForResult(intent_jouer, 42);
            }
        });

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

        b_quitter.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                finish();
            }
        });

        IntentFilter filter = new IntentFilter();
        filter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        registerReceiver(mReceiver, filter);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 48)
            Toast.makeText(this, "Code 48 récupéré", Toast.LENGTH_LONG).show();
    }

    private void showEnabled() {
        mStatusTv.setText("Bluetooth is On");
        mStatusTv.setTextColor(Color.GREEN);

        mActivateBtn.setText("Disable");
        mActivateBtn.setEnabled(true);
    }

    private void showDisabled() {
        mStatusTv.setText("Bluetooth is Off");
        mStatusTv.setTextColor(Color.RED);

        mActivateBtn.setText("Enable");
        mActivateBtn.setEnabled(true);
    }

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