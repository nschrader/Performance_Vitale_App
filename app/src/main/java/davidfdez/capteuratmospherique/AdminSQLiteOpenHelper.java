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
        registro.put("Performance", getPerformance(CO2Mesure,Temperature,Humidite,Luminosite,TempLum));
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
    public double getPerformance(double CO2, double Temp, double humidite, double Luminosite, double Couleur){
        return 100 - this.ecHumidite(Temp, humidite) - this.ecCo2(CO2) - this.ecTemp(Temp, humidite) - this.ecLuminosite(Luminosite) - this.ecCouleur(Couleur,Luminosite);
    }
    public double ecCouleur(double Couleur,double Luminosite){
        double Coulmin = 1313.06* Math.exp(-248.36/Luminosite) + 2338.54;
        double Coulmax = 5.71*Luminosite + 2448.42;


        if(Couleur>Coulmax){
            return (Couleur - Coulmax) * (Couleur - Coulmax)*2/10000;
        }
        else if(Couleur<Coulmin){
            return (Couleur - Coulmin) * (Couleur - Coulmin)*2/10000;
        }
        else return 0;
    }

    public double ecTemp(double Temp, double Humid){
        double Tmax = -0.075*Humid + 25.25;
        double Tmin = -0.05*Humid +27.5;
        if(Temp<Tmin){
            return (Temp-Tmin)*(Temp - Tmin)/2;
        }
        else if(Temp>Tmax){
            return (Temp-Tmax)*(Temp - Tmax)/2;
        }
        else{
            return 0;
        }
    }
    public double ecCo2(double CO2){
        if(CO2!= -1) {
            return (CO2 - 400) * (CO2 - 400) / 300000;
        }
        return 0;
    }
    public double ecHumidite(double Temp, double humidite){
        double Hmin = - 20*Temp +430;
        double Hmax = -10*Temp+330;

        if(Hmin<30){
            //System.out.println("UNO" + Hmin);
            Hmin = 30;
            //System.out.println("UNO" + Hmin);
        }

        if(Hmax >= 70){
            //System.out.println("Dos");
            Hmax = 70;
        }
        if(humidite<Hmin){
            //System.out.println("1."+ Hmin);
            return (humidite-Hmax)*(humidite-Hmax)/100;
        }
        else if(humidite>Hmax){
            //System.out.println("2."+ Hmax);
            return (humidite-Hmax)*(humidite-Hmax)/100 ;
        }else{
            return 0;
        }
    }
    public double ecLuminosite(double Luminosite){
        return (Luminosite - 500)*(Luminosite - 500)*6/10000;
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
