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
    public double lastMesure(String user, SensorType sensorType) {
        AdminSQLiteOpenHelper admin = new AdminSQLiteOpenHelper(this, "administracion", null, 1);
        SQLiteDatabase bd = admin.getWritableDatabase();
        Cursor fila = null;
        switch (sensorType) {
            case PERFORMANCE:
                fila = bd.rawQuery("select Performance from Mesure where idUser = '" + user + "' ORDER BY idMesure DESC LIMIT 1 ", null);
                break;
            case CO2:
                fila = bd.rawQuery("select CO2mesure from Mesure where idUser = '" + user + "' ORDER BY idMesure DESC LIMIT 1 ", null);
                break;
            case TEMPERATURE:
                fila = bd.rawQuery("select Temperature from Mesure where idUser = '" + user + "' ORDER BY idMesure DESC LIMIT 1", null);
                break;
            case LUMINOSITY:
                fila = bd.rawQuery("select Luminosite from Mesure where idUser = '" + user + "' ORDER BY idMesure DESC LIMIT 1 ", null);
                break;
            case HUMIDITY:
                fila = bd.rawQuery("select Humidite from Mesure where idUser = '" + user + "' ORDER BY idMesure DESC LIMIT 1 ", null);
                break;
            case COLOR_TEMPERATURE:
                fila = bd.rawQuery("select TempLum from Mesure where idUser = '" + user + "' ORDER BY idMesure DESC LIMIT 1 ", null);
        }
        if (fila == null)
            return -1;
        else if (!fila.moveToFirst())
            return -1;
        else
            return fila.getDouble(0);
    }

    public class WebAppInterface {

        @JavascriptInterface
        public double getPerformance() {
            return lastMesure(user, SensorType.PERFORMANCE); //A completar
        }

        @JavascriptInterface
        public double getMinTemperature() {
            double h = lastMesure(user, SensorType.HUMIDITY);
            return -0.05 * h + 21.5;
        }

        @JavascriptInterface
        public double getOptimalTemperature() {
            double h = lastMesure(user, SensorType.HUMIDITY);
            return -0.05 * h + 27.5;
        }

        @JavascriptInterface
        public double getMaxTemperature() {
            double h = lastMesure(user, SensorType.HUMIDITY);
            return -0.075 * h + 25.25;
        }

        @JavascriptInterface
        public double getCurrentTemperature() {
            return lastMesure(user, SensorType.TEMPERATURE);
        }

        @JavascriptInterface
        public double getMinHumidity() {
            double t = lastMesure(user, SensorType.HUMIDITY);
            double h = -20 * t + 430;
            return h < 0.3 ? 0.3 : h;
        }

        @JavascriptInterface
        public double getMaxHumidity() {
            double t = lastMesure(user, SensorType.HUMIDITY);
            double h = -10 * t + 330;
            return h < 0.7 ? 0.7 : h;
        }

        @JavascriptInterface
        public double getCurrentHumidity() {
            return lastMesure(user, SensorType.HUMIDITY);
        }

        @JavascriptInterface
        public double getCurrentCO2() {
            return lastMesure(user, SensorType.CO2);
        }

        @JavascriptInterface
        public double getCurrentLuminosity() {
            return lastMesure(user, SensorType.LUMINOSITY);
        }

        @JavascriptInterface
        public double getMinColorTemperature() {
            double l = lastMesure(user, SensorType.LUMINOSITY);
            return 1313.06 * Math.exp(2338.54 / l) - 248.36;
        }

        @JavascriptInterface
        public double getMaxColorTemperature() {
            double l = lastMesure(user, SensorType.LUMINOSITY);
            return 5.71 * l + 2448.42;
        }

        @JavascriptInterface
        public double getCurrentColorTemperature() {
            return lastMesure(user, SensorType.COLOR_TEMPERATURE);
        }
    }

}
