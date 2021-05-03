package com.example.parcial_2;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.text.format.Time;
import android.widget.Toast;

import androidx.annotation.Nullable;

public class DB extends SQLiteOpenHelper {
    static String nombre_bd = "db_cine";
    static String tblcine = "CREATE TABLE tblcine(idCine integer primary key autoincrement, titulo text, sipnosis text, duracion text, precio text, urlfoto text)";
    public DB(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, nombre_bd, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(tblcine);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }

    public Cursor administracion_de_cine(String accion, String[] datos){
        Cursor datocursor = null;
        SQLiteDatabase sqLiteDatabaseW = getWritableDatabase();
        SQLiteDatabase sqLiteDatabaseR = getReadableDatabase();

        switch (accion){
            case "consultar":
                datocursor = sqLiteDatabaseR.rawQuery("select * from tblcine",null);
                break;

            case "nuevo":
                sqLiteDatabaseW.execSQL("INSERT INTO tblcine(titulo, sipnosis, duracion, precio, urlfoto, urlvideo) VALUES ('"+datos[1]+"','"+datos[2]+"','"+datos[3]+"','"+datos[4]+"','"+datos[5]+"','"+datos[6]+"')");
                break;
            case "modificar":
                sqLiteDatabaseW.execSQL("update tblcine set titulo='"+datos[1]+"',sipnosis='"+datos[2]+"',duracion='"+datos[3]+"',precio='"+datos[4]+"',urlfoto='"+datos[5]+"',urlvideo='"+datos[6]+"' where idCine='"+datos[0]+"'");
                break;
            case "eliminar":
                sqLiteDatabaseW.execSQL("DELETE FROM tblcine WHERE idCine='"+ datos[0]+"'");
                break;
        }

        return datocursor; }

    public Cursor eliminar(String accion,String idd){
        Cursor datocursor = null;
        SQLiteDatabase sqLiteDatabaseW = getWritableDatabase();
        SQLiteDatabase sqLiteDatabaseR = getReadableDatabase();

        switch (accion){

            case "eliminar":
                sqLiteDatabaseW.execSQL("DELETE FROM tblcine WHERE idCine='"+ idd+"'");
                break;
        }

        return datocursor;
    }
}
