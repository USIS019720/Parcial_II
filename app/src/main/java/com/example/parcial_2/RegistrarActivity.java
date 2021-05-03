package com.example.parcial_2;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.app.Activity;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONObject;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

public class RegistrarActivity extends AppCompatActivity {

    FloatingActionButton btnregresar;
    ImageView imgfotodepeli;
    String urlphoto="", idCine,idlocal, accion = "nuevo", rev;
    Button btnagregar;
    DB miconexion;
    TextView temp;
    utilidades miUrl;
    detectarInternet di;
    private static final int RPQ= 100;
    private static final int RIG= 101;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registrar);

        miconexion = new DB(getApplicationContext(),"",null,1);
        btnregresar = findViewById(R.id.btnregresar);
        imgfotodepeli = findViewById(R.id.imgpeli);
        btnagregar = findViewById(R.id.btnguardarpelicula);

        btnregresar.setOnClickListener(v -> {
            regresarmainactivity();
        });

        imgfotodepeli.setOnClickListener(v -> {
            abrirgaleriaimagen();
        });

        btnagregar.setOnClickListener(v -> {
            agregar();
        });

        permisos();
        mostrardatos();
    }


    private void agregar() {
        try {
            temp = findViewById(R.id.txtTitulo);
            String titulo = temp.getText().toString();

            temp = findViewById(R.id.txtsinopsis);
            String sinopsis = temp.getText().toString();

            temp = findViewById(R.id.txtduracion);
            String duracion = temp.getText().toString();

            temp = findViewById(R.id.txtprecio);
            String precio = temp.getText().toString();

            JSONObject datospelis = new JSONObject();
            if(accion.equals("modificar") && idCine.length()>0 && rev.length()>0 ){
                datospelis.put("_id",idCine);
                datospelis.put("_rev",rev);
            }
            datospelis.put("titulo",titulo);
            datospelis.put("sinopsis",sinopsis);
            datospelis.put("duracion",duracion);
            datospelis.put("precio",precio);
            datospelis.put("urlfoto",urlphoto);

            String[] datos = {idlocal, titulo, sinopsis, duracion, precio, urlphoto };
            miconexion.administracion_de_cine(accion, datos);

            di = new detectarInternet(getApplicationContext());
            if (di.hayConexionInternet()) {
                enviarDatosCine guardarpelis = new enviarDatosCine(getApplicationContext());
                String resp = guardarpelis.execute(datospelis.toString()).get();
            }

            mensajes("Registro guardado con exito.");
            regresarmainactivity();
        }catch (Exception w){
            mensajes(w.getMessage());
        }
    }

    private void mostrardatos() {
        try {
            Bundle recibirparametros = getIntent().getExtras();
            accion = recibirparametros.getString("accion");
            idlocal = recibirparametros.getString("idlocal");
            if(accion.equals("modificar")){
                JSONObject datos = new JSONObject(recibirparametros.getString("datos")).getJSONObject("value");
                idCine = datos.getString("_id");

                rev = datos.getString("_rev");

                temp = findViewById(R.id.txtTitulo);
                temp.setText(datos.getString("titulo"));

                temp = findViewById(R.id.txtsinopsis);
                temp.setText(datos.getString("sinopsis"));

                temp = findViewById(R.id.txtduracion);
                temp.setText(datos.getString("duracion"));

                temp = findViewById(R.id.txtprecio);
                temp.setText(datos.getString("precio"));

                urlphoto =  datos.getString("urlfoto");

                imgfotodepeli.setImageURI(Uri.parse(urlphoto));
            }
        }catch (Exception ex){
            mensajes(ex.getMessage());

        }
    }

    private void permisos() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            if (ActivityCompat.checkSelfPermission(RegistrarActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)== PackageManager.PERMISSION_GRANTED){
            }else {
                ActivityCompat.requestPermissions(RegistrarActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},RPQ);
            }
        }else {
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent dataimagen) {
        if (resultCode == Activity.RESULT_OK && dataimagen != null) {
            if (requestCode == RIG) {
                Uri photo = dataimagen.getData();
                imgfotodepeli.setImageURI(photo);

                urlphoto = getRealUrl(this,photo);
            }
        }
        super.onActivityResult(requestCode, resultCode, dataimagen);
    }

    private void abrirgaleriaimagen(){
        Intent i = new Intent(Intent.ACTION_GET_CONTENT );
        i.setType("image/*");
        startActivityForResult(i, RIG);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode== RPQ){
            if(permissions.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
            }else{
                mensajes("Por favor dame los permisos");
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private void regresarmainactivity() {
        Intent i = new Intent(getApplicationContext(),MainActivity.class);
        startActivity(i);
    }

    private void mensajes(String msg){
        Toast.makeText(getApplicationContext(),msg, Toast.LENGTH_LONG).show();
    }

    public static String getRealUrl(final Context context, final Uri uri) {
        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];
                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }
            }
            else if (isDownloadsDocument(uri)) {
                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));
                return getDataColumn(context, contentUri, null, null);
            }
            else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }

                final String selection = "_id=?";
                final String[] selectionArgs = new String[] {
                        split[1]
                };

                return getDataColumn(context, contentUri, selection, selectionArgs);
            }
        }
        else if ("content".equalsIgnoreCase(uri.getScheme())) {
            return getDataColumn(context, uri, null, null);
        }
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }

        return null;
    }


    public static String getDataColumn(Context context, Uri uri, String selection,
                                       String[] selectionArgs) {

        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {
                column
        };

        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,
                    null);
            if (cursor != null && cursor.moveToFirst()) {
                final int column_index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(column_index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }


    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }


    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }


    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

}