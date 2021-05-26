package com.example.dontstopthemusic.ConexionesBD;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.work.Data;
import androidx.work.ListenableWorker;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;

public class ConexionInfoArtista extends Worker {

    // Tarea para comprobar si el usuario existe en la base de datos con el php existeUsuario.php

    public ConexionInfoArtista(@NonNull Context pcontext, @NonNull WorkerParameters workerParams) {
        super(pcontext, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        String idArtista = getInputData().getString("idArtista");

        String direccion = "http://ec2-54-242-79-204.compute-1.amazonaws.com/igonzalez274/WEB/Entrega2/cargarArtista.php";
        String result = "";
        Data resultados = null;
        HttpURLConnection urlConnection = null;
        try {
            URL destino = new URL(direccion);
            urlConnection = (HttpURLConnection) destino.openConnection();
            urlConnection.setConnectTimeout(5000);
            urlConnection.setReadTimeout(5000);
            urlConnection.setRequestMethod("POST");
            urlConnection.setDoOutput(true);
            JSONObject parametrosJSON = new JSONObject();
            parametrosJSON.put("idArtista", idArtista);

            urlConnection.setRequestProperty("Content-Type","application/json");
            PrintWriter out = new PrintWriter(urlConnection.getOutputStream());
            out.print(parametrosJSON.toJSONString());
            out.close();

            int statusCode = urlConnection.getResponseCode();

            if (statusCode == 200) {
                BufferedInputStream inputStream = new BufferedInputStream(urlConnection.getInputStream());
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
                String line = "";
                while ((line = bufferedReader.readLine()) != null) {
                    result += line;
                }
                inputStream.close();

                JSONParser parser = new JSONParser();
                JSONObject json = (JSONObject) parser.parse(result);

                String nombre = (String) json.get("nombre");
                String lugar = (String) json.get("lugar");
                String fecha = (String) json.get("fecha");

                String[] f = fecha.split("-");
                fecha = f[2] + " / " + f[1] + " / " + f[0];

                resultados = new Data.Builder()
                        .putString("nombre", nombre)
                        .putString("lugar", lugar)
                        .putString("fecha", fecha)
                        .build();

            }

        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }
        return Result.success(resultados);
    }
}
