package com.example.dontstopthemusic.Login_Registro;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.Observer;
import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.example.dontstopthemusic.ConexionesBD.ConexionRegistro;
import com.example.dontstopthemusic.Dialogs.ClaseDialogCamposSinRellenar;
import com.example.dontstopthemusic.Dialogs.ClaseDialogPasswordError;
import com.example.dontstopthemusic.Main.MainActivity;
import com.example.dontstopthemusic.R;

public class RegistroActivity extends AppCompatActivity {

    TextView txtUsuario;
    TextView txtContraseña;
    TextView txtContraseña1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro);
        setSupportActionBar(findViewById(R.id.labarraRegistro));
        txtUsuario = findViewById(R.id.txtRegistroUsuario);
        txtContraseña = findViewById(R.id.txtRegistroContraseña);
        txtContraseña1 = findViewById(R.id.txtRegistroContraseña1);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menuregistro, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.opcionSiguiente) { // Opción Siguiente -> Foto
            // Si alguno de los campos no está rellenado
            if (txtUsuario.getText().toString().equals("") ||
                    txtContraseña.getText().toString().equals("") ||
                    txtContraseña1.getText().toString().equals("")) {
                DialogFragment dialogoAlerta = new ClaseDialogCamposSinRellenar();
                dialogoAlerta.show(getSupportFragmentManager(), "CamposSinRellenar");
            } else if (txtContraseña.getText().toString().equals(txtContraseña1.getText().toString())) {
                // Todos los campos rellenados pero las contraseñas no coinciden
                DialogFragment dialogoAlerta = new ClaseDialogPasswordError();
                dialogoAlerta.show(getSupportFragmentManager(), "PasswordError");
            } else {
                Data datos = new Data.Builder().putString("usuario", txtUsuario.getText().toString())
                        .putString("contraseña", txtContraseña.getText().toString()).build();
                OneTimeWorkRequest otwr = new OneTimeWorkRequest.Builder(ConexionRegistro.class)
                        .setInputData(datos).build();
                WorkManager.getInstance(this).getWorkInfoByIdLiveData(otwr.getId())
                        .observe(this, new Observer<WorkInfo>() {
                            @Override
                            public void onChanged(WorkInfo workInfo) {
                                if (workInfo != null && workInfo.getState().isFinished()) {
                                    if (workInfo.getOutputData().getString("resultado") != null) {

                                    } else { // Mostrar Dialog de error

                                    }
                                }
                            }
                        });
            }
        } else { // Opción cancelar -> volver al Main
            Intent iMain = new Intent(this, MainActivity.class);
            startActivity(iMain);
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

}