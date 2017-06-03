
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
        ContentValues valuesToAdd = new ContentValues();
        valuesToAdd.put("idMesure", idMesure);
        valuesToAdd.put("idUser", idUser);
        valuesToAdd.put("Performance", getPerformance(CO2Mesure,Temperature,Humidite,Luminosite,TempLum));
        valuesToAdd.put("CO2Mesure", CO2Mesure);
        valuesToAdd.put("Luminosite", Luminosite);
        valuesToAdd.put("Latitude", Latitude);
        valuesToAdd.put("Longitude", Longitude);
        valuesToAdd.put("Temperature", Temperature);
        valuesToAdd.put("Humidite", Humidite);
        valuesToAdd.put("TempLum", TempLum);
        if (!checkMessure(idMesure)) {
            bd.insert("Mesure", null, valuesToAdd);
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
    public double getPerformance(double CO2, double temp, double humidity, double lightIntensity, double Color){

        double perf = 100 - this.ecHumidite(temp, humidity) - this.ecCo2(CO2) - this.ecTemp(temp, humidity) - this.ecColor(Color,lightIntensity);
        if(perf>100){
            return 100;
        }else if(perf<0){
            return 0;
        }else{
            return perf;
        }
    }
    public double ecColor(double color, double lightIntensity){
        double colorMin = 1313.06* Math.exp(-248.36/lightIntensity) + 2338.54;
        double colorMax = 5.71*lightIntensity + 2448.42;


        if(color>colorMax){
            return (color - colorMax) * (color - colorMax)*2/50000;
        }
        else if(color<colorMin){
            return (color - colorMin) * (color - colorMin)*2/50000;
        }
        else return 0;
    }

    public double ecTemp(double temp, double humid){
        double tMin = -0.075*humid + 25.25;
        double tMax = -0.05*humid +27.5;
        if(temp<tMin){
            return (temp-tMin)*(temp - tMin)/2;
        }
        else if(temp>tMax){
            return (temp-tMax)*(temp - tMax)/2;
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
    public double ecHumidite(double temp, double humidity){
        double hMin = - 20*temp +430;
        double hMax = -10*temp+330;

        if(hMin<30){
            //System.out.println("UNO" + hMin);
            hMin = 30;
            //System.out.println("UNO" + hMin);
        }

        if(hMax >= 70){
            //System.out.println("Dos");
            hMax = 70;
        }
        if(humidity<hMin){
            //System.out.println("1."+ hMin);
            return (humidity-hMax)*(humidity-hMax)/100;
        }
        else if(humidity>hMax){
            //System.out.println("2."+ hMax);
            return (humidity-hMax)*(humidity-hMax)/100 ;
        }else{
            return 0;
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

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
