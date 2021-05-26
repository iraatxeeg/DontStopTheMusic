package com.example.dontstopthemusic.Login_Registro;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.lifecycle.Observer;
import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
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
import android.widget.Toast;

import com.example.dontstopthemusic.ConexionesBD.ConexionRegistro;
import com.example.dontstopthemusic.ConexionesBD.ConexionToken;
import com.example.dontstopthemusic.PantallaPrincipal.PantallaPrincipalActivity;
import com.example.dontstopthemusic.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class RegistroFotoActivity extends AppCompatActivity {

    ImageView img;
    String username;
    String password;

    private static final int CODIGO_PERMISOS_CAMERA = 1;
    private static final int CODIGO_PERMISOS_GALERIA = 2;
    private static final String[] CAMERA_PERMISO = {Manifest.permission.CAMERA};
    private static final String[] GALERIA_PERMISO = {Manifest.permission.READ_EXTERNAL_STORAGE};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro1);
        setSupportActionBar(findViewById(R.id.labarraRegistro1));

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            username = extras.getString("username");
            password = extras.getString("password");
        }

        Button btnSeleccionar = findViewById(R.id.btnSeleccionar);
        Button btnSacar = findViewById(R.id.btnSacar);
        img = (ImageView) findViewById(R.id.fotoPerfilRegistro);

        // Establecemos la foto de perfil por defecto
        img.setImageResource(R.drawable.fotoperfil);

        // Boton para seleccionar de la galeria
        btnSeleccionar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Se comprobara si el usuario ha concedido los permisos necesarios
                if (comprobarPermisosGaleria()){
                    Intent elIntentGal = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(elIntentGal, 9999);
                }
            }
        });

        // Boton para sacar foto
        btnSacar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Se comprobara si el usuario ha concedido los permisos necesarios
                if (comprobarPermisosCamara()){
                    dispatchTakePictureIntent();
                }
            }
        });

    }

    // Este metodo comprobara si el usuario ha concedido los permisos del acceso a la galeria
    private boolean comprobarPermisosGaleria() {
        try {
            int permisoG = ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);
            if (permisoG != PackageManager.PERMISSION_GRANTED) {
                // Forzamos los permisos
                ActivityCompat.requestPermissions(this,GALERIA_PERMISO,CODIGO_PERMISOS_GALERIA);
            } else {
                // El usuario ya ha concedido los permisos
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, 8888);
        }
    }

    // Este metodo comprobara si el usuario ha concedido los permisos de la camara
    private boolean comprobarPermisosCamara() {
        try {
            int permiso = ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA);
            if (permiso != PackageManager.PERMISSION_GRANTED) {
                // Forzamos los permisos
                ActivityCompat.requestPermissions(this,CAMERA_PERMISO,CODIGO_PERMISOS_CAMERA);
            } else {
                // El usuario ya ha concedido los permisos
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }



    // Sobreescribiremos este metodo para saber si el usuario ha aceptado o denegado los permisos
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case CODIGO_PERMISOS_CAMERA:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permiso concedido
                    dispatchTakePictureIntent();
                } else {
                    // Permiso denegado
                    int tiempo= Toast.LENGTH_SHORT;
                    Toast aviso = Toast.makeText(getApplicationContext(), "Debes conceder los permisos de cámara", tiempo);
                    aviso.setGravity(Gravity.BOTTOM| Gravity.CENTER, 0, 0);
                    aviso.show();
                } break;
            case CODIGO_PERMISOS_GALERIA:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permiso concedido
                    Intent elIntentGal = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(elIntentGal, 9999);
                } else {
                    // Permiso denegado
                    int tiempo= Toast.LENGTH_SHORT;
                    Toast aviso = Toast.makeText(getApplicationContext(), "Debes conceder los permisos de la galería", tiempo);
                    aviso.setGravity(Gravity.BOTTOM| Gravity.CENTER, 0, 0);
                    aviso.show();
                } break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Imagen de la camara
        if (requestCode == 8888 && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap laminiatura = (Bitmap) extras.get("data");
            File eldirectorio = this.getFilesDir();
            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
            String nombrefichero = "IMG_" + timeStamp + "_";
            File imagenFich = new File(eldirectorio, nombrefichero + ".jpg");
            OutputStream os;

            try {
                img.setImageBitmap(laminiatura);
                os = new FileOutputStream(imagenFich);
                laminiatura.compress(Bitmap.CompressFormat.JPEG, 100, os);
                os.flush();
                os.close();
            } catch (Exception e) {

            }
        }

        // Imagen de la galeria
        if (requestCode == 9999 && resultCode == RESULT_OK) {
            Uri imagenSeleccionada = data.getData();
            img.setImageURI(imagenSeleccionada);
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menuregistro1,menu);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id=item.getItemId();
        if (id == R.id.opcionGuardar) {

            //Instancia de FireBase
            FirebaseStorage storage = FirebaseStorage.getInstance();
            // Crear una storage reference de nuestra app
            StorageReference storageRef = storage.getReference();
            // Crear una referencia a "fotoUser.jpg" siendo User el nombre de usuario
            String ref = "FotosPerfil/foto" + username + ".jpg";
            StorageReference fotoRef = storageRef.child(ref);

            //Transformar el ImageView a bytes
            BitmapDrawable bitmapDrawablefto = (BitmapDrawable) img.getDrawable();
            Bitmap bitmapFto = bitmapDrawablefto.getBitmap();
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmapFto.compress(Bitmap.CompressFormat.PNG, 100, stream);
            byte[] data = stream.toByteArray();

            UploadTask uploadTask = fotoRef.putBytes(data);
            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    // Hacer algo cuando ocurra un error en la subida
                    int tiempo= Toast.LENGTH_SHORT;
                    String texto = "Ha ocurrido un error al guardar la foto de perfil";
                    Toast aviso = Toast.makeText(getApplicationContext(), texto, tiempo);
                    aviso.setGravity(Gravity.BOTTOM| Gravity.CENTER, 0, 0);
                    aviso.show();
                }
            });


            Data datos = new Data.Builder()
                    .putString("username", username)
                    .putString("password",password)
                    .build();


            OneTimeWorkRequest otwr = new OneTimeWorkRequest.Builder(ConexionRegistro.class)
                    .setInputData(datos).build();

            WorkManager.getInstance(this).getWorkInfoByIdLiveData(otwr.getId())
                    .observe(this, new Observer<WorkInfo>() {
                        @Override
                        public void onChanged(WorkInfo workInfo) {
                            if (workInfo != null && workInfo.getState().isFinished()) {
                                if (workInfo.getOutputData().getString("resultado").equals("true")) {
                                    Intent iPrincipal = new Intent(getBaseContext(), PantallaPrincipalActivity.class);
                                    iPrincipal.putExtra("username", username);
                                    startActivity(iPrincipal);
                                    finish();
                                    // Conocer el token del dispositivo y subirlo
                                    FirebaseMessaging.getInstance().getToken()
                                            .addOnCompleteListener(new OnCompleteListener<String>() {
                                                @Override
                                                public void onComplete(@NonNull Task<String> task) {
                                                    if (!task.isSuccessful()) {
                                                        return;
                                                    }
                                                    String token = task.getResult();
                                                    registrarToken(token);
                                                }
                                            });

                                }
                            }
                        }
                    });
            WorkManager.getInstance(this).enqueue(otwr);



        }
        return super.onOptionsItemSelected(item);
    }

    private void registrarToken(String token) {

        Data datos = new Data.Builder()
                .putString("username", username)
                .putString("token",token)
                .build();

        OneTimeWorkRequest otwr = new OneTimeWorkRequest.Builder(ConexionToken.class)
                .setInputData(datos).build();

        WorkManager.getInstance(this).enqueue(otwr);
    }

}