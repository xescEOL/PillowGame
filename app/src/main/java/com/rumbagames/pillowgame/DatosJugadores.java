package com.rumbagames.pillowgame;

public class DatosJugadores {

    public String nombre = "";
    public String nombreJ1 = "";
    public String idJ1 = "";
    public String nombreJ2 = "";
    public String idJ2 = "";
    public String nombreJ3 = "";
    public String idJ3 = "";
    public String equipo = "";

    public DatosJugadores(){
    }

    public DatosJugadores(String nombre1, String nombre2, String nombre3){
        this.nombreJ1 = nombre1;
        this.nombreJ2 = nombre2;
        this.nombreJ3 = nombre3;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getNombreJ1() {
        return nombreJ1;
    }

    public void setNombreJ1(String nombre) {
        this.nombreJ1 = nombre;
    }

    public String getIdJ1() {
        return idJ1;
    }

    public void setIdJ1(String id) {
        this.idJ1 = id;
    }

    public String getNombreJ2() {
        return nombreJ2;
    }

    public void setNombreJ2(String nombre) {
        this.nombreJ2 = nombre;
    }

    public String getIdJ2() {
        return idJ2;
    }

    public void setIdJ2(String id) {
        this.idJ2 = id;
    }

    public String getNombreJ3() {
        return nombreJ3;
    }

    public void setNombreJ3(String nombre) {
        this.nombreJ3 = nombre;
    }

    public String getIdJ3() {
        return idJ3;
    }

    public void setIdJ3(String id) {
        this.idJ3 = id;
    }

    public String getEquipo() {
        return equipo;
    }

    public void setEquipo(String equipo) {
        this.equipo = equipo;
    }


}
