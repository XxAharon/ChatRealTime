package com.example.chatrealtime.Network;

import com.example.chatrealtime.Model.Usuario;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.EventListener;

public class FirebaseManager {
    private static FirebaseManager instance;
    private DatabaseReference databaseReference;
    private FirebaseAuth auth;

    public static synchronized FirebaseManager getInstance() {
        if(instance == null) {
            instance = new FirebaseManager();
        }
        return instance;
    }

    public interface AuthCallback {
        void onAuthSuccess();
        void onAuthError(String errorMessage);
    }

    public void autentificarConGoogle(String idToken, AuthCallback callback) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);

        auth.signInWithCredential(credential)
                .addOnCompleteListener(task -> {
                   if(task.isSuccessful()) {
                       guardarUsuarioEnBaseDeDatos(() -> {
                           callback.onAuthSuccess();
                       });
                   } else {
                       callback.onAuthError(task.getException().getMessage());
                   }
                });
    }

    private FirebaseManager() {
        databaseReference = FirebaseDatabase.getInstance().getReference();
        auth = FirebaseAuth.getInstance();
    }

    public FirebaseUser getCurrentUser() {
        return auth.getCurrentUser();
    }

    public void singout() {
        auth.signOut();
    }

    public void guardarUsuarioEnBaseDeDatos(Runnable onSucces) {
        FirebaseUser user = auth.getCurrentUser();

        if(user != null)     {
            String foto = (user.getPhotoUrl() != null) ? user.getPhotoUrl().toString() : "";

            Usuario usuario = new Usuario(
                    user.getUid(),
                    user.getDisplayName(),
                    user.getEmail(),
                    foto
            );

            databaseReference.child("users").child(user.getUid()).setValue(usuario)
                    .addOnSuccessListener(aVoid -> {
                        if(onSucces != null) { onSucces.run(); }
                    })
            .addOnFailureListener(e -> {
                System.out.println("Error al guardar el usuario: " + e.getMessage());
                if (onSucces != null) onSucces.run();
            });
        }
    }

    public void obtenerTodosLosUsuarios(ValueEventListener Listener) {
        databaseReference.child("users").addValueEventListener(Listener);
    }
}
