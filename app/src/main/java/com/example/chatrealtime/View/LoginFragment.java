package com.example.chatrealtime.View;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.example.chatrealtime.Network.FirebaseManager;
import com.example.chatrealtime.R;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;

public class LoginFragment extends Fragment {

    private static final int RC_SIGN_IN = 9001;
    private GoogleSignInClient mGoogleSignInClient;
    private Button btnGoogleLogin;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_login, container, false);
    }

    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        btnGoogleLogin = view.findViewById(R.id.btnGoogleLogin);
        Toast.makeText(getContext(), "Bienvenido", Toast.LENGTH_SHORT).show();

        GoogleSignInOptions config = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(getActivity(), config);

        btnGoogleLogin.setOnClickListener(v -> {
            Intent signInIntent = mGoogleSignInClient.getSignInIntent();
            startActivityForResult(signInIntent, RC_SIGN_IN);
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                String idToken = account.getIdToken();

                FirebaseManager.getInstance().autentificarConGoogle(idToken, new FirebaseManager.AuthCallback() {
                    @Override
                    public void onAuthSuccess() {
                        Toast.makeText(getContext(), "Autentificación exitosa", Toast.LENGTH_SHORT).show();
                        navegarAlChat();
                    }

                    @Override
                    public void onAuthError(String errorMessage) {
                        Toast.makeText(getContext(), "Error: " + errorMessage, Toast.LENGTH_SHORT).show();
                    }
                });
            } catch (ApiException e) {
                Toast.makeText(getContext(), "Fallo Google: " + e.getStatusCode(), Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
        }
    }

    private void navegarAlChat() {
        ListaContactosFragment listaContactosFragment = new ListaContactosFragment();
        getActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.main, listaContactosFragment)
                .commit();
    }
}