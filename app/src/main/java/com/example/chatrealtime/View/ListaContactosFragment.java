package com.example.chatrealtime.View;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.chatrealtime.Adapter.ContactosAdapter;
import com.example.chatrealtime.Model.Usuario;
import com.example.chatrealtime.Network.FirebaseManager;
import com.example.chatrealtime.R;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ListaContactosFragment extends Fragment {

    private RecyclerView recyclerViewContactos;
    private Button btnCerrarSesion;
    private ProgressBar progressBar;
    private ContactosAdapter contactosAdapter;
    private List<Usuario> usuarios;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_lista_contactos, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @NonNull Bundle savedInstanceState) {
            super.onViewCreated(view, savedInstanceState);

        recyclerViewContactos = view.findViewById(R.id.recyclerViewContactos);
        progressBar = view.findViewById(R.id.progressBar);
        btnCerrarSesion = view.findViewById(R.id.btnCerrarSesion);

        recyclerViewContactos.setLayoutManager(new LinearLayoutManager(getContext()));
        usuarios = new ArrayList<>();

        cargarListaDeUsuarios();
        cargarListener();

    }

    private void cargarListaDeUsuarios() {
        progressBar.setVisibility(View.VISIBLE);

        FirebaseManager.getInstance().obtenerTodosLosUsuarios(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                usuarios.clear();
                String miUid = FirebaseManager.getInstance().getCurrentUser().getUid();

                for (DataSnapshot data : snapshot.getChildren()) {
                    Usuario user = data.getValue(Usuario.class);

                    if (user != null && user.getUid() != null) {
                        usuarios.add(user);
                    }
                }

                contactosAdapter = new ContactosAdapter(usuarios, new ContactosAdapter.OnUsuarioClickListener() {
                    @Override
                    public void onUsuarioClick(Usuario usuario) {
                        irAlChat(usuario);
                    }
                });

                recyclerViewContactos.setAdapter(contactosAdapter);
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(getContext(), "Error al cargar: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void irAlChat(Usuario usuarioDestino) {
        ChatFragment fragmentChat = new ChatFragment();

        Bundle args = new Bundle();
        args.putString("uid_destino", usuarioDestino.getUid());
        args.putString("nombre_destino", usuarioDestino.getNombre());
        fragmentChat.setArguments(args);

        getParentFragmentManager().beginTransaction()
                .replace(R.id.main, fragmentChat)
                .addToBackStack(null)
                .commit();
    }

    private void cargarListener() {
        btnCerrarSesion.setOnClickListener(v -> {

            // 1. Configuramos Google Sign In (Igual que en el LoginFragment)
            // Necesitamos esto para tener acceso al cliente que controla la sesión de Google
            GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestIdToken(getString(R.string.default_web_client_id))
                    .requestEmail()
                    .build();

            GoogleSignInClient googleSignInClient = GoogleSignIn.getClient(requireActivity(), gso);

            // 2. Cerramos la sesión de GOOGLE primero
            googleSignInClient.signOut().addOnCompleteListener(task -> {

                // 3. Una vez cerrado Google, cerramos la sesión de FIREBASE
                FirebaseManager.getInstance().singout();

                // 4. Verificamos y nos vamos
                if(FirebaseManager.getInstance().getCurrentUser() == null) {
                    navegacionLogin();
                    Toast.makeText(getContext(), "Sesión cerrada correctamente", Toast.LENGTH_SHORT).show();
                }
            });
        });
    }

    private void navegacionLogin() {
        LoginFragment loginFragment = new LoginFragment();
        getParentFragmentManager().beginTransaction()
                .replace(R.id.main, loginFragment)
                .commit();
    }
}