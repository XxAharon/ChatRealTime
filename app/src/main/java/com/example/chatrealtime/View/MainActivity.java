package com.example.chatrealtime.View;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.chatrealtime.R;

public class MainActivity extends AppCompatActivity {

    private TextView textInicial;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        textInicial = findViewById(R.id.textViewInicial);

        vienvenida();
    }

    private void vienvenida() {
        textInicial.animate()
                .setStartDelay(800)
                .translationX(500f)
                .setDuration(2000)
                .withEndAction(() -> {
                    textInicial.setText("");
                    navegarAlChat();
                })
                .start();
    }

    private void navegarAlChat() {
        LoginFragment loginFragment = new LoginFragment();
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.main, loginFragment)
                .commit();
    }
}