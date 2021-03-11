package com.bluetooth.puissanceFour.activites;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.bluetooth.puissanceFour.BluetoothActivity;
import com.bluetooth.puissanceFour.R;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button b_jouer = (Button) findViewById(R.id.jouer);
        Button b_quitter = (Button) findViewById(R.id.quitter);
        b_jouer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent_jouer = new Intent(getApplicationContext(), BluetoothActivity.class);
                startActivityForResult(intent_jouer, 42);
            }
        });

        Button b_option = (Button) findViewById(R.id.option);
        b_option.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent_option = new Intent(getApplicationContext(), Option.class);
                startActivityForResult(intent_option, 40);
            }
        });

        b_quitter.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
               finish();
            }
        });
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 48)
            Toast.makeText(this, "Code 48 récupéré", Toast.LENGTH_LONG).show();
    }
}