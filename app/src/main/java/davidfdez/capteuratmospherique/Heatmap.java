package davidfdez.capteuratmospherique;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.webkit.WebView;
import android.webkit.WebChromeClient;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.content.Context;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.view.MenuItem;
import android.webkit.JavascriptInterface;


import java.util.ArrayList;

import java.util.List;


public class Heatmap extends AppCompatActivity {
    WebView webView;
    private String user = "";
    List<Double> latitudesComfort = new ArrayList<>();
    List<Double> longitudesComfort = new ArrayList<>();
    List<Double> latitudesDiscomfort = new ArrayList<>();
    List<Double> longitudesDiscomfort = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_heatmap);

        getOver50();
        //getUnder50();


        webView = (WebView) findViewById(R.id.heatmap);
        //setContentView(webView);
        webView.addJavascriptInterface(new WebAppInterface(), "Android");

        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setLoadWithOverviewMode(true);
        webView.getSettings().setUseWideViewPort(true);
        webView.loadUrl("file:///android_asset/heatmap.html");

    }
    public void getOver50(){
        AdminSQLiteOpenHelper admin = new AdminSQLiteOpenHelper(this,
                "administracion", null, 1);
        SQLiteDatabase bd = admin.getWritableDatabase();
        Cursor fila = bd.rawQuery(
                "select Latitude, Longitude from Mesure where idUser = '" + user + "' AND Performance > 50", null);
/*
        if (fila.moveToFirst()) {
            String []  splittedLat = fila.getString(0).split(",");
            String []  splittedLong = fila.getString(1).split(",");
            latitudesComfort.add(Double.parseDouble(splittedLat[0]));
            longitudesComfort.add(Double.parseDouble(splittedLong[0]));
            while(fila.moveToNext()){
                splittedLat = fila.getString(0).split(",");
                splittedLong = fila.getString(1).split(",");
                latitudesComfort.add(Double.parseDouble(splittedLat[0]));
                longitudesComfort.add(Double.parseDouble(splittedLong[0]));
            }
        }*/
    }
    public void getUnder50(){
        AdminSQLiteOpenHelper admin = new AdminSQLiteOpenHelper(this,
                "administracion", null, 1);
        SQLiteDatabase bd = admin.getWritableDatabase();
        Cursor fila = bd.rawQuery(
                "select Latitude, Longitude from Mesure where id = '" + user + "' and Performance < 50", null);

        if (fila.moveToFirst()) {
            String []  splittedLat = fila.getString(0).split(",");
            String []  splittedLong = fila.getString(1).split(",");
            latitudesDiscomfort.add(Double.parseDouble(splittedLat[0]));
            latitudesDiscomfort.add(Double.parseDouble(splittedLong[0]));
            while(fila.moveToNext()){
                splittedLat = fila.getString(0).split(",");
                splittedLong = fila.getString(1).split(",");
                latitudesDiscomfort.add(Double.parseDouble(splittedLat[0]));
                latitudesDiscomfort.add(Double.parseDouble(splittedLong[0]));
            }
        }
    }
    public class WebAppInterface {
        @JavascriptInterface
        public double getLatitude(int i) {
            return latitudesComfort.get(i);


        }
        @JavascriptInterface
        public double getLongitude(int i) {
            return longitudesComfort.get(i);


        }
        @JavascriptInterface
        public int getSize(){
            return longitudesDiscomfort.size();
        }
        @JavascriptInterface
        public double getLatitudeDis(int i) {
            return latitudesDiscomfort.get(i);


        }
        @JavascriptInterface
        public double getLongitudeDis(int i) {
            return longitudesDiscomfort.get(i);


        }
        @JavascriptInterface
        public int getSizeDis(){
            return longitudesDiscomfort.size();
        }
    }
}