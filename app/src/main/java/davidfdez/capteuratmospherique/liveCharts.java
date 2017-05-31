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
import android.webkit.WebView;
import davidfdez.capteuratmospherique.R;
import android.database.Cursor;


public class liveCharts extends AppCompatActivity {
    WebView webView;
    private String user = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_live_charts);



        webView = (WebView)findViewById(R.id.web);
        //setContentView(webView);
        webView.addJavascriptInterface(new WebAppInterface(), "Android");

        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setLoadWithOverviewMode(true);
        webView.getSettings().setUseWideViewPort(true);
        webView.loadUrl("file:///android_asset/liveChart.html");

    }
    public class WebAppInterface {
        @JavascriptInterface
        public int getPerformance() {

            return (int) (Math.random() * 10) + 80; //A completar

        }

        @JavascriptInterface
        public int getTemperature() {

            return (int) (Math.random() * 10) + 80;

        }

        @JavascriptInterface
        public int getHumidite() {

            return (int) (Math.random() * 10) + 80;

        }

        @JavascriptInterface
        public int getCO2() {

            return (int) (Math.random() * 10) + 80;

        }

        @JavascriptInterface
        public int getLuminosite() {

            return (int) (Math.random() * 10) + 80;

        }
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == android.R.id.home) {
            // finish the activity
            onBackPressed();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
/*
    //Methode pour selectionner les mesures d'un capteur qui sont dehors l'interval optimale (Co2 dans ce cas)
    public int lastMessure(String user, int TypeCapteur) {  //TypeCapteur-->1 (CO2) TypeCapteur-->2 (Temp) TypeCapteur-->3 (Luminositée) TypeCapteur-->4 (Humiditée)
        AdminSQLiteOpenHelper admin = new AdminSQLiteOpenHelper(this,
                "administracion", null, 1);
        SQLiteDatabase bd = admin.getWritableDatabase();
        Cursor fila = null;
        if(TypeCapteur == 1) {
            fila = bd.rawQuery("select CO2messure from Mes where idUser = '" + user + "' orderBy DATETIME DESC Limit 1 ", null);
        }
        else if(TypeCapteur == 2) {
            fila = bd.rawQuery("select Temperature from Mes where idUser = '" + user + "' orderBy DATETIME DESC Limit 1 ", null);
        }
        else if(TypeCapteur == 3) {
            fila = bd.rawQuery("select Luminosite from Mes where idUser = '" + user + "' ORDER BY DATETIME DESC Limit 1 ", null);
        }
        else if(TypeCapteur == 4) {
            fila = bd.rawQuery("select Humidite from Mes where idUser = '" + user + "' orderBy DATETIME DESC Limit 1 ", null);
        }

        if (fila.moveToFirst()) {
            return Integer.parseInt(fila.getString(0));
        }
        return -100;
    }*/

}
