package com.example.dontstopthemusic.PantallaPrincipal;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

import com.example.dontstopthemusic.R;

public class ActivityArtista extends AppCompatActivity {

    String idArtista;
    // Actividad para mostrar la información de cada artista
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

        if (idArtista.equals("1")) {
            nombre = "Niall Horan";
            nacimiento = "13/09/1993";
            lugar = "Irlanda";
        }
        if (idArtista.equals("2")) {
            nombre = "Shawn Mendes";
            nacimiento = "17/07/1998";
            lugar = "Canada";
        }
        if (idArtista.equals("3")) {
            nombre = "Selena Gomez";
            nacimiento = "17/07/1992";
            lugar = "Texas";
        }


        TextView txtNombre = findViewById(R.id.txtNombreArtista1);
        txtNombre.setText(nombre);
        TextView txtFechaNac = findViewById(R.id.txtNacimientoArtista1);
        txtFechaNac.setText(nacimiento);
        TextView txtLugarNac = findViewById(R.id.txtNacimientoArtistaLugar);
        txtLugarNac.setText(lugar);

        ImageView imagen = findViewById(R.id.imageViewArtista);
        if (idArtista.equals("1")) imagen.setImageResource(R.drawable.niallhoran);
        if (idArtista.equals("2")) imagen.setImageResource(R.drawable.shawnmendes);
        if (idArtista.equals("3")) imagen.setImageResource(R.drawable.selenagomez);



        ////////////////////////////////////////////////////////////////////////////////////////////
        // NOTIFICACIONES LOCALES
        NotificationManager elManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationCompat.Builder elBuilder = new NotificationCompat.Builder(this, "Canal1");
        elBuilder.setSmallIcon(R.drawable.logo).setContentTitle("¿Por qué no revisas...")
                .setVibrate(new long[] {0, 500})
                .setAutoCancel(true);
        if (idArtista.equals("1")) elBuilder.setContentText("Heartbreak Weather de Niall Horan?");
        if (idArtista.equals("2")) elBuilder.setContentText("Wonder de Shawn Mendes?");
        if (idArtista.equals("3")) elBuilder.setContentText("Revelación de Selena Gómez?");
        if (idArtista.equals("4")) elBuilder.setContentText("Don't Forget de Demi Lovato?");
        if (idArtista.equals("5")) elBuilder.setContentText("Therefore I Am de Billie Eilish?");

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


}