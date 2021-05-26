package com.example.dontstopthemusic.PantallaPrincipal;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.lifecycle.Observer;
import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.dontstopthemusic.ConexionesBD.ConexionCambiarContraseña;
import com.example.dontstopthemusic.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
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

public class ActivityPerfil extends AppCompatActivity {

    // En esta clase podremos consultar y modificar los datos de nuestro perfil

    String username;
    TextView txtUsername;
    TextView txtPassword;
    ImageView foto;
    EditText nuevaP;

    private static final int CODIGO_PERMISOS_CAMERA = 1;
    private static final int CODIGO_PERMISOS_GALERIA = 2;
    private static final String[] CAMERA_PERMISO = {Manifest.permission.CAMERA};
    private static final String[] GALERIA_PERMISO = {Manifest.permission.READ_EXTERNAL_STORAGE};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil);
        setSupportActionBar(findViewById(R.id.labarraPerfil));

        txtUsername = findViewById(R.id.txtPerfilUsername1);
        txtPassword = findViewById(R.id.txtCambiarPassword);
        foto = findViewById(R.id.imgFotoPerfil);

        // Recibimos el nombre de usuario del usuario
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            username = extras.getString("username");
        }

        // Poner el nombre de usuario en el campo correspondiente
        txtUsername.setText(username);

        // Recoger la foto del usuario
        recogerFoto();

        // Cambiar la contraseña del usuario
        txtPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { createDialogoCC().show(); }
        });

        // Actualizar foto de perfil
        ImageButton bCambiarFoto = (ImageButton) findViewById(R.id.iCambiarFoto);
        bCambiarFoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createDialogoCF().show();
            }
        });

        // Guardar foto de perfil
        ImageButton bGuardarFoto = (ImageButton) findViewById(R.id.bGuardarFoto);
        bGuardarFoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                guardarFoto();
            }
        });

    }

    // Se genara un dialogo para modificar la foto de perfil del usuario
    public AlertDialog createDialogoCF() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        View v = inflater.inflate(R.layout.dialogo_cambiarfotoperfil, null);
        builder.setView(v);
        builder.setNegativeButton("Cancelar", (dialog, which) -> dialog.cancel());

        Button bTFoto = (Button) v.findViewById(R.id.bTomarFotoPerfil);
        Button bSGaleria = (Button) v.findViewById(R.id.bSeleccionarGaleriaPerfil);

        // El usuario desea tomar una foto con su camara
        bTFoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Se comprobara si el usuario ha concedido los permisos necesarios
                if (comprobarPermisosCamara()){
                    dispatchTakePictureIntent();
                }
            }
        });

        // El usuario desea seleccionar una foto de su galeria
        bSGaleria.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Se comprobara si el usuario ha concedido los permisos necesarios
                if (comprobarPermisosGaleria()){
                    Intent elIntentGal = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(elIntentGal, 9999);
                }
            }
        });

        return builder.create();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
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
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            laminiatura.compress(Bitmap.CompressFormat.PNG, 100, stream);
            byte[] fototransformada = stream.toByteArray();
            String fotoen64 = Base64.encodeToString(fototransformada,Base64.DEFAULT);
            Uri.Builder builder = new Uri.Builder()
                    .appendQueryParameter("identificador", username)
                    .appendQueryParameter("imagen", fotoen64);
            String parametrosURL = builder.build().getEncodedQuery();
            try {
                foto.setImageBitmap(laminiatura);
                os = new FileOutputStream(imagenFich);
                laminiatura.compress(Bitmap.CompressFormat.JPEG, 100, os);
                os.flush();
                os.close();
            } catch (Exception e) { }
        }

        // Imagen de la galeria
        if (requestCode == 9999 && resultCode == RESULT_OK) {
            Uri imagenSeleccionada = data.getData();
            foto.setImageURI(imagenSeleccionada);
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
        } catch (Exception e) {e.printStackTrace();}
        return false;
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, 8888);
        }
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
        } catch (Exception e) {e.printStackTrace();}
        return false;
    }

    // Sobreescribiremos este metodo para saber si el usuario ha aceptado o denegado los permisos
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
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

    // Se genara un dialogo para modificar la contraseña del usuario
    private AlertDialog createDialogoCC() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Cambiar contraseña");
        LayoutInflater inflater = this.getLayoutInflater();
        View v = inflater.inflate(R.layout.dialogo_cambiarcontrasena, null);
        builder.setView(v);
        nuevaP = (EditText) v.findViewById(R.id.editNuevaPass);
        EditText repetirP = (EditText) v.findViewById(R.id.editNuevaPass2);

        builder.setPositiveButton("Cambiar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (nuevaP.getText().toString().length() == 0) {
                    // No se ha escrito nada
                    String text = "Rellena los campos";
                    Toast toast = Toast.makeText(getBaseContext(), text, Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.BOTTOM | Gravity.CENTER, 0, 0);
                    toast.show();
                } else {
                    if (nuevaP.getText().toString().equals(repetirP.getText().toString())) {
                        // Se modifica la contrasena
                        cambiarContraseña();
                    } else {
                        // Las contrasenas introducidas por el usuario no coincidedn
                        String text = "Las contraseñas no coinciden";
                        Toast toast = Toast.makeText(getBaseContext(), text, Toast.LENGTH_LONG);
                        toast.setGravity(Gravity.BOTTOM | Gravity.CENTER, 0, 0);
                        toast.show();
                    }
                }
            }
        });
        builder.setNegativeButton("Cancelar", (dialog, which) -> dialog.cancel());

        return builder.create();

    }

    // Realizar el cambio de contraseña en la base de datos
    private void cambiarContraseña() {

        Data datos = new Data.Builder()
                .putString("username", username)
                .putString("password", nuevaP.getText().toString())
                .build();

        OneTimeWorkRequest otwr = new OneTimeWorkRequest.Builder(ConexionCambiarContraseña.class)
                .setInputData(datos)
                .build();

        WorkManager.getInstance(getApplicationContext()).getWorkInfoByIdLiveData(otwr.getId())
                .observe(this, new Observer<WorkInfo>() {
                    @Override
                    public void onChanged(WorkInfo workInfo) {
                        if (workInfo != null && workInfo.getState().isFinished()) {
                            if (workInfo.getOutputData().getString("result").equals("done")) {
                                // Se ha actualizado la contraseña en la base de datos
                                String text = "Contraseña cambiada";
                                Toast toast = Toast.makeText(getBaseContext(), text, Toast.LENGTH_LONG);
                                toast.setGravity(Gravity.BOTTOM | Gravity.CENTER, 0, 0);
                                toast.show();
                                Intent o = new Intent (getBaseContext(), ActivityPerfil.class);
                                o.putExtra("username",username);
                                startActivity(o);
                                finish();
                            } else {
                                // Ha sucedido un eror
                                String text = "Ha ocurrido un error";
                                Toast toast = Toast.makeText(getBaseContext(), text, Toast.LENGTH_LONG);
                                toast.setGravity(Gravity.BOTTOM | Gravity.CENTER, 0, 0);
                                toast.show();
                            }
                        }
                    }
                });
        WorkManager.getInstance(getBaseContext()).enqueue(otwr);
    }

    private void recogerFoto() {

        String text = "Cargando datos";
        Toast toast = Toast.makeText(getBaseContext(), text, Toast.LENGTH_LONG);
        toast.setGravity(Gravity.BOTTOM | Gravity.CENTER, 0, 0);
        toast.show();

        //Instancia de FireBase
        FirebaseStorage storage = FirebaseStorage.getInstance();
        // Crear una storage reference de nuestra app
        StorageReference storageRef = storage.getReference();
        // Crear una referencia a "fotoUser.jpg" siendo User el nombre de usuario
        String ref = "FotosPerfil/foto" + username + ".jpg";
        StorageReference fotoRef = storageRef.child(ref);

        final long ONE_MEGABYBTE = 1024 * 1024;
        fotoRef.getBytes(ONE_MEGABYBTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                //Se ha devuelto la foto
                Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                foto.setImageBitmap(bitmap);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                // Ha ocurrido un error
                int tiempo= Toast.LENGTH_SHORT;
                String texto = "No se ha podido cargar la foto de perfil";
                Toast aviso = Toast.makeText(getApplicationContext(), texto, tiempo);
                aviso.setGravity(Gravity.BOTTOM| Gravity.CENTER, 0, 0);
                aviso.show();
                foto.setImageResource(R.drawable.fotoperfil);
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        return true;
    }

    private void guardarFoto() {

        //Instancia de FireBase
        FirebaseStorage storage = FirebaseStorage.getInstance();
        // Crear una storage reference de nuestra app
        StorageReference storageRef = storage.getReference();
        // Crear una referencia a "fotoUser.jpg" siendo User el nombre de usuario
        String ref = "FotosPerfil/foto" + username + ".jpg";
        StorageReference fotoRef = storageRef.child(ref);

        //Transformar el ImageView a bytes
        BitmapDrawable bitmapDrawablefto = (BitmapDrawable) foto.getDrawable();
        Bitmap bitmapFto = bitmapDrawablefto.getBitmap();
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmapFto.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] data = stream.toByteArray();

        UploadTask uploadTask = fotoRef.putBytes(data);
        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // Foto subida correctamente
                int tiempo= Toast.LENGTH_SHORT;
                String texto = "Foto de perfil actualizada";
                Toast aviso = Toast.makeText(getApplicationContext(), texto, tiempo);
                aviso.setGravity(Gravity.BOTTOM| Gravity.CENTER, 0, 0);
                aviso.show();
                Intent iPrin = new Intent(getBaseContext(), PantallaPrincipalActivity.class);
                iPrin.putExtra("username", username);
                startActivity(iPrin);
                finish();
            }
        }).addOnFailureListener(new OnFailureListener() {
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

    }

}