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
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.example.dontstopthemusic.ConexionesBD.ConexionLogin;
import com.example.dontstopthemusic.Dialogs.ClaseDialogPasswordError;
import com.example.dontstopthemusic.Main.MainActivity;
import com.example.dontstopthemusic.PantallaPrincipal.PantallaPrincipalActivity;
import com.example.dontstopthemusic.R;

public class LoginActivity extends AppCompatActivity {

    TextView txtUsuario;
    TextView txtContraseña;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        setSupportActionBar(findViewById(R.id.labarraLogin));
        txtUsuario = findViewById(R.id.txtLoginUsuario);
        txtContraseña = findViewById(R.id.txtLoginContraseña);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menulogin,menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.opcionEntrar) {

            Data datos = new Data.Builder().putString("usuario", txtUsuario.getText().toString())
                    .putString("contraseña", txtContraseña.getText().toString()).build();
            OneTimeWorkRequest otwr = new OneTimeWorkRequest.Builder(ConexionLogin.class)
                    .setInputData(datos).build();
            WorkManager.getInstance(this).getWorkInfoByIdLiveData(otwr.getId())
                    .observe(this, new Observer<WorkInfo>() {
                        @Override
                        public void onChanged(WorkInfo workInfo) {
                            if (workInfo != null && workInfo.getState().isFinished()) {
                                if(workInfo.getOutputData().getString("resultado").equals("true")) {
                                    // Login correcto
                                    Intent iPrincipal = new Intent(getBaseContext(), PantallaPrincipalActivity.class);
                                    startActivity(iPrincipal);
                                    finish();


                                } else { // Incorrecta -> Mostrar Dialog de error
                                    DialogFragment dialogoAlerta = new ClaseDialogPasswordError();
                                    dialogoAlerta.show(getSupportFragmentManager(), "PasswordErrorRegistro");


                                }
                            }
                        }
                    });
            WorkManager.getInstance(this).enqueue(otwr);
        }
        return super.onOptionsItemSelected(item);
    }
}