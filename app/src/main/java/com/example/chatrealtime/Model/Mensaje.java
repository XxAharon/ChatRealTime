package com.example.chatrealtime.Model;

public class Mensaje {
    private String emisor;
    private String contenido;
    private String hora;

    public Mensaje() {

    }

    public Mensaje(String contenido, String emisor, String hora) {
        this.emisor = emisor;
        this.contenido = contenido;
        this.hora = hora;
    }

    // Getters
    public String getEmisor() {
        return emisor;
    }

    public String getContenido() {
        return contenido;
    }

    public String getHora() {
        return hora;
    }
}
