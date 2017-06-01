package davidfdez.capteuratmospherique;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.view.View;


public class AdminSQLiteOpenHelper extends SQLiteOpenHelper {

    public AdminSQLiteOpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table User (id Varchar(15) Primary Key, password Varchar(15))");
        db.execSQL("create table Mesure ( idMesure INTEGER Primary Key,idUser Varchar(15),Performance double, CO2Mesure double,Luminosite double, Latitude varchar(15), Longitude varchar(15), Temperature double, Humidite double, TempLum double)");
    }

    public boolean introduireDesMesures(long idMesure, String idUser, double CO2Mesure, double Humidite, double Temperature, double Luminosite, double TempLum, String Latitude, String Longitude) {
        SQLiteDatabase bd = this.getWritableDatabase();
        ContentValues registro = new ContentValues();
        registro.put("idMesure", idMesure);
        registro.put("idUser", idUser);
        registro.put("Performance", 50);
        registro.put("CO2Mesure", CO2Mesure);
        registro.put("Luminosite", Luminosite);
        registro.put("Latitude", Latitude);
        registro.put("Longitude", Longitude);
        registro.put("Temperature", Temperature);
        registro.put("Humidite", Humidite);
        registro.put("TempLum", TempLum);
        if (!checkMessure(idMesure)) {
            bd.insert("Mesure", null, registro);
            bd.close();
            return true;
        } else {
            return false;
        }
    }

    public boolean checkMessure(long idMesure) {
        SQLiteDatabase bd = this.getWritableDatabase();
        boolean exists = false;
        Cursor fila = bd.rawQuery(
                "select idMesure from Mesure where idMesure = '" + idMesure + "'", null);
        if (fila.moveToFirst()) {
            if (fila.getLong(0) == idMesure) {
                exists = true;
            }
        } else {
            exists = false;
        }
        return exists;
    }

    public boolean signIn(View v, String User, String Password) {
        SQLiteDatabase bd = this.getWritableDatabase();
        ContentValues registro = new ContentValues();
        registro.put("id", User);
        registro.put("password", Password);

        if (!checkIfUserExists(User)) {
            bd.insert("User", null, registro);
            bd.close();
            return true;
        } else {
            return false;
        }
    }

    public boolean checkIfUserExists(String User) {
        SQLiteDatabase bd = this.getWritableDatabase();
        boolean exists = false;
        Cursor fila = bd.rawQuery(
                "select id from User where id = '" + User + "'", null);
        if (fila.moveToFirst()) {
            if (fila.getString(0).equals(User)) {
                exists = true;
            }
        } else {
            exists = false;
        }
        return exists;
    }

    public boolean logIn(View v, String User, String Password) {

        SQLiteDatabase bd = this.getWritableDatabase();
        Cursor fila = bd.rawQuery(
                "select id,password from User where id = '" + User + "'", null);
        if (fila.moveToFirst()) {
            if (fila.getString(0).equals(User) && fila.getString(1).equals(Password)) {
                bd.close();
                return true;

            } else
                bd.close();
            return false;
        } else {
            bd.close();
            return false;
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
