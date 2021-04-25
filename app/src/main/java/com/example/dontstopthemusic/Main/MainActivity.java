package com.example.dontstopthemusic.Main;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.Button;

import com.example.dontstopthemusic.Login_Registro.LoginActivity;
import com.example.dontstopthemusic.Login_Registro.RegistroActivity;
import com.example.dontstopthemusic.R;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setSupportActionBar(findViewById(R.id.labarraMain));

        // Botón para ir a LOGIN
        Button btnEntrar = findViewById(R.id.btnEntrar);
        btnEntrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent iLogin = new Intent(getBaseContext(), LoginActivity.class);
                startActivity(iLogin);
                finish();
            }
        });

        // Botón para ir a REGISTRO
        Button btnRegistro = findViewById(R.id.btnRegistro);
        btnRegistro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent iRegistro = new Intent(getBaseContext(), RegistroActivity.class);
                startActivity(iRegistro);
                finish();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menumain,menu);
        return true;
    }
}