package com.rumbagames.pillowgame;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
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

public class JugandoActivity extends AppCompatActivity {

    String codigoPartida, tabu = "";
    DatosJugadores equipo = new DatosJugadores();
    ArrayList<DatosJugadores> equipos = new ArrayList<DatosJugadores>();
    ArrayList<String> tabus = new ArrayList<String>();
    TextView tiempoTV, tabuTV;
    Button cambiarBtn, acertadoBtn;
    CountDownTimer tiempo;
    int ronda, numJugadores;
    int[] jugadorJugando = new int[50];
    int equipoJugando = 0;
    boolean actualizarTabu = true;
    boolean master = false;
    Query query;
    ValueEventListener listener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_jugando);

        tiempoTV = (TextView) findViewById(R.id.tiempoTV);
        tabuTV = (TextView) findViewById(R.id.tabuTV);
        cambiarBtn = (Button) findViewById(R.id.cambiarBoton);
        acertadoBtn = (Button) findViewById(R.id.acertadoBoton);

        master = getIntent().getExtras().getBoolean("master", false);
        codigoPartida = getIntent().getExtras().getString("codigopartida");
        numJugadores = getIntent().getExtras().getInt("numjugadores");
        //animacionJuego = (LottieAnimationView) findViewById(R.id.animacionJuego);

        empezarTiempo();

        DatabaseReference database = FirebaseDatabase.getInstance().getReference();
        query = database.child("games").child(codigoPartida);
        listener = query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                equipos.clear();
                equipoJugando = Integer.parseInt(dataSnapshot.child("equipojugando").getValue().toString());
                ronda = Integer.parseInt(dataSnapshot.child("ronda").getValue().toString());
                tabus.clear();
                for (DataSnapshot ds : dataSnapshot.child("tabus").getChildren()) {
                    if(Integer.parseInt(ds.getValue().toString()) != ronda) {
                        tabus.add(ds.getKey().toString());
                    }
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
                /*
                jugadorJugando1TV.setText(equipos.get(equipoJugando - 1).getNombreJ1());
                jugadorJugando2TV.setText(equipos.get(equipoJugando - 1).getNombreJ2());
                if(!equipos.get(equipoJugando - 1).getNombreJ3().equals(""))
                    jugadorJugando3TV.setText(equipos.get(equipoJugando - 1).getNombreJ3());
                else
                    jugadorJugando3TV.setVisibility(View.GONE);

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
                }*/
                if(actualizarTabu) {
                    boolean mismo = true;
                    if(tabus.size() > 0) {
                        while(mismo) {
                            String tabuAnterior = tabu;
                            tabu = tabus.get(new Random().nextInt(tabus.size()));
                            if (!tabuAnterior.equals(tabu))
                                mismo = false;
                        }
                    }else{
                        finJugando();
                    }
                    tabuTV.setText(tabu.split("_")[0]);
                    if(tabus.size() <= 1)
                        cambiarBtn.setEnabled(false);
                }
                actualizarTabu = false;
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

    }

    public void empezarTiempo(){
        tiempoTV.setVisibility(View.VISIBLE);
        tiempo = new CountDownTimer(45000, 1000) {
            @Override
            public void onTick(long l) {
                tiempoTV.setText(((int) l % 60000 / 1000) + "");
            }

            @Override
            public void onFinish() {
                tiempoTV.setText("FIN");
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    public void run() {
                        finJugando();
                    }
                }, 3000);

            }
        }.start();
    }

    public void finJugando(){
        query.removeEventListener(listener);
        if(tabus.size() <= 0)
            FirebaseDatabase.getInstance().getReference("games").child(codigoPartida).child("ronda").setValue(ronda + 1);
        int equipoJugandoUpdate = 0;
        if(equipoJugando >= equipos.size())
            equipoJugandoUpdate = 1;
        else
            equipoJugandoUpdate = equipoJugando + 1;
        int jugadorJugandoUpdate = 0;
        if(equipo.getIdJ3().equals("") && jugadorJugando[equipoJugando - 1] == 1)
            jugadorJugandoUpdate = 0;
        else if(jugadorJugando[equipoJugando - 1] == 2)
            jugadorJugandoUpdate = 0;
        else
            jugadorJugandoUpdate = jugadorJugando[equipoJugando - 1] + 1;

        FirebaseDatabase.getInstance().getReference("games").child(codigoPartida).child("jugadores").child(equipoJugando + "").child("jugadorjugando").setValue(jugadorJugandoUpdate);
        FirebaseDatabase.getInstance().getReference("games").child(codigoPartida).child("equipojugando").setValue(equipoJugandoUpdate);

        Intent intent = new Intent(this, EntreRondas.class);
        intent.putExtra("master", master);
        intent.putExtra("codigopartida", codigoPartida);
        intent.putExtra("numjugadores", numJugadores);
        startActivity(intent);
        finish();
    }


    public void correctoBoton (View view){
        actualizarTabu = true;
        FirebaseDatabase.getInstance().getReference("games").child(codigoPartida).child("tabus").child(tabu).setValue(ronda);
    }

    public void pasarBoton (View view){
        boolean mismo = true;
        while(mismo) {
            String tabuAnterior = tabu;
            tabu = tabus.get(new Random().nextInt(tabus.size()));
            if (!tabuAnterior.equals(tabu))
                mismo = false;
        }
        tabuTV.setText(tabu.split("_")[0]);
    }
}