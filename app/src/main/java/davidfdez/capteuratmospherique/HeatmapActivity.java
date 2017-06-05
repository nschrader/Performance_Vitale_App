package davidfdez.capteuratmospherique;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;

import java.util.ArrayList;
import java.util.List;


public class HeatmapActivity extends AbstractChartsActivity {
    private WebView webView;
    private List<Double> comfortLatitudes = new ArrayList<>();
    private List<Double> comfortLongitudes = new ArrayList<>();
    private List<Double> discomfortLatitudes = new ArrayList<>();
    private List<Double> discomforLongitudes = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_heatmap);

        getOver50();
        getUnder50();

        webView = (WebView) findViewById(R.id.heatmap);
        webView.addJavascriptInterface(new WebAppInterface(), "Android");
        configureWebView(webView);
        webView.loadUrl("file:///android_asset/heatmap.html");
    }

    public double convertLatLong(double value) {
        double r = (int) (value / 100);
        r += (value - r * 100) / 60.0;
        return r;
    }

    public void getOver50() {
        AdminSQLiteOpenHelper admin = new AdminSQLiteOpenHelper(this);
        SQLiteDatabase bd = admin.getWritableDatabase();
        Cursor fila = bd.rawQuery("select Latitude, Longitude from Mesure where Performance > 50", null);

        if (fila.moveToFirst()) {
            String[] splittedLat = fila.getString(0).split(",");
            String[] splittedLong = fila.getString(1).split(",");
            comfortLatitudes.add(convertLatLong(Double.parseDouble(splittedLat[0])));
            comfortLongitudes.add(convertLatLong(Double.parseDouble(splittedLong[0])));

            while (fila.moveToNext()) {
                splittedLat = fila.getString(0).split(",");
                splittedLong = fila.getString(1).split(",");
                comfortLatitudes.add(convertLatLong(Double.parseDouble(splittedLat[0])));
                comfortLongitudes.add(convertLatLong(Double.parseDouble(splittedLong[0])));

            }
        }
    }

    public void getUnder50() {
        AdminSQLiteOpenHelper admin = new AdminSQLiteOpenHelper(this);
        SQLiteDatabase bd = admin.getWritableDatabase();
        Cursor fila = bd.rawQuery("select Latitude, Longitude from Mesure where Performance <= 50", null);

        if (fila.moveToFirst()) {
            String[] splittedLat = fila.getString(0).split(",");
            String[] splittedLong = fila.getString(1).split(",");
            discomfortLatitudes.add(convertLatLong(Double.parseDouble(splittedLat[0])));
            discomforLongitudes.add(convertLatLong(Double.parseDouble(splittedLong[0])));

            while (fila.moveToNext()) {
                splittedLat = fila.getString(0).split(",");
                splittedLong = fila.getString(1).split(",");
                discomfortLatitudes.add(convertLatLong(Double.parseDouble(splittedLat[0])));
                discomforLongitudes.add(convertLatLong(Double.parseDouble(splittedLong[0])));

            }
        }
    }

    public class WebAppInterface {

        @JavascriptInterface
        public double getLatitude(int i) {
            return comfortLatitudes.get(i);
        }

        @JavascriptInterface
        public double getLongitude(int i) {
            return comfortLongitudes.get(i);
        }

        @JavascriptInterface
        public int getSize() {
            return comfortLongitudes.size();
        }

        @JavascriptInterface
        public double getLatitudeDis(int i) {
            return discomfortLatitudes.get(i);
        }

        @JavascriptInterface
        public double getLongitudeDis(int i) {
            return discomforLongitudes.get(i);
        }

        @JavascriptInterface
        public int getSizeDis() {
            return discomforLongitudes.size();
        }
    }
}