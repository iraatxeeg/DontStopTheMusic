package com.example.dontstopthemusic.Login_Registro;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.example.dontstopthemusic.R;

public class Registro1Activity extends AppCompatActivity {

    ImageView img;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro1);
        setSupportActionBar(findViewById(R.id.labarraRegistro1));

        Button btnSeleccionar = findViewById(R.id.btnSeleccionar);
        Button btnSacar = findViewById(R.id.btnSacar);
        img = findViewById(R.id.fotoPerfilRegistro);

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
                if (ContextCompat.checkSelfPermission(getBaseContext(), Manifest.permission.CAMERA)
                        != PackageManager.PERMISSION_GRANTED) {

                } else {
                    Intent elIntentFoto= new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(elIntentFoto, 456);
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
            img.setImageBitmap(laminiatura);
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
            // comprobar si ya existe

        } else { // atrás

        }
        return super.onOptionsItemSelected(item);
    }
}