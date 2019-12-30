package com.example.proyectodm;

public class Contacto {

    private String id;
    private String idcelular;
    private String nombre;
    private double latitud;
    private double longitud;

    public Contacto(){}


    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getIdcelular() {
        return idcelular;
    }

    public void setIdcelular(String idcel) {
        this.idcelular = idcel;
    }

    public double getLatitud() {
        return latitud;
    }

    public void setLatitud(double latitud) {
        this.latitud = latitud;
    }

    public double getLongitud() {
        return longitud;
    }

    public void setLongitud(double longitud) {
        this.longitud = longitud;
    }


}
