package com.example.dontstopthemusic.ConexionesBD;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.work.Data;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.example.dontstopthemusic.Login_Registro.LoginActivity;
import com.example.dontstopthemusic.R;

import org.json.simple.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;

public class ConexionLogin extends Worker {

    Context context;
    public ConexionLogin(@NonNull Context pcontext, @NonNull WorkerParameters workerParams) {
        super(pcontext, workerParams);
        context = pcontext;
    }

    @NonNull
    @Override
    public Result doWork() {
        String txtUsuario = getInputData().getString("usuario");
        String txtContraseña = getInputData().getString("contraseña");

        String direccion = "http://ec2-54-167-31-169.compute-1.amazonaws.com/igonzalez274/WEB/login.php";
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
           parametrosJSON.put("usuario", txtUsuario);
           parametrosJSON.put("contraseña", txtContraseña);

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
                    Log.i("hola", line + "linea");
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
