package com.rumbagames.pillowgame;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Random;
import java.util.stream.IntStream;

import androidx.appcompat.app.AppCompatActivity;

import static com.rumbagames.pillowgame.EsperandoJugadores.getMacAddress;


public class InsertarTabus extends AppCompatActivity {

    String codigoPartida;
    DatosJugadores equipo = new DatosJugadores();
    ArrayList<DatosJugadores> equipos = new ArrayList<DatosJugadores>();
    boolean master = false;
    int numJugadores = 0;
    TextView equipoTV, j1TV, j2TV, j3TV;
    EditText tabu1ET, tabu2ET, tabu3ET, tabu4ET, tabu5ET;
    DatabaseReference database;
    Query query, query2;
    ValueEventListener listener, listener2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_insertartabus);

        master = getIntent().getExtras().getBoolean("master", false);
        codigoPartida = getIntent().getExtras().getString("codigopartida");
        numJugadores = getIntent().getExtras().getInt("numjugadores");
        //equipos = (ArrayList<DatosJugadores>)getIntent().getSerializableExtra("equipos");

        equipoTV = (TextView) findViewById(R.id.equipotxt);
        j1TV = (TextView) findViewById(R.id.jugador1);
        j2TV = (TextView) findViewById(R.id.jugador2);
        j3TV = (TextView) findViewById(R.id.jugador3);
        tabu1ET = (EditText) findViewById(R.id.tabu1ET);
        tabu2ET = (EditText) findViewById(R.id.tabu2ET);
        tabu3ET = (EditText) findViewById(R.id.tabu3ET);
        tabu4ET = (EditText) findViewById(R.id.tabu4ET);
        tabu5ET = (EditText) findViewById(R.id.tabu5ET);


        database = FirebaseDatabase.getInstance().getReference();
        query = database.child("games").child(codigoPartida).child("jugadores");
        listener = query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                equipos.clear();
                int count = 0;
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    count = Integer.parseInt(ds.getKey());
                    for (DataSnapshot ds2 : ds.getChildren()) {
                        if (!ds2.getKey().equals("jugadorjugando")) {
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
                                if (ds2.getKey().equals(getMacAddress())) {
                                    equipo = arrival;
                                    ActualizarEquipo(arrival);
                                }
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
                                if (ds2.getKey().equals(getMacAddress())) {
                                    equipo = arrival;
                                    ActualizarEquipo(arrival);
                                } else {
                                    if (equipo.getEquipo().equals(ds.getKey())) {
                                        equipo = arrival;
                                        ActualizarEquipo(arrival);
                                    }
                                }
                                equipos.set(contFor, arrival);
                            }
                        }
                    }
                }
                if(master) {
                    final int random = new Random().nextInt(count) + 1;
                    FirebaseDatabase.getInstance().getReference("games").child(codigoPartida).child("equipojugando").setValue(random);
                    FirebaseDatabase.getInstance().getReference("games").child(codigoPartida).child("ronda").setValue(0);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

        DatabaseReference database2 = FirebaseDatabase.getInstance().getReference();

        query2 = database2.child("games").child(codigoPartida).child("tabus");
        listener2 = query2.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                int count = 0;
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    count++;
                }
                if(count >= numJugadores * 5) {
                    OpenEntreRondas();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

    }

    public void OpenEntreRondas(){
        query.removeEventListener(listener);
        query2.removeEventListener(listener2);
        Intent intent = new Intent(this, EntreRondas.class);
        intent.putExtra("master", master);
        intent.putExtra("codigopartida", codigoPartida);
        intent.putExtra("numjugadores", numJugadores);
        startActivity(intent);
        finish();
    }
    public void ActualizarEquipo(DatosJugadores datos){
        equipoTV.setText(datos.getEquipo());
        j1TV.setText(datos.getNombreJ1());
        j2TV.setText(datos.getNombreJ2());
        j3TV.setText(datos.getNombreJ3());
    }

    public void InsertarTabus(View view) {
        int count = 0;
        String[] tabus = {tabu1ET.getText().toString(), tabu2ET.getText().toString(), tabu3ET.getText().toString(), tabu4ET.getText().toString(), tabu5ET.getText().toString()};
        for(int i=0; i<tabus.length; i++){
            if(tabus[i].equals(tabus[0]))
                count++;
            if(tabus[i].equals(tabus[1]))
                count++;
            if(tabus[i].equals(tabus[2]))
                count++;
            if(tabus[i].equals(tabus[3]))
                count++;
            if(tabus[i].equals(tabus[4]))
                count++;
        }
        if(count == 5) {
            DatabaseReference mPostReference = FirebaseDatabase.getInstance().getReference("games").child(codigoPartida).child("tabus");
            mPostReference.child(tabu1ET.getText().toString() + "_" + getMacAddress()).setValue(0);
            mPostReference.child(tabu2ET.getText().toString() + "_" + getMacAddress()).setValue(0);
            mPostReference.child(tabu3ET.getText().toString() + "_" + getMacAddress()).setValue(0);
            mPostReference.child(tabu4ET.getText().toString() + "_" + getMacAddress()).setValue(0);
            mPostReference.child(tabu5ET.getText().toString() + "_" + getMacAddress()).setValue(0);
        }else{
            //Error: Tabus repetidos
        }
    }
}