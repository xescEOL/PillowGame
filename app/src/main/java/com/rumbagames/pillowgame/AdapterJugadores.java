package com.rumbagames.pillowgame;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.google.firebase.database.DataSnapshot;

import java.util.List;

/**
 * Adaptador de leads
 */
public class AdapterJugadores extends ArrayAdapter<DatosJugadores> {
    public AdapterJugadores(Context context, List<DatosJugadores> objects) {
        super(context, 0, objects);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @Nullable ViewGroup parent) {

        View listaPersonalizada = convertView;

        if(listaPersonalizada == null){
            listaPersonalizada = LayoutInflater.from(getContext()).inflate(R.layout.list_item_equipo, parent, false);
        }

        // Referencias UI.
        TextView equipo = (TextView) listaPersonalizada.findViewById(R.id.equipotxt);
        TextView j1TV = (TextView) listaPersonalizada.findViewById(R.id.jugador1);
        TextView j2TV = (TextView) listaPersonalizada.findViewById(R.id.jugador2);
        TextView j3TV = (TextView) listaPersonalizada.findViewById(R.id.jugador3);
        ImageView bg = (ImageView) listaPersonalizada.findViewById(R.id.equipobg);
        RelativeLayout circuloly = (RelativeLayout) listaPersonalizada.findViewById(R.id.circulo_ly);
        TextView equiponuevo = (TextView) listaPersonalizada.findViewById(R.id.equiponuevo_tv);

        String j1 = "Vacio";
        String j2 = "Vacio";
        String j3 = "Vacio";

        // Lead actual.
        /*for (int i = 0; i < getCount(); ++i) {
            DatosJugadores datos = getItem(i);
            if (datos.getEquipo().equals(position + "")) {
                if (j1.equals("Vacio"))
                    j1 = datos.getNombre();
                else if (j2.equals("Vacio"))
                    j2 = datos.getNombre();
                else if (j3.equals("Vacio"))
                    j3 = datos.getNombre();
            }
        }*/

        // Setup.
        //equipo.setText(getItem(position).getEquipo());
        equipo.setText((position + 1) + "");
        j1TV.setText(getItem(position).getNombreJ1());
        j2TV.setText(getItem(position).getNombreJ2());
        j3TV.setText(getItem(position).getNombreJ3());



        if(getItem(position).getNombreJ1().equals("") && getItem(position).getNombreJ2().equals("") && getItem(position).getNombreJ3().equals("")) {
            bg.setImageResource(R.drawable.equiponuevo);
            circuloly.setVisibility(View.INVISIBLE);
            equiponuevo.setVisibility(View.VISIBLE);
        }else if(!getItem(position).getNombreJ1().equals("") && getItem(position).getNombreJ2().equals("") && getItem(position).getNombreJ3().equals("")){
            circuloly.setVisibility(View.VISIBLE);
            equiponuevo.setVisibility(View.INVISIBLE);
            j1TV.setTextColor(Color.WHITE);
            switch(position) {
                case 0 :
                    bg.setImageResource(R.drawable.lista1r);
                    break;
                case 1 :
                    bg.setImageResource(R.drawable.lista2r);
                    break;
                case 2 :
                    bg.setImageResource(R.drawable.lista3r);
                    break;
                default :
                    bg.setImageResource(R.drawable.lista1r);
            }
        }else{
            circuloly.setVisibility(View.VISIBLE);
            equiponuevo.setVisibility(View.INVISIBLE);
            j1TV.setTextColor(ContextCompat.getColor(getContext(), R.color.paleta_negro));
            switch(position) {
                case 0 :
                    bg.setImageResource(R.drawable.lista1y);
                    break;
                case 1 :
                    bg.setImageResource(R.drawable.lista2y);
                    break;
                case 2 :
                    bg.setImageResource(R.drawable.lista3y);
                    break;
                default :
                    bg.setImageResource(R.drawable.lista1y);
            }
        }

        return listaPersonalizada;
    }
}
