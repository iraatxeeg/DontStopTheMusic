package com.example.dontstopthemusic.PantallaPrincipal;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.example.dontstopthemusic.Main.MainActivity;
import com.example.dontstopthemusic.R;
import com.example.dontstopthemusic.RecyclerView.ElAdaptadorRecycler;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;

public class PantallaPrincipalActivity extends AppCompatActivity {

    String username = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pantalla_principal);
        setSupportActionBar(findViewById(R.id.labarraPrincipal));

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            username = extras.getString("username");
            String text = "Hola " + username + "!";
            Toast toast = Toast.makeText(this, text, Toast.LENGTH_LONG);
            toast.setGravity(Gravity.BOTTOM | Gravity.CENTER, 0, 0);
            toast.show();
        }


        ////////////////////////////////////////////////////////////////////////////////////////////

        // Cargar los datos de los artistas en el recyclerView
        RecyclerView laLista;
        ElAdaptadorRecycler elAdaptador;
        String[] nombres = {"Niall Horan", "Selena Gomez", "Shawn Mendes",
                "Billie Eilish", "Demi Lovato"};
        int[] ids = {0,1,2,3,4};
        int[] fotos = {R.drawable.niallhoran, R.drawable.selenagomez, R.drawable.shawnmendes,
                R.drawable.billieeilish, R.drawable.demilovato};
        laLista = findViewById(R.id.elrecyclerview);
        elAdaptador = new ElAdaptadorRecycler(nombres, fotos);
        elAdaptador.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent idArtista = new Intent(getBaseContext(), ActivityArtista.class);
                idArtista.putExtra("idArtista",String.valueOf(ids[laLista.getChildAdapterPosition(v)]));
                idArtista.putExtra("username", username);
                startActivity(idArtista);
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
        int id = item.getItemId();

        if (id == R.id.opcionPerfil) {
            Intent iPerfil = new Intent(this, ActivityPerfil.class);
            iPerfil.putExtra("username", username);
            startActivity(iPerfil);
        }
        return super.onOptionsItemSelected(item);
    }

    // Cuando el usuario pulse el boton "Atras" de su dispositivo movil,
    // le preguntaremos si desea cerrar sesion.
    @Override
    public void onBackPressed(){
        AlertDialog.Builder alertDialogBuilder;
        alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setTitle("Don't Stop The Music");
        alertDialogBuilder.setMessage("¿Deseas cerrar la sesión?")
                .setCancelable(false)
                .setPositiveButton("Aceptar", (dialog, which) -> {
                    // Cerrar sesión
                    Intent ma = new Intent(this, MainActivity.class);
                    ma.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(ma);
                    finish();
                })
                .setNegativeButton("Cancelar", (dialog, which) -> dialog.cancel()).create().show();
    }
}