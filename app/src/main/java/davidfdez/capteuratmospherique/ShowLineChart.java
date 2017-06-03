package davidfdez.capteuratmospherique;

import android.annotation.SuppressLint;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;

import java.util.Date;
import java.util.LinkedList;

@SuppressLint("SetJavaScriptEnabled")
public class ShowLineChart extends ActionBarActivity {
    public String user = "";
    public double max;
    public double min;
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
        setContentView(R.layout.activity_show_line_chart);

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

        /*
         * introducir sql
         * datos de tipo capteur(string)
         * entre dateFrom y dateTo
         */
        for (int i = 0; i < 0; i++) {
            valeurs.add(0.0);
            dates.add(dateTo);
        }
        max = 0;
        min = 0;
        if (capteur.equals("Temperature")) {
            max = 26;
            min = 20;
        } else if (capteur.equals("Luminosite")) {
            max = 800;
            min = 300;
        } else if (capteur.equals("CO2Mesure")) {
            max = 1500;
            min = 400;
        } else if (capteur.equals("TempLum")) {
            max = 7016;
            min = 2919;
        } else if (capteur.equals("Humidite")) {
            max = 70;
            min = 30;
        } else if (capteur.equals("Performance")) {
            max = 100;
            min = 0;
        }

        webView = (WebView) findViewById(R.id.webLines);
        webView.addJavascriptInterface(new WebAppInterface(), "Android");
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setBuiltInZoomControls(true);
        webView.loadUrl("file:///android_asset/lineChart.html");
    }

    public void getDataSql() {
        AdminSQLiteOpenHelper admin = new AdminSQLiteOpenHelper(this,
                "administracion", null, 1);
        SQLiteDatabase bd = admin.getWritableDatabase();
        Cursor raw = bd.rawQuery(
                "select idMesure," + capteur + " from Mesure where idUser ='" + user + "' and idMesure > "+ dateFrom.getTime() + " and idMesure < "+ dateTo.getTime(), null);

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
        public double getMax() {
            return max;
        }

        @JavascriptInterface
        public double getMin() {
            return min;
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