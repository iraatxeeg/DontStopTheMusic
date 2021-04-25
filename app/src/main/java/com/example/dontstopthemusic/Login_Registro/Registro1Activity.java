package com.example.dontstopthemusic.Login_Registro;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.Observer;
import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.dontstopthemusic.ConexionesBD.ConexionRegistro;
import com.example.dontstopthemusic.ConexionesBD.ConexionRegistro1;
import com.example.dontstopthemusic.Main.MainActivity;
import com.example.dontstopthemusic.R;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Registro1Activity extends AppCompatActivity {

    ImageView img;
    String usuario;
    String contraseña;
    boolean correcto = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro1);
        setSupportActionBar(findViewById(R.id.labarraRegistro1));

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            usuario = extras.getString("usuario");
            contraseña = extras.getString("contraseña");
        }

        Button btnSeleccionar = findViewById(R.id.btnSeleccionar);
        Button btnSacar = findViewById(R.id.btnSacar);
        img = (ImageView) findViewById(R.id.fotoPerfilRegistro);
        img.setImageResource(R.drawable.fondo);


        btnSeleccionar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent iSeleccionar = new Intent(Intent.ACTION_PICK,
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(iSeleccionar, 123);
            }
        });

        btnSacar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                    startActivityForResult(takePictureIntent, 456);
                }
            }
        });

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 123 && resultCode == RESULT_OK) {
            Uri imagenSeleccionada = data.getData();
            img.setImageURI(imagenSeleccionada);
        } if (requestCode == 456 && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap laminiatura = (Bitmap) extras.get("data");
            File elDirectorio = getFilesDir();
            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
            String nombrefichero = "IMG_" + timeStamp + "_";
            File imagenFich = new File(elDirectorio, nombrefichero + ".jpg");
            OutputStream os;
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            laminiatura.compress(Bitmap.CompressFormat.PNG, 100, stream);
            try {
                img.setImageBitmap(laminiatura);
                os = new FileOutputStream(imagenFich);
                laminiatura.compress(Bitmap.CompressFormat.JPEG, 100, os);
                os.flush();
                os.close();
            } catch (Exception e) {

            }
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menuregistro1,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id=item.getItemId();
        if (id == R.id.opcionGuardar) {
            Data datos = new Data.Builder().putString("usuario", usuario)
                    .putString("constraseña",contraseña).putString("foto", img.toString()).build();
            OneTimeWorkRequest otwrRegistro1 = new OneTimeWorkRequest.Builder(ConexionRegistro1.class)
                    .setInputData(datos).build();
            WorkManager.getInstance(this).getWorkInfoByIdLiveData(otwrRegistro1.getId())
                    .observe(this, new Observer<WorkInfo>() {
                        @Override
                        public void onChanged(WorkInfo workInfo) {
                            if (workInfo != null && workInfo.getState().isFinished()) {
                                if (workInfo.getOutputData().getString("resultado").equals("true")) {
                                    Intent iLogin = new Intent(getBaseContext(), LoginActivity.class);
                                    iLogin.putExtra("registro", "true");
                                    startActivity(iLogin);
                                    finish();
                                }
                            }
                        }
                    });
            WorkManager.getInstance(this).enqueue(otwrRegistro1);

        } else { // atrás
            Intent iRegistro = new Intent(getBaseContext(),RegistroActivity.class);
            startActivity(iRegistro);
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}