package com.rumbagames.pillowgame;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
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
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.stream.IntStream;

public class EsperandoJugadores extends AppCompatActivity {
    ArrayList<DatosJugadores> equipos = new ArrayList<DatosJugadores>();

    int equipoJugador = -1;
    boolean master = false;
    int numJugadores = 0;
    String codigoPartida, nombreUsuario;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_esperandojugadores);

        SharedPreferences prefs = getSharedPreferences("config", Context.MODE_PRIVATE);
        nombreUsuario = prefs.getString("nombre", "");
        master = getIntent().getExtras().getBoolean("master", false);

        ListView post_view = findViewById(R.id.listajugadores_list);

        // Inicializar el adaptador con la fuente de datos.
        AdapterJugadores mLeadsAdapter = new AdapterJugadores(EsperandoJugadores.this,
                equipos);

        //Relacionando la lista con el adaptador
        post_view.setAdapter(mLeadsAdapter);

        codigoPartida = randomNum();

        TextView codigoTxt = (TextView) findViewById(R.id.code_txt);
        codigoTxt.setText("Codigo: " + codigoPartida);

        DatabaseReference database = FirebaseDatabase.getInstance().getReference();

        Query query = database.child("games").child(codigoPartida).child("jugadores");
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mLeadsAdapter.clear();
                equipos.clear();
                numJugadores = 0;
                int count = 0;
                for(DataSnapshot ds : dataSnapshot.getChildren()) {
                    count = Integer.parseInt(ds.getKey());
                    for (DataSnapshot ds2 : ds.getChildren()) {
                        if(!ds2.getKey().equals("jugadorjugando")){
                        numJugadores++;
                        int[] equiposNumeros = {-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1};
                        for (int i = 0; i < equipos.size(); i++) {
                            equiposNumeros[i] = Integer.parseInt(equipos.get(i).getEquipo());
                        }
                        if (!IntStream.of(equiposNumeros).anyMatch(x -> x == Integer.parseInt(ds.getKey()))) {
                            //index not exists
                            DatosJugadores arrival = ds2.getValue(DatosJugadores.class);
                            arrival.setNombreJ1(arrival.getNombre());
                            arrival.setIdJ1(ds2.getKey());
                            arrival.setEquipo(ds.getKey());
                            equipos.add(arrival);
                        } else {
                            // index exists
                            int contFor = 0;
                            for (int i = 0; i < equipos.size(); i++) {
                                if (equipos.get(i).getEquipo().equals(ds.getKey())) {
                                    contFor = i;
                                }
                            }
                            DatosJugadores arrival = equipos.get(contFor);
                            DatosJugadores arrival2 = ds2.getValue(DatosJugadores.class);
                            if (arrival.idJ1.equals("")) {
                                arrival.setNombreJ1(arrival2.getNombre());
                                arrival.setIdJ1(ds2.getKey());
                            } else if (arrival.idJ2.equals("")) {
                                arrival.setNombreJ2(arrival2.getNombre());
                                arrival.setIdJ2(ds2.getKey());
                            } else if (arrival.idJ3.equals("")) {
                                arrival.setNombreJ3(arrival2.getNombre());
                                arrival.setIdJ3(ds2.getKey());
                            }
                            equipos.set(contFor, arrival);
                            }
                        }
                    }
                }
                count++;
                DatosJugadores vacio = new DatosJugadores();
                vacio.setEquipo(count + "");
                equipos.add(vacio);
                mLeadsAdapter.notifyDataSetChanged();
                if(equipoJugador == -1)
                    entrarPartida(mLeadsAdapter);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

        post_view.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {

                DatosJugadores datos = mLeadsAdapter.getItem(position);
                if (!datos.getEquipo().equals(equipoJugador)){
                    if(!(!datos.getIdJ1().equals("") && !datos.getIdJ2().equals("") && !datos.getIdJ3().equals(""))){
                        DatabaseReference mPostReference = FirebaseDatabase.getInstance().getReference("games").child(codigoPartida).child("jugadores").child(equipoJugador + "").child(getMacAddress());
                        mPostReference.removeValue();
                        equipoJugador = Integer.parseInt(datos.getEquipo());
                        añadirEquipo(equipoJugador);
                    }
                }


            }
        });
    }

    public String randomNum() {
        final int random = new Random().nextInt(99999);
        //Toast.makeText(this, String.format("%05d", random), Toast.LENGTH_LONG).show();
        return String.format("%05d", random) ;
    }

    public boolean añadirEquipo(int equipoNum) {
        // Write a message to the database
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("games");

        myRef.child(codigoPartida).child("jugadores").child(equipoNum + "").child(getMacAddress()).child("nombre").setValue(nombreUsuario);
        return true;
    }

    public void entrarPartida(AdapterJugadores mLeadsAdapter){
        int equipoDisponible = 0;
        while(equipoDisponible != -1) {
            DatosJugadores datos = mLeadsAdapter.getItem(equipoDisponible);
                if (!(!datos.getIdJ1().equals("") && !datos.getIdJ2().equals("") && !datos.getIdJ3().equals(""))) {
                    equipoJugador = Integer.parseInt(datos.getEquipo());
                    añadirEquipo(equipoJugador);
                    equipoDisponible = -1;
                }else {
                    equipoDisponible++;
                }
        }
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

    public void openInsertarTabus(View view) {
        actualizarEquipos();
        Intent intent = new Intent(this, InsertarTabus.class);
        intent.putExtra("master", master);
        intent.putExtra("codigopartida", codigoPartida);
        intent.putExtra("numjugadores", numJugadores);
        startActivity(intent);
        finish();
    }

    public void actualizarEquipos(){
        DatabaseReference mPostReference = FirebaseDatabase.getInstance().getReference("games").child(codigoPartida).child("jugadores");
        mPostReference.removeValue();
        for(int i = 0; i < equipos.size(); i++ ){
            int cont = 0;
            if((!equipos.get(i).idJ1.equals(""))) {
                mPostReference.child((i + 1) + "").child(equipos.get(i).idJ1).child("nombre").setValue(equipos.get(i).nombreJ1);
                cont++;
            }
            if((!equipos.get(i).idJ2.equals(""))){
                mPostReference.child((i+1) + "").child(equipos.get(i).idJ2).child("nombre").setValue(equipos.get(i).nombreJ2);
                cont++;
            }
            if((!equipos.get(i).idJ3.equals(""))){
                mPostReference.child((i+1) + "").child(equipos.get(i).idJ3).child("nombre").setValue(equipos.get(i).nombreJ3);
                cont++;
            }
            if(cont>0) {
                final int random = new Random().nextInt(cont);
                mPostReference.child((i + 1) + "").child("jugadorjugando").setValue(random);
            }
        }

    }

}