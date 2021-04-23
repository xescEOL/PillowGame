package com.rumbagames.pillowgame;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;
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

public class EntreRondas extends AppCompatActivity {
    String codigoPartida;
    DatosJugadores equipo = new DatosJugadores();
    ArrayList<DatosJugadores> equipos = new ArrayList<DatosJugadores>();
    int[] jugadorJugando = new int[50];
    String[] tabus;
    boolean master = false;
    int numJugadores = 0;
    int equipoJugando = 0;
    int ronda = 0;
    Button listoBut;
    TextView equipoJugandoTV, jugadorJugando1TV, jugadorJugando2TV, jugadorJugando3TV, tiempoTV;
    LottieAnimationView animacionJuego;
    CountDownTimer tiempo;
    Query query;
    ValueEventListener listener;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_entrerondas);

        master = getIntent().getExtras().getBoolean("master", false);
        codigoPartida = getIntent().getExtras().getString("codigopartida");
        numJugadores = getIntent().getExtras().getInt("numjugadores");

        tabus = new String[numJugadores * 5];

        listoBut = (Button) findViewById(R.id.listoBut);
        equipoJugandoTV = (TextView) findViewById(R.id.equipoTV);
        jugadorJugando1TV = (TextView) findViewById(R.id.j1TV);
        jugadorJugando2TV = (TextView) findViewById(R.id.j2TV);
        jugadorJugando3TV = (TextView) findViewById(R.id.j3TV);
        tiempoTV = (TextView) findViewById(R.id.tiempoTV);
        animacionJuego = (LottieAnimationView) findViewById(R.id.animacionJuego);



        DatabaseReference database = FirebaseDatabase.getInstance().getReference();
        query = database.child("games").child(codigoPartida);
        listener = query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                equipos.clear();
                equipoJugando = Integer.parseInt(dataSnapshot.child("equipojugando").getValue().toString());
                ronda = Integer.parseInt(dataSnapshot.child("ronda").getValue().toString());
                int countTabus = 0;
                for (DataSnapshot ds : dataSnapshot.child("tabus").getChildren()) {
                    tabus[countTabus] = ds.getKey();
                    countTabus++;
                }
                int count = 1;
                for (DataSnapshot ds : dataSnapshot.child("jugadores").getChildren()) {
                    count = Integer.parseInt(ds.getKey());
                    for (DataSnapshot ds2 : ds.getChildren()) {
                        if (ds2.getKey().equals("jugadorjugando")) {
                            jugadorJugando[count - 1] = Integer.parseInt(ds2.getValue().toString());
                        } else {
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
                                } else {
                                    if (equipo.getEquipo().equals(ds.getKey())) {
                                        equipo = arrival;
                                    }
                                }
                                equipos.set(contFor, arrival);
                            }
                        }
                    }
                }
                equipoJugandoTV.setText(equipoJugando + "");
                jugadorJugando1TV.setText(equipos.get(equipoJugando - 1).getNombreJ1());
                jugadorJugando2TV.setText(equipos.get(equipoJugando - 1).getNombreJ2());
                if(!equipos.get(equipoJugando - 1).getNombreJ3().equals(""))
                    jugadorJugando3TV.setText(equipos.get(equipoJugando - 1).getNombreJ3());
                else
                    jugadorJugando3TV.setVisibility(View.GONE);

                boolean juegas = false;
                listoBut.setVisibility(View.GONE);
                if (jugadorJugando[Integer.parseInt(equipo.getEquipo()) - 1] == 0) {
                    if (equipos.get(equipoJugando - 1).getIdJ1().equals(getMacAddress())) {
                        juegas = true;
                    }
                } else if (jugadorJugando[Integer.parseInt(equipo.getEquipo()) - 1] == 1) {
                    if (equipos.get(equipoJugando - 1).getIdJ2().equals(getMacAddress())) {
                        juegas = true;
                    }
                } else if (jugadorJugando[Integer.parseInt(equipo.getEquipo()) - 1] == 2) {
                    if (equipos.get(equipoJugando - 1).getIdJ3().equals(getMacAddress())) {
                        juegas = true;
                    }
                }
                if(!juegas){
                    empezarTiempo();
                }else{
                    if(ronda == 0 || ronda == 2 || ronda == 4 || ronda == 6) {
                        listoBut.setVisibility(View.VISIBLE);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

        DatabaseReference database2 = FirebaseDatabase.getInstance().getReference();

        Query query2 = database2.child("games").child(codigoPartida).child("tabus");
        query2.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                int count = 0;
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    count++;
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

    }

    public void openCreate(View view) {
        Intent intent = new Intent(this, EsperandoJugadores.class);
        intent.putExtra("master", false);
        startActivity(intent);
    }

    public void empezarTiempo(){
        tiempoTV.setVisibility(View.VISIBLE);
       tiempo = new CountDownTimer(45500, 1000) {
           @Override
           public void onTick(long l) {
                tiempoTV.setText(((int) l % 60000 / 1000) + "");
           }

           @Override
           public void onFinish() {
                tiempoTV.setVisibility(View.INVISIBLE);
           }
       }.start();
    }

    public void entrarJugando(View view){
        if (ronda == 0) {
            query.removeEventListener(listener);
            FirebaseDatabase.getInstance().getReference("games").child(codigoPartida).child("ronda").setValue(1); //Descripcion entera
            Intent intent = new Intent(this, JugandoActivity.class);
            intent.putExtra("codigopartida", codigoPartida);
            intent.putExtra("numjugadores", numJugadores);
            intent.putExtra("master", master);
            startActivity(intent);
        }else if(ronda == 2){
            query.removeEventListener(listener);
            FirebaseDatabase.getInstance().getReference("games").child(codigoPartida).child("ronda").setValue(3); //dibuix
            Intent intent = new Intent(this, DibujarActivity.class);
            intent.putExtra("codigopartida", codigoPartida);
            intent.putExtra("numjugadores", numJugadores);
            intent.putExtra("master", master);
            startActivity(intent);
        }else if(ronda == 4){
            query.removeEventListener(listener);
            FirebaseDatabase.getInstance().getReference("games").child(codigoPartida).child("ronda").setValue(5); //una paraula
            Intent intent = new Intent(this, JugandoActivity.class);
            intent.putExtra("codigopartida", codigoPartida);
            intent.putExtra("numjugadores", numJugadores);
            intent.putExtra("master", master);
            startActivity(intent);
        }else if(ronda == 6){
            query.removeEventListener(listener);
            FirebaseDatabase.getInstance().getReference("games").child(codigoPartida).child("ronda").setValue(7); //mimica
            Intent intent = new Intent(this, JugandoActivity.class);
            intent.putExtra("codigopartida", codigoPartida);
            intent.putExtra("numjugadores", numJugadores);
            intent.putExtra("master", master);
            startActivity(intent);
        }
        this.finish();
    }
}
