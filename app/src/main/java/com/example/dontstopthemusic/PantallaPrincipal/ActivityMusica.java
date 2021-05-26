package com.example.dontstopthemusic.PantallaPrincipal;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.example.dontstopthemusic.ConexionesBD.ConexionCanciones;
import com.example.dontstopthemusic.R;
import com.example.dontstopthemusic.Reproductores.ReproducirBaila;
import com.example.dontstopthemusic.Reproductores.ReproducirBury;
import com.example.dontstopthemusic.Reproductores.ReproducirDontForget;
import com.example.dontstopthemusic.Reproductores.ReproducirFlicker;
import com.example.dontstopthemusic.Reproductores.ReproducirHeartbreak;
import com.example.dontstopthemusic.Reproductores.ReproducirLookUp;
import com.example.dontstopthemusic.Reproductores.ReproducirRare;
import com.example.dontstopthemusic.Reproductores.ReproducirStitches;
import com.example.dontstopthemusic.Reproductores.ReproducirTellMeYou;
import com.example.dontstopthemusic.Reproductores.ReproducirTherefore;

public class ActivityMusica extends AppCompatActivity {

    // Actividad para mostrar las canciones de un artista
    String idArtista = "";
    String username = "";
    String[] canciones = new String[2];
    ImageButton pause;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_musica);
        setSupportActionBar(findViewById(R.id.labarrainfoartitsta));

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            idArtista = extras.getString("idArtista");
            username = extras.getString("username");
        }

        cargarCanciones();

        pause = findViewById(R.id.imagePause);
        pause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Parar la canción anterior
                Intent i = new Intent(getBaseContext(), ReproducirFlicker.class);
                stopService(i);
                i = new Intent(getBaseContext(), ReproducirHeartbreak.class);
                stopService(i);
                i = new Intent(getBaseContext(), ReproducirRare.class);
                stopService(i);
                i = new Intent(getBaseContext(), ReproducirBaila.class);
                stopService(i);
                i = new Intent(getBaseContext(), ReproducirStitches.class);
                stopService(i);
                i = new Intent(getBaseContext(), ReproducirLookUp.class);
                stopService(i);
                i = new Intent(getBaseContext(), ReproducirBury.class);
                stopService(i);
                i = new Intent(getBaseContext(), ReproducirTherefore.class);
                stopService(i);
                i = new Intent(getBaseContext(), ReproducirDontForget.class);
                stopService(i);
                i = new Intent(getBaseContext(), ReproducirTellMeYou.class);
                stopService(i);

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        return true;
    }

    private void cargarCanciones() {

        Data datos = new Data.Builder()
                .putString("idArtista", idArtista)
                .build();

        OneTimeWorkRequest otwr = new OneTimeWorkRequest.Builder(ConexionCanciones.class)
                .setInputData(datos)
                .build();

        WorkManager.getInstance(this).getWorkInfoByIdLiveData(otwr.getId())
                .observe(this, new Observer<WorkInfo>() {
                    @Override
                    public void onChanged(WorkInfo workInfo) {
                        if (workInfo != null && workInfo.getState().isFinished()) {
                            canciones = workInfo.getOutputData().getStringArray("resultado");
                            crearLista();
                        }
                    }
                });
        WorkManager.getInstance(this).enqueue(otwr);
    }

    private void crearLista() {
        ArrayAdapter eladaptador =
                new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,canciones);
        ListView lalista = (ListView) findViewById(R.id.lalista);
        lalista.setAdapter(eladaptador);



        lalista.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Parar la canción anterior
                Intent i = new Intent(getBaseContext(), ReproducirFlicker.class);
                stopService(i);
                i = new Intent(getBaseContext(), ReproducirHeartbreak.class);
                stopService(i);
                i = new Intent(getBaseContext(), ReproducirRare.class);
                stopService(i);
                i = new Intent(getBaseContext(), ReproducirBaila.class);
                stopService(i);
                i = new Intent(getBaseContext(), ReproducirStitches.class);
                stopService(i);
                i = new Intent(getBaseContext(), ReproducirLookUp.class);
                stopService(i);
                i = new Intent(getBaseContext(), ReproducirBury.class);
                stopService(i);
                i = new Intent(getBaseContext(), ReproducirTherefore.class);
                stopService(i);
                i = new Intent(getBaseContext(), ReproducirDontForget.class);
                stopService(i);
                i = new Intent(getBaseContext(), ReproducirTellMeYou.class);
                stopService(i);

                // Reproducir la seleccionada
                if (((TextView)view).getText().toString().equals("Flicker")) { i = new Intent(getBaseContext(), ReproducirFlicker.class); }
                if (((TextView)view).getText().toString().equals("Heartbreak Weather")) { i = new Intent(getBaseContext(), ReproducirHeartbreak.class); }
                if (((TextView)view).getText().toString().equals("Rare")) { i = new Intent(getBaseContext(), ReproducirRare.class); }
                if (((TextView)view).getText().toString().equals("Revolución")) { i = new Intent(getBaseContext(), ReproducirBaila.class); }
                if (((TextView)view).getText().toString().equals("Shawn Mendes")) { i = new Intent(getBaseContext(), ReproducirStitches.class); }
                if (((TextView)view).getText().toString().equals("Wonder")) { i = new Intent(getBaseContext(), ReproducirLookUp.class); }
                if (((TextView)view).getText().toString().contains("When We All")) { i = new Intent(getBaseContext(), ReproducirBury.class); }
                if (((TextView)view).getText().toString().equals("Don't Forget")) { i = new Intent(getBaseContext(), ReproducirDontForget.class); }
                if (((TextView)view).getText().toString().contains("Tell Me")) { i = new Intent(getBaseContext(), ReproducirTellMeYou.class); }

                startService(i);

            }
        });
    }
}