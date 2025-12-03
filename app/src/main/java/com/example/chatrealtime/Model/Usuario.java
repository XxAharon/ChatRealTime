package com.example.chatrealtime.Model;

public class Usuario {
    private String uid;
    private String nombre;
    private String email;
    private String fotoPerfil;

    // Constructor vacío requerido por Firebase
    public Usuario() {
    }

    public Usuario(String uid, String nombre, String email, String fotoPerfil) {
        this.uid = uid;
        this.nombre = nombre;
        this.email = email;
        this.fotoPerfil = fotoPerfil;
    }

    // Getters
    public String getUid() {
        return uid;
    }

    public String getNombre() {
        return nombre;
    }

    public String getEmail() {
        return email;
    }

    public String getFotoPerfil() {
        return fotoPerfil;
    }
}
