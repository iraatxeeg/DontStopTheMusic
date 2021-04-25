package com.example.dontstopthemusic.PantallaPrincipal;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.example.dontstopthemusic.R;
import com.example.dontstopthemusic.RecyclerView.ElAdaptadorRecycler;

public class PantallaPrincipalActivity extends AppCompatActivity {

    String usuario = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pantalla_principal);
        setSupportActionBar(findViewById(R.id.labarraMain));

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            usuario = extras.getString("usuario");
        }
        String text = "Hola " + usuario;
        Toast toast = Toast.makeText(this, text, Toast.LENGTH_LONG);
        toast.setGravity(Gravity.BOTTOM | Gravity.CENTER, 0, 0);
        toast.show();

        ////////////////////////////////////////////////////////////////////////////////////////////
        // RecyclerView Artistas
        RecyclerView laLista = findViewById(R.id.elrecyclerview);

        String[] nombres = {"Niall Horan", "Shawn Mendes", "Selena Gómez"};
        int[] ids = {1,2,3};
        int[] fotos = {R.drawable.niallhoran, R.drawable.shawnmendes, R.drawable.selenagomez};
        ElAdaptadorRecycler elAdaptador = new ElAdaptadorRecycler(nombres, fotos);
        elAdaptador.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent idArtista = new Intent(getBaseContext(), ActivityArtista.class);
//                idArtista.putExtra("idArtista",String.valueOf(ids[laLista.getChildAdapterPosition(v)]));
//                startActivity(idArtista);
            }
        });
        laLista.setAdapter(elAdaptador);
        LinearLayoutManager layout = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL,false);
        laLista.setLayoutManager(layout);


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menuprincipal, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
//        int id = item.getItemId();
//        if (id == R.id.Perfil) {
//            Intent iPerfil = new Intent(this, ActivityPerfil.class);
//            iPerfil.putExtra("Usuario", usuario);
//            startActivity(iPerfil);
//        }
        return super.onOptionsItemSelected(item);
    }
}