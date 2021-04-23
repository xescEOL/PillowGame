package com.rumbagames.pillowgame;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import java.net.NetworkInterface;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import androidx.appcompat.app.AppCompatActivity;

public class EntrarPartida extends AppCompatActivity {

    EditText codigo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_entrarpartida);

        EditText codigo = (EditText) findViewById(R.id.codigo_et);

    }

    public void entrarboton(View view) {
        Intent intent = new Intent(this, EsperandoJugadores.class);
        intent.putExtra("codigopartida", codigo.getText());
        intent.putExtra("mac", getIntent().getStringExtra("mac"));
        startActivity(intent);
    }
}