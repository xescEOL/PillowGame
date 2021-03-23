package com.rumbagames.pillowgame;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        EditText nombreTxt = (EditText) findViewById(R.id.nombre_txt);
        SharedPreferences prefs = getSharedPreferences("config", Context.MODE_PRIVATE);
        nombreTxt.setText(prefs.getString("nombre", ""));

        nombreTxt.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {
                SharedPreferences prefs = getSharedPreferences("config", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = prefs.edit();
                editor.putString("nombre", nombreTxt.getText().toString());
                editor.commit();
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            public void onTextChanged(CharSequence s, int start, int before, int count) {}
        });

    }

    public void openCreate(View view) {
        Intent intent = new Intent(this, EsperandoJugadores.class);
        startActivity(intent);
    }
}