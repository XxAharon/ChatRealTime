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

        entrada();
    }

    private void entrada() {
        textInicial.animate()
                .translationX(500f)
                .setDuration(1500)
                .withEndAction(() -> {
                    textInicial.setText("");
                    Toast.makeText(this, "Bienvenido", Toast.LENGTH_SHORT).show();
                })
                .start();
    }
}