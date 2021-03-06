package com.example.parcial_2;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.VideoView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONObject;

public class VistaActivity extends AppCompatActivity {
    FloatingActionButton btnregresar;
    ImageView imgfotodepeli;
    VideoView vdidepeli;
    String urldefoto, urldevideo,idpeli,idlocal, accion = "nuevo", rev;
    TextView temp;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vista);


        vdidepeli = findViewById(R.id.vdpelicula);
        imgfotodepeli = findViewById(R.id.imgPelic);
        btnregresar = findViewById(R.id.btnAtrass);
        btnregresar.setOnClickListener(v -> {
            regresarmainactivity();
        });

        try {
            mostrardatos();
            controles();
        }catch (Exception e){

        }
    }


    private void mostrardatos() {
        try {
            Bundle recibirparametros = getIntent().getExtras();
            accion = recibirparametros.getString("accion");
            idlocal = recibirparametros.getString("idlocal");
            if(accion.equals("ver")){
                JSONObject datos = new JSONObject(recibirparametros.getString("datos")).getJSONObject("value");
                idpeli = datos.getString("_id");

                rev = datos.getString("_rev");

                temp = findViewById(R.id.txtTitulo);
                temp.setText(datos.getString("titulo"));

                temp = findViewById(R.id.txtsinopsis);
                temp.setText(datos.getString("sinopsis"));

                temp = findViewById(R.id.txtduracion);
                temp.setText(datos.getString("duracion"));

                temp = findViewById(R.id.txtprecio);
                temp.setText(datos.getString("precio"));

                urldefoto =  datos.getString("urlfoto");
                urldevideo =  datos.getString("urltriler");

                imgfotodepeli.setImageURI(Uri.parse(urldefoto));
                vdidepeli.setVideoURI(Uri.parse(urldevideo));

            }
        }catch (Exception ex){

        }
    }




    private void regresarmainactivity() {
        Intent i = new Intent(getApplicationContext(),MainActivity.class);
        startActivity(i);
    }

    private void controles(){
        //Controles de video
        MediaController mediaController = new MediaController(this);
        vdidepeli.setMediaController(mediaController);
        mediaController.setAnchorView(vdidepeli);
    }

}