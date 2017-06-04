package davidfdez.capteuratmospherique;

import android.annotation.SuppressLint;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;

import java.util.Date;
import java.util.LinkedList;

@SuppressLint("SetJavaScriptEnabled")
public class ShowHistogramActivity extends AbstractChartsActivity {
    private WebView webView;
    private LinkedList<Date> dates;
    private LinkedList<Double> valeurs;
    private Date dateTo;
    private Date dateFrom;
    private String capteur;

    @SuppressWarnings("deprecation")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_histogram);
        Bundle bundle = getIntent().getExtras();
        capteur = bundle.getString("capteur");
        int yearTo = bundle.getInt("yearTo");
        int monthTo = bundle.getInt("monthTo");
        int dayTo = bundle.getInt("dayTo");
        dateTo = new Date(yearTo, monthTo, dayTo);
        int yearFrom = bundle.getInt("yearFrom");
        int monthFrom = bundle.getInt("monthFrom");
        int dayFrom = bundle.getInt("dayFrom");
        dateFrom = new Date(yearFrom, monthFrom, dayFrom);

        dates = new LinkedList();
        valeurs = new LinkedList();

        getDataSql();

        webView = (WebView) findViewById(R.id.webLines);
        configureWebView(webView);
        webView.loadUrl("file:///android_asset/histogram.html");
    }

    public void getDataSql() {
        AdminSQLiteOpenHelper admin = new AdminSQLiteOpenHelper(this);
        SQLiteDatabase bd = admin.getWritableDatabase();
        Cursor raw = bd.rawQuery(
                "select idMesure," + capteur + " from Mesure where idUser ='" + user + "' and idMesure > " + dateFrom.getTime() + " and idMesure < " + dateTo.getTime(), null);

        if (raw.moveToFirst()) {
            long stringDateSql = raw.getLong(0);
            Date dateSql = new Date(stringDateSql);
            double aux = raw.getDouble(1);

            valeurs.add(aux);
            dates.add(dateSql);


            while (raw.moveToNext()) {
                stringDateSql = raw.getLong(0);
                dateSql = new Date(stringDateSql);
                aux = raw.getDouble(1);

                valeurs.add(aux);
                dates.add(dateSql);


            }
        }
    }

    public class WebAppInterface {

        @JavascriptInterface
        public String getNom() {
            return capteur;
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
        public String getTimeMesure(int a) {
            return dates.get(a).toString();
        }


    }

}