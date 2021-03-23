package com.rumbagames.pillowgame;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.net.NetworkInterface;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class EsperandoJugadores extends AppCompatActivity {
    ArrayAdapter newslist_adapter;
    ArrayList<String> new_subject = new ArrayList<>();

    int equipo = 0;
    String codigoPartida, nombreUsuario;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_esperandojugadores);

        SharedPreferences prefs = getSharedPreferences("config", Context.MODE_PRIVATE);
        nombreUsuario = prefs.getString("nombre", "");

        final ListView post_view = findViewById(R.id.listajugadores_list);
                String new_value = "Equipo 1: " + "Nombre";
                new_subject.add(new_value);
                newslist_adapter = new ArrayAdapter(
                        EsperandoJugadores.this,
                        android.R.layout.simple_expandable_list_item_1, new_subject);
                post_view.setAdapter(newslist_adapter);

        codigoPartida = randomNum();

        TextView codigoTxt = (TextView) findViewById(R.id.code_txt);
        codigoTxt.setText("Codigo: " + codigoPartida);
        test();

        DatabaseReference database = FirebaseDatabase.getInstance().getReference();
        //DatabaseReference ref = database.child("games");

        Query phoneQuery = database.child("games").child(codigoPartida);
        phoneQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                /*for(DataSnapshot singleSnapshot : dataSnapshot.getChildren()){
                    Log.e("Error", singleSnapshot.child("nombre").getValue().toString());
                }*/
                List<String> list = new ArrayList<>();
                for(DataSnapshot ds : dataSnapshot.getChildren()) {
                    String arrival = ds.child("nombre").getValue(String.class);
                    Log.e("Error", arrival.toString());
                    list.add(arrival);
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

    }

    public String randomNum() {
        final int random = new Random().nextInt(99999);
        //Toast.makeText(this, String.format("%05d", random), Toast.LENGTH_LONG).show();
        return String.format("%05d", random) ;
    }

    public boolean test() {
        // Write a message to the database
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("games");

        myRef.child(codigoPartida).child("jugadores").child(equipo + "").child(getMacAddress()).child("nombre").setValue(nombreUsuario);
        return true;
    }

    public static String getMacAddress() {
        try {
            List<NetworkInterface> all = Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface nif : all) {
                if (!nif.getName().equalsIgnoreCase("wlan0")) continue;

                byte[] macBytes = nif.getHardwareAddress();
                if (macBytes == null) {
                    return "";
                }

                StringBuilder res1 = new StringBuilder();
                for (byte b : macBytes) {
                    res1.append(Integer.toHexString(b & 0xFF));
                }

                if (res1.length() > 0) {
                    res1.deleteCharAt(res1.length() - 1);
                }
                return res1.toString();
            }
        } catch (Exception ex) {
            Log.e("Error", ex.getMessage());
        }
        return "";
    }

}