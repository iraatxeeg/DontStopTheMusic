package com.example.dontstopthemusic.Login_Registro;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;

import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.example.dontstopthemusic.ConexionesBD.ConexionLogin;
import com.example.dontstopthemusic.Main.MainActivity;
import com.example.dontstopthemusic.PantallaPrincipal.PantallaPrincipalActivity;
import com.example.dontstopthemusic.R;

public class LoginActivity extends AppCompatActivity {

    // Actividad para logearse con username-password
    TextView txtUsuario;
    TextView txtPassword;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        setSupportActionBar(findViewById(R.id.labarraLogin));
        txtUsuario = findViewById(R.id.editTextUsuarioLogin);
        txtPassword = findViewById(R.id.editTextContraseñaLogin);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menulogin,menu);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        String txtUser = txtUsuario.getText().toString();
        String txtPass = txtPassword.getText().toString();

        if (id == R.id.opcionEntrar) {
            if (txtUser.equals("") || txtPass.equals("")) {
                String text = "Rellena todos los campos";
                Toast toast = Toast.makeText(this, text, Toast.LENGTH_LONG);
                toast.setGravity(Gravity.BOTTOM | Gravity.CENTER, 0, 0);
                toast.show();
            } else if (txtUser.length() > 50) {
                String text = "Username demasiado largo";
                Toast toast = Toast.makeText(this, text, Toast.LENGTH_LONG);
                toast.setGravity(Gravity.BOTTOM | Gravity.CENTER, 0, 0);
                toast.show();
            } else {
                Data datos = new Data.Builder()
                        .putString("username", txtUser)
                        .putString("password", txtPass)
                        .build();

                OneTimeWorkRequest otwr = new OneTimeWorkRequest.Builder(ConexionLogin.class)
                        .setInputData(datos).build();

                WorkManager.getInstance(this).getWorkInfoByIdLiveData(otwr.getId())
                        .observe(this, new Observer<WorkInfo>() {
                            @Override
                            public void onChanged(WorkInfo workInfo) {
                                if (workInfo != null && workInfo.getState().isFinished()) {

                                    if (workInfo.getOutputData().getString("resultado").equals("logOK")) {
                                        // Login correcto
                                        Intent iPrincipal = new Intent(getBaseContext(), PantallaPrincipalActivity.class);
                                        iPrincipal.putExtra("username", txtUser);
                                        startActivity(iPrincipal);
                                        finish();


                                    } else { // Incorrecta -> Mostrar Dialog de error
                                        String text = "Inicio de sesión erróneo";
                                        Toast toast = Toast.makeText(getBaseContext(), text, Toast.LENGTH_LONG);
                                        toast.setGravity(Gravity.BOTTOM | Gravity.CENTER, 0, 0);
                                        toast.show();
                                    }
                                }
                            }
                        });
                WorkManager.getInstance(this).enqueue(otwr);
            }
        } else { // Opción cancelar -> volver al Main
        Intent iMain = new Intent(this, MainActivity.class);
        startActivity(iMain);
        finish();
    }
        return super.onOptionsItemSelected(item);
    }
}