package davidfdez.capteuratmospherique;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.location.LocationManager;
import android.view.View;


public class AdminSQLiteOpenHelper extends SQLiteOpenHelper {
    LocationManager locationManager;
    public AdminSQLiteOpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
        locationManager =  (LocationManager)context.getSystemService(Context.LOCATION_SERVICE);
    }

    public AdminSQLiteOpenHelper(Context context) {

        super(context, "administracion", null, 1);
        locationManager =  (LocationManager)context.getSystemService(Context.LOCATION_SERVICE);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table User (id Varchar(15) Primary Key, password Varchar(15))");
        db.execSQL("create table Mesure ( idMesure INTEGER Primary Key,idUser Varchar(15),Performance double, CO2Mesure double,Luminosite double, Latitude varchar(15), Longitude varchar(15), Temperature double, Humidite double, TempLum double)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }

    public boolean setMeasures(long timestamp, String user, double CO2, double humidity, double temperature, double luminosity, double color, String latitude, String longitude) {
        SQLiteDatabase bd = this.getWritableDatabase();
        ContentValues valuesToAdd = new ContentValues();
        valuesToAdd.put("idMesure", timestamp);
        valuesToAdd.put("idUser", user);
        valuesToAdd.put("Performance", MeasureUtil.calculatePerformance(CO2, temperature, humidity, luminosity, color));
        valuesToAdd.put("CO2Mesure", CO2);
        valuesToAdd.put("Luminosite", luminosity);
        valuesToAdd.put("Latitude", latitude);
        valuesToAdd.put("Longitude", longitude);
        valuesToAdd.put("Temperature", temperature);
        valuesToAdd.put("Humidite", humidity);
        valuesToAdd.put("TempLum", color);
        if (!checkTimestamp(timestamp)) {
            bd.insert("Mesure", null, valuesToAdd);
            bd.close();
            return true;
        } else {
            return false;
        }
    }

    public boolean checkTimestamp(long timestamp) {
        SQLiteDatabase bd = this.getWritableDatabase();
        boolean exists = false;
        Cursor fila = bd.rawQuery(
                "select idMesure from Mesure where idMesure = '" + timestamp + "'", null);
        if (fila.moveToFirst()) {
            if (fila.getLong(0) == timestamp) {
                exists = true;
            }
        } else {
            exists = false;
        }
        return exists;
    }

    public boolean signIn(View v, String User, String Password) {
        SQLiteDatabase bd = this.getWritableDatabase();
        ContentValues valuesToAdd = new ContentValues();
        valuesToAdd.put("id", User);
        valuesToAdd.put("password", Password);

        if (!checkIfUserExists(User)) {
            bd.insert("User", null, valuesToAdd);
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
        Cursor raw = bd.rawQuery(
                "select id,password from User where id = '" + User + "'", null);
        if (raw.moveToFirst()) {
            if (raw.getString(0).equals(User) && raw.getString(1).equals(Password)) {
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
}
