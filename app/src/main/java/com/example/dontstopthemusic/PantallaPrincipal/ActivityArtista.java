package com.example.dontstopthemusic.PantallaPrincipal;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.lifecycle.Observer;
import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;

import com.example.dontstopthemusic.ConexionesBD.ConexionInfoArtista;
import com.example.dontstopthemusic.ConexionesBD.ConexionRegistro;
import com.example.dontstopthemusic.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class ActivityArtista extends AppCompatActivity {

    // Actividad para mostrar la información de cada artista

    String idArtista;
    TextView txtNombre;
    TextView txtFechaNac;
    TextView txtLugarNac;
    ImageView imagen;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_artista);


        Bundle extras = getIntent().getExtras();
        idArtista = "";
        if (extras != null) {
            idArtista = extras.getString("idArtista");
        }


        String nombre = "";
        String nacimiento = "";
        String lugar = "";

        cargarInfoArtista();

        txtNombre = findViewById(R.id.txtNombreArtista1);
        txtNombre.setText(nombre);
        txtFechaNac = findViewById(R.id.txtNacimientoArtista1);
        txtFechaNac.setText(nacimiento);
        txtLugarNac = findViewById(R.id.txtNacimientoArtistaLugar);
        txtLugarNac.setText(lugar);

        imagen = findViewById(R.id.imageViewArtista);
        cargarFoto();

        ////////////////////////////////////////////////////////////////////////////////////////////
        // NOTIFICACIONES LOCALES
        NotificationManager elManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationCompat.Builder elBuilder = new NotificationCompat.Builder(this, "Canal1");
        elBuilder.setSmallIcon(R.drawable.logo).setContentTitle("¿Por qué no revisas...")
                .setVibrate(new long[] {0, 500})
                .setAutoCancel(true);
        if (idArtista.equals("0")) elBuilder.setContentText("Heartbreak Weather de Niall Horan?");
        if (idArtista.equals("1")) elBuilder.setContentText("Revelación de Selena Gómez?");
        if (idArtista.equals("2")) elBuilder.setContentText("Wonder de Shawn Mendes?");
        if (idArtista.equals("3")) elBuilder.setContentText("Don't Forget de Demi Lovato?");
        if (idArtista.equals("4")) elBuilder.setContentText("Therefore I Am de Billie Eilish?");

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel elCanal = new NotificationChannel("Canal1", "CanalMain",
                    NotificationManager.IMPORTANCE_DEFAULT);
            elCanal.setDescription("Canal Principal");
            elCanal.setVibrationPattern(new long[] {0, 500});
            elCanal.enableVibration(true);
            elManager.createNotificationChannel(elCanal);
        }

        elManager.notify(1, elBuilder.build());

    }

    private void cargarFoto() {

        //Instancia de FireBase
        FirebaseStorage storage = FirebaseStorage.getInstance();
        // Crear una storage reference de nuestra app
        StorageReference storageRef = storage.getReference();
        // Crear una referencia a "fotoUser.jpg" siendo User el nombre de usuario
        String ref = "FotosArtistas/artista_" + idArtista + ".jpg";
        StorageReference fotoRef = storageRef.child(ref);

        final long ONE_MEGABYBTE = 1024 * 1024;
        fotoRef.getBytes(ONE_MEGABYBTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                //Se ha devuelto la foto
                Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                imagen.setImageBitmap(bitmap);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                // Ha ocurrido un error
                int tiempo= Toast.LENGTH_SHORT;
                String texto = "No se ha podido cargar la foto del artista";
                Toast aviso = Toast.makeText(getApplicationContext(), texto, tiempo);
                aviso.setGravity(Gravity.BOTTOM| Gravity.CENTER, 0, 0);
                aviso.show();
            }
        });
    }

    private void cargarInfoArtista() {

        Data datos = new Data.Builder()
                .putString("idArtista", idArtista)
                .build();

        OneTimeWorkRequest otwr = new OneTimeWorkRequest.Builder(ConexionInfoArtista.class)
                .setInputData(datos).build();

        WorkManager.getInstance(this).getWorkInfoByIdLiveData(otwr.getId())
                .observe(this, new Observer<WorkInfo>() {
                    @Override
                    public void onChanged(WorkInfo workInfo) {
                        if (workInfo != null && workInfo.getState().isFinished()) {
                            // -> Asignamos los valores a los EditText
                            txtNombre.setText(workInfo.getOutputData().getString("nombre"));
                            txtLugarNac.setText(workInfo.getOutputData().getString("lugar"));
                            txtFechaNac.setText(workInfo.getOutputData().getString("fecha"));
                        }
                    }
                });
        WorkManager.getInstance(this).enqueue(otwr);
    }


}