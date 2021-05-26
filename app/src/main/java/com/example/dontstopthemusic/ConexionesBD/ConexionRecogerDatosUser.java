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

public class ConexionRecogerDatosUser extends Worker {

    // Tarea para recoger los datos de un Usuario en la base de datos con el php recogerDatosUserE3.php

    public ConexionRecogerDatosUser(@NonNull Context pcontext, @NonNull WorkerParameters workerParams) {
        super(pcontext, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        String username = getInputData().getString("username");

        String direccion = "http://ec2-54-242-79-204.compute-1.amazonaws.com/igonzalez274/WEB/Entrega3/recogerDatosUserE3.php";
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
            parametrosJSON.put("username", username);
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

                String userDB = (String) json.get("username");
                String nombre = (String) json.get("nombre");
                String cumple = (String) json.get("cumple");

                String[] c = cumple.split("-");
                cumple = c[2] + " / " + c[1] + " / " + c[0];

                resultados = new Data.Builder()
                        .putString("username", userDB)
                        .putString("nombre", nombre)
                        .putString("cumple", cumple)
                        .build();

            }

        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }
        return ListenableWorker.Result.success(resultados);
    }
}

