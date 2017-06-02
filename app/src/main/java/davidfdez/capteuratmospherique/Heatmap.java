package davidfdez.capteuratmospherique;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;

import java.util.ArrayList;
import java.util.List;


public class Heatmap extends AppCompatActivity {
    private WebView webView;
    private List<Double> comfortLatitudes = new ArrayList<>();
    private List<Double> comfortLongitudes = new ArrayList<>();
    private List<Double> discomfortLatitudes = new ArrayList<>();
    private List<Double> discomforLongitudes = new ArrayList<>();
    private String user = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_heatmap);

        getOver50();
        getUnder50();

        webView = (WebView) findViewById(R.id.heatmap);
        webView.addJavascriptInterface(new WebAppInterface(), "Android");
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setLoadWithOverviewMode(true);
        webView.getSettings().setUseWideViewPort(true);
        webView.loadUrl("file:///android_asset/heatmap.html");

    }

    public void getOver50() {
        AdminSQLiteOpenHelper admin = new AdminSQLiteOpenHelper(this,
                "administracion", null, 1);
        SQLiteDatabase bd = admin.getWritableDatabase();
        Cursor fila = bd.rawQuery(
                "select Latitude, Longitude from Mesure where Performance > 50", null);

        if (fila.moveToFirst()) {
            String[] splittedLat = fila.getString(0).split(",");
            String[] splittedLong = fila.getString(1).split(",");
            comfortLatitudes.add(Double.parseDouble(splittedLat[0]));
            comfortLongitudes.add(Double.parseDouble(splittedLong[0]));


            while (fila.moveToNext()) {
                splittedLat = fila.getString(0).split(",");
                splittedLong = fila.getString(1).split(",");
                comfortLatitudes.add(Double.parseDouble(splittedLat[0]));
                comfortLongitudes.add(Double.parseDouble(splittedLong[0]));

            }
        }
    }

    public void getUnder50() {
        AdminSQLiteOpenHelper admin = new AdminSQLiteOpenHelper(this,
                "administracion", null, 1);
        SQLiteDatabase bd = admin.getWritableDatabase();
        Cursor fila = bd.rawQuery(
                "select Latitude, Longitude from Mesure where Performance <= 50", null);

        if (fila.moveToFirst()) {
            String[] splittedLat = fila.getString(0).split(",");
            String[] splittedLong = fila.getString(1).split(",");
            discomfortLatitudes.add(Double.parseDouble(splittedLat[0]));
            discomforLongitudes.add(Double.parseDouble(splittedLong[0]));


            while (fila.moveToNext()) {
                splittedLat = fila.getString(0).split(",");
                splittedLong = fila.getString(1).split(",");
                discomfortLatitudes.add(Double.parseDouble(splittedLat[0]));
                discomforLongitudes.add(Double.parseDouble(splittedLong[0]));

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