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
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.example.dontstopthemusic.ConexionesBD.ConexionExisteUsuario;
import com.example.dontstopthemusic.Main.MainActivity;
import com.example.dontstopthemusic.R;

public class RegistroActivity extends AppCompatActivity {

    // Actividad para que el usuario introduzca username-password para registrarse
    TextView txtUsername;
    TextView txtPassword;
    TextView txtPassword1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // En el caso de que lleguemos a esta actividad desde RegistroFoto, recogeremos el
        // nombre de usuario elegido previamente y lo volveremos a mostrar
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            txtUsername.setText(extras.getString("username"));
        }

        setContentView(R.layout.activity_registro);
        setSupportActionBar(findViewById(R.id.labarraRegistro));
        txtUsername = findViewById(R.id.editTextUsuarioRegistro);
        txtPassword = findViewById(R.id.editTextContrase├▒aRegistro);
        txtPassword1 = findViewById(R.id.editTextContrase├▒aRegistro1);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menuregistro, menu);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.opcionSiguiente) { // Opci├│n Siguiente -> Foto

            // Si alguno de los campos no est├í rellenado
            if (txtUsername.getText().toString().equals("") ||
                    txtPassword.getText().toString().equals("") ||
                    txtPassword1.getText().toString().equals("")) {
                String text = "Rellena todos los campos";
                Toast toast = Toast.makeText(this, text, Toast.LENGTH_LONG);
                toast.setGravity(Gravity.BOTTOM | Gravity.CENTER, 0, 0);
                toast.show();
            } else if (txtUsername.length() > 50) {
                String text = "Username demasiado largo";
                Toast toast = Toast.makeText(this, text, Toast.LENGTH_LONG);
                toast.setGravity(Gravity.BOTTOM | Gravity.CENTER, 0, 0);
                toast.show();
            } else if (!txtPassword.getText().toString().equals(txtPassword1.getText().toString())) {
                // Todos los campos rellenados pero las contrase├▒as no coinciden
                String text = "Las contrase├▒as no coinciden";
                Toast toast = Toast.makeText(this, text, Toast.LENGTH_LONG);
                toast.setGravity(Gravity.BOTTOM | Gravity.CENTER, 0, 0);
                toast.show();
            } else { // Pasar a la siguiente activity -> Foto
                // comprobar si ya existe
                Data datos = new Data.Builder()
                        .putString("username", txtUsername.getText().toString())
                        .build();

                OneTimeWorkRequest otwrRegistro = new OneTimeWorkRequest.Builder(ConexionExisteUsuario.class)
                        .setInputData(datos).build();

                WorkManager.getInstance(this).getWorkInfoByIdLiveData(otwrRegistro.getId())
                        .observe(this, new Observer<WorkInfo>() {
                            @Override
                            public void onChanged(WorkInfo workInfo) {
                                if (workInfo != null && workInfo.getState().isFinished()) {
                                    if(workInfo.getOutputData().getString("resultado").equals("noexiste")) {
                                        // Usuario no existe, se puede crear
                                        Intent iSiguiente = new Intent(getBaseContext(), RegistroFotoActivity.class);
                                        iSiguiente.putExtra("username", txtUsername.getText().toString());
                                        iSiguiente.putExtra("password", txtPassword.getText().toString());
                                        startActivity(iSiguiente);
                                    }
                                    else {
                                        String text = "Ese username ya est├í en uso";
                                        Toast toast = Toast.makeText(getBaseContext(), text, Toast.LENGTH_LONG);
                                        toast.setGravity(Gravity.BOTTOM | Gravity.CENTER, 0, 0);
                                        toast.show();
                                    }
                                }
                            }
                        });
                WorkManager.getInstance(this).enqueue(otwrRegistro);
            }
        } else { // Opci├│n cancelar -> volver al Main
            Intent iMain = new Intent(this, MainActivity.class);
            startActivity(iMain);
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

}