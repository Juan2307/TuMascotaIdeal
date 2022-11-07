package com.foysal.official.Destinos;

public class Destinos {
    //Atributos
    private String nombre;
    private String email;

    //Constructor Vacio
    public Destinos() {
        super();
    }

    public Destinos(String nombre, String email) {
        this.nombre = nombre;
        this.email = email;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
