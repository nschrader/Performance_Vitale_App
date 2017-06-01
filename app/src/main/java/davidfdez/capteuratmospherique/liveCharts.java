package davidfdez.capteuratmospherique;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;


public class liveCharts extends AppCompatActivity {
    private WebView webView;
    private String user = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_live_charts);

        webView = (WebView) findViewById(R.id.web);
        //setContentView(webView);
        webView.addJavascriptInterface(new WebAppInterface(), "Android");

        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setLoadWithOverviewMode(true);
        webView.getSettings().setUseWideViewPort(true);
        webView.loadUrl("file:///android_asset/liveChart.html");

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

    //Methode pour selectionner les mesures d'un capteur qui sont dehors l'interval optimale (Co2 dans ce cas)
    public double lastMesure(String user, int TypeCapteur) {  //TypeCapteur-->0 (Performance) TypeCapteur-->1 (CO2) TypeCapteur-->2 (Temp) TypeCapteur-->3 (Luminositée) TypeCapteur-->4 (Humiditée)
        AdminSQLiteOpenHelper admin = new AdminSQLiteOpenHelper(this,
                "administracion", null, 1);
        SQLiteDatabase bd = admin.getWritableDatabase();
        Cursor fila = null;
        if (TypeCapteur == 1) {
            fila = bd.rawQuery("select CO2mesure from Mesure where idUser = '" + user + "' Limit 1 ", null);
        } else if (TypeCapteur == 2) {
            fila = bd.rawQuery("select Temperature from Mesure where idUser = '" + user + "'Limit 1 ", null);
        } else if (TypeCapteur == 3) {
            fila = bd.rawQuery("select Luminosite from Mesure where idUser = '" + user + "'Limit 1 ", null);
        } else if (TypeCapteur == 4) {
            fila = bd.rawQuery("select Humidite from Mesure where idUser = '" + user + "' Limit 1 ", null);
        } else if (TypeCapteur == 0) {
            fila = bd.rawQuery("select Performance from Mesure where idUser = '" + user + "' Limit 1 ", null);
        }

        if (fila.moveToFirst()) {
            return Integer.parseInt(fila.getString(0));
        }
        return 10;
    }

    public class WebAppInterface {

        @JavascriptInterface
        public double getPerformance() {
            return lastMesure(user, 0); //A completar
        }

        @JavascriptInterface
        public double getTemperature() {
            return lastMesure(user, 2);
        }

        @JavascriptInterface
        public double getHumidite() {
            return lastMesure(user, 4);
        }

        @JavascriptInterface
        public double getCO2() {
            return lastMesure(user, 1);
        }

        @JavascriptInterface
        public double getLuminosite() {
            return lastMesure(user, 3);
        }
    }

}
