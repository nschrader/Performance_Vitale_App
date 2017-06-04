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
public class CalendarActivity extends AbstractChartsActivity {
    private WebView webView;
    private LinkedList<Date> dates = new LinkedList();
    private LinkedList<Double> valeurs = new LinkedList();


    @SuppressWarnings("deprecation")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);

        getDataSql();
        average();

        webView = (WebView) findViewById(R.id.calendar);
        webView.addJavascriptInterface(new CalendarActivity.WebAppInterface(), "Android");
        configureWebView(webView);
        webView.loadUrl("file:///android_asset/calendar.html");
    }

    public void average() {
        LinkedList<Double> v = new LinkedList();
        LinkedList<Date> d = new LinkedList();
        double cont = 1;
        for (int i = 0; i < valeurs.size(); i++) {
            if (i + 1 != valeurs.size() && sameDay(dates.get(i), dates.get(i + 1))) {
                valeurs.set(i + 1, valeurs.get(i) + valeurs.get(i + 1));
                cont++;

            } else {

                v.add(valeurs.get(i) / cont);
                d.add(dates.get(i));
                cont = 1;
            }
        }
        valeurs = v;
        dates = d;
    }

    public void getDataSql() {
        AdminSQLiteOpenHelper admin = new AdminSQLiteOpenHelper(this);
        SQLiteDatabase bd = admin.getWritableDatabase();
        Cursor raw = bd.rawQuery(
                "select idMesure, Performance from Mesure where idUser ='" + user + "' ORDER BY idMesure DESC", null);

        if (raw.moveToFirst()) {
            long stringDateSql = raw.getLong(0);
            Date dateSql = new Date(stringDateSql);
            double aux = raw.getDouble(1);

            valeurs.add(aux - 50);
            dates.add(dateSql);


            while (raw.moveToNext()) {
                stringDateSql = raw.getLong(0);
                dateSql = new Date(stringDateSql);
                aux = raw.getDouble(1);

                valeurs.add(aux - 50);
                dates.add(dateSql);


            }
        }
    }

    public boolean sameDay(Date a, Date b) {
        return a.getDate() == b.getDate() && a.getMonth() == b.getMonth() && a.getYear() == b.getYear();

    }

    public class WebAppInterface {

        @JavascriptInterface
        public int getSize() {
            return dates.size();
        }

        @JavascriptInterface
        public double getValue(int a) {
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

    }

}