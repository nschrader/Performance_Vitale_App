package davidfdez.capteuratmospherique;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.widget.Toast;

import java.util.Date;
import java.util.LinkedList;

public class ShowLineChartActivity extends AbstractGraphsActivity {
    private WebView webView;
    private LinkedList<Double> min = new LinkedList<>();
    private LinkedList<Double> max = new LinkedList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_line_chart);

        getDataSql();
        webView = (WebView) findViewById(R.id.webLines);
        webView.addJavascriptInterface(new ShowLineChartActivity.WebAppInterface(), "Android");
        configureWebView(webView);
        enableZoom(webView);
        webView.loadUrl("file:///android_asset/lineChart.html");
    }

    public void getDataSql() {
        AdminSQLiteOpenHelper admin = new AdminSQLiteOpenHelper(this);
        SQLiteDatabase bd = admin.getWritableDatabase();
        Cursor raw = bd.rawQuery(
                "select * from Mesure where idUser ='" + user + "' and idMesure > " + dateFrom.getTime() + " and idMesure < " + dateTo.getTime(), null);

        if (!raw.moveToFirst()) {
            this.finish();
            Toast.makeText(getApplicationContext(), "No data!", Toast.LENGTH_SHORT).show();
        } else {
            do {
                long stringDateSql = raw.getLong(raw.getColumnIndex("idMesure"));
                Date dateSql = new Date(stringDateSql);
                double val = raw.getDouble(raw.getColumnIndex(capteur));
                double _min = 0, _max = 0;
                switch (capteur) {
                    case "Temperature":
                        double h = raw.getLong(raw.getColumnIndex("Humidite"));
                        _min = MeasureUtil.calculateMinTemperature(h);
                        _max = MeasureUtil.calculateMaxTemperature(h);
                        break;
                    case "Luminosite":
                        _min = MeasureUtil.minLuminosity;
                        _max = MeasureUtil.comfortableLuminosity;
                        break;
                    case "CO2Mesure":
                        _min = MeasureUtil.optimalCO2;
                        _max = MeasureUtil.maxCO2;
                        break;
                    case "TempLum":
                        double l = raw.getLong(raw.getColumnIndex("Luminosite"));
                        _min = MeasureUtil.calculateMinColorTemperature(l);
                        _max = MeasureUtil.calculateMaxColorTemperature(l);
                        break;
                    case "Humidite":
                        double t = raw.getLong(raw.getColumnIndex("Temperature"));
                        _min = MeasureUtil.calculateMinHumidity(t);
                        _max = MeasureUtil.calculateMinHumidity(t);
                        break;
                    case "Performance":
                        _min = 75;
                        _max = 100;
                }

                valeurs.add(val);
                dates.add(dateSql);
                min.add(_min);
                max.add(_max);
            } while (raw.moveToNext());
        }
    }

    public class WebAppInterface {

        @JavascriptInterface
        public String getNom() {
            return getIntent().getExtras().getString("nom");
        }

        @JavascriptInterface
        public double getMax(int a) {
            return max.get(a);
        }

        @JavascriptInterface
        public double getMin(int a) {
            return min.get(a);
        }

        @JavascriptInterface
        public int getSize() {
            return dates.size();
        }

        @JavascriptInterface
        public double getValeur(int a) {
            return valeurs.get(a);
        }

        @JavascriptInterface
        public int getYear(int a) {
            return dates.get(a).getYear();
        }

        @JavascriptInterface
        public int getMonth(int a) {
            return dates.get(a).getMonth();
        }

        @JavascriptInterface
        public int getDay(int a) {
            return dates.get(a).getDate();
        }

        @JavascriptInterface
        public int getHour(int a) {
            return dates.get(a).getHours();
        }

        @JavascriptInterface
        public int getMinute(int a) {
            return dates.get(a).getMinutes();
        }

    }

}