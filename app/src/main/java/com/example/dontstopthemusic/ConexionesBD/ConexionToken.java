package com.example.dontstopthemusic.ConexionesBD;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.work.Data;
import androidx.work.ListenableWorker;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import org.json.simple.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;

public class ConexionToken extends Worker {

    public ConexionToken(@NonNull Context pcontext, @NonNull WorkerParameters workerParams) {
        super(pcontext, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        String usuario = getInputData().getString("username");
        String token = getInputData().getString("token");

        String direccion = "http://ec2-54-242-79-204.compute-1.amazonaws.com/igonzalez274/WEB/Entrega2/subirToken.php";
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
            parametrosJSON.put("username", usuario);
            parametrosJSON.put("token", token);

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

                resultados = new Data.Builder()
                        .putString("resultado", result)
                        .build();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return Result.success(resultados);
    }
}