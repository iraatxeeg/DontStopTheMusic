package com.example.dontstopthemusic.RecyclerView;

import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.dontstopthemusic.R;

public class ElAdaptadorRecycler extends RecyclerView.Adapter<ElViewHolder>
 implements View.OnClickListener {

    private String[] losNombres;
    private int[] lasImagenes;
    private View.OnClickListener listener;

    public ElAdaptadorRecycler(String[] nombres, int[] imagenes) {
        losNombres = nombres;
        lasImagenes = imagenes;
    }

    @NonNull
    @Override
    public ElViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View elLayoutDeCadaItem = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_layout,null);
        ElViewHolder evh = new ElViewHolder(elLayoutDeCadaItem);
        elLayoutDeCadaItem.setOnClickListener(this);
        return evh;
    }

    @Override
    public void onBindViewHolder(@NonNull ElViewHolder holder, int position) {
        holder.eltexto.setText(losNombres[position]);
        holder.laimagen.setImageResource(lasImagenes[position]);
    }

    @Override
    public int getItemCount() {
        return losNombres.length;
    }

    public void setOnClickListener(View.OnClickListener pListener) {
        listener = pListener;
    }

    @Override
    public void onClick(View v) {
        if (listener != null) {
            listener.onClick(v);
        }
    }
}
