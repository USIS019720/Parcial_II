package com.example.parcial_2;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;


import com.example.parcial_2.cine;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class MostrarDatos extends BaseAdapter {
    Context context;
    ArrayList<cine> datospelisArrayList;
    LayoutInflater layoutInflater;
    cine misPelic;

    public MostrarDatos(Context context, ArrayList<cine> datospelisArrayList) {
        this.context = context;
        this.datospelisArrayList = datospelisArrayList;
    }

    @Override
    public int getCount() {
        return datospelisArrayList.size();
    }
    @Override
    public Object getItem(int position) {
        return datospelisArrayList.get(position);
    }
    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        layoutInflater = (LayoutInflater)context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
        View encuadre = layoutInflater.inflate(R.layout.list_mostrar, parent, false);
        TextView temp = encuadre.findViewById(R.id.lbltitulo);
        ImageView img = encuadre.findViewById(R.id.img);
        try{
            misPelic = datospelisArrayList.get(position);
            temp.setText(misPelic.getTitulo());

            temp = encuadre.findViewById(R.id.lblprecio);
            temp.setText("$"+misPelic.getPrecio());

            String urldefoto = misPelic.getUrlfoto();


            img.setImageURI(Uri.parse(urldefoto));

        }catch (Exception e){
        }
        return encuadre;
    }
}
