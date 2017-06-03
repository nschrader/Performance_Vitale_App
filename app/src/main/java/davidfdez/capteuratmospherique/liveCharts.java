package davidfdez.capteuratmospherique;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.webkit.ConsoleMessage;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.Toast;


public class liveCharts extends AppCompatActivity {
    private WebView webView;
    private String user = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_live_charts);

        webView = (WebView) findViewById(R.id.web);
        webView.addJavascriptInterface(new WebAppInterface(), "Android");
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setLoadWithOverviewMode(true);
        webView.getSettings().setUseWideViewPort(true);
        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public boolean onConsoleMessage(ConsoleMessage consoleMessage) {
                Toast.makeText(getApplicationContext(), consoleMessage.message() + " At line " + consoleMessage.lineNumber(), Toast.LENGTH_LONG).show();
                return super.onConsoleMessage(consoleMessage);
            }
        });

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
            double t = lastMesure(user, SensorType.HUMIDITY);
            return MeasureUtil.calculateMinHumidity(t);
        }

        @JavascriptInterface
        public double getMaxHumidity() {
            double t = lastMesure(user, SensorType.HUMIDITY);
            return MeasureUtil.calculateMaxHumidity(t);
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
