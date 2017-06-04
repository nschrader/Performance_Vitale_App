package davidfdez.capteuratmospherique;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.MenuItem;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.widget.Toast;

public class LiveChartsActivity extends AbstractChartsActivity {
    private WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_live_charts);

        webView = (WebView) findViewById(R.id.web);
        webView.addJavascriptInterface(new WebAppInterface(), "Android");
        configureWebView(webView);
        webView.loadUrl("file:///android_asset/liveChart.html");

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        /* Handle action bar item clicks here. The action bar will
         * automatically handle clicks on the Home/Up button, so long
         * as you specify a parent activity in AndroidManifest.xml.
         */
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == android.R.id.home) {
            // finish the activity
            onBackPressed();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public double lastMesure(String user, SensorType sensorType) {
        AdminSQLiteOpenHelper admin = new AdminSQLiteOpenHelper(this);
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
            return lastMesure(user, SensorType.PERFORMANCE);
        }

        @JavascriptInterface
        public double getMinTemperature() {
            double h = lastMesure(user, SensorType.HUMIDITY);
            return MeasureUtil.calculateMinTemperature(h);
        }

        @JavascriptInterface
        public double getOptimalTemperature() {
            double h = lastMesure(user, SensorType.HUMIDITY);
            return MeasureUtil.calculateOptimalTemperature(h);
        }

        @JavascriptInterface
        public double getMaxTemperature() {
            double h = lastMesure(user, SensorType.HUMIDITY);
            return MeasureUtil.calculateMaxTemperature(h);
        }

        @JavascriptInterface
        public double getCurrentTemperature() {
            return lastMesure(user, SensorType.TEMPERATURE);
        }

        @JavascriptInterface
        public double getMinHumidity() {
            double t = lastMesure(user, SensorType.TEMPERATURE);

            double r = MeasureUtil.calculateMinHumidity(t);

            return r;
        }

        @JavascriptInterface
        public double getMaxHumidity() {
            double t = lastMesure(user, SensorType.TEMPERATURE);
            double r = MeasureUtil.calculateMaxHumidity(t);
            return r;
        }

        @JavascriptInterface
        public double getCurrentHumidity() {
            return lastMesure(user, SensorType.HUMIDITY);
        }

        @JavascriptInterface
        public double getMinCO2() {
            return MeasureUtil.optimalCO2;
        }

        @JavascriptInterface
        public double getAcceptableCO2() {
            return MeasureUtil.maxCO2;
        }

        @JavascriptInterface
        public double getCurrentCO2() {
            return lastMesure(user, SensorType.CO2);
        }

        @JavascriptInterface
        public double getMinLuminosity() {
            return MeasureUtil.minLuminosity;
        }

        @JavascriptInterface
        public double geComfortLuminosity() {
            return MeasureUtil.comfortableLuminosity;
        }

        @JavascriptInterface
        public double getCurrentLuminosity() {
            return lastMesure(user, SensorType.LUMINOSITY);
        }

        @JavascriptInterface
        public double getMinColorTemperature() {
            double l = lastMesure(user, SensorType.LUMINOSITY);
            return MeasureUtil.calculateMinColorTemperature(l);
        }

        @JavascriptInterface
        public double getMaxColorTemperature() {
            double l = lastMesure(user, SensorType.LUMINOSITY);
            return MeasureUtil.calculateMaxColorTemperature(l);
        }

        @JavascriptInterface
        public double getCurrentColorTemperature() {
            return lastMesure(user, SensorType.COLOR_TEMPERATURE);
        }
    }

}
