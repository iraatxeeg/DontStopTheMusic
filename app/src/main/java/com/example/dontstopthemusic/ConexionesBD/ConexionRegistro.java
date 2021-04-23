package com.example.dontstopthemusic.ConexionesBD;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.work.Data;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import java.net.HttpURLConnection;
import java.net.URL;

public class ConexionRegistro extends Worker {

    public ConexionRegistro(@NonNull Context pcontext, @NonNull WorkerParameters workerParams) {
        super(pcontext, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        String txtUsuario = getInputData().getString("usuario");
        String txtContraseña = getInputData().getString("contraseña");

        String direccion = "http://ec2-54-167-31-169.compute-1.amazonaws.com/igonzalez274/WEB/registro.php";
        String result = "";
        Data resultados = null;
        HttpURLConnection urlConnection = null;
        try {
            URL destino = new URL(direccion);
            urlConnection = (HttpURLConnection) destino.openConnection();
            urlConnection.setConnectTimeout(5000);
            urlConnection.setReadTimeout(5000);
    }
}
