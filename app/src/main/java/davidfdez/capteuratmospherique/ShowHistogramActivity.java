package davidfdez.capteuratmospherique;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.widget.Toast;

import java.util.Date;

public class ShowHistogramActivity extends AbstractGraphsActivity {
    private WebView webView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_histogram);

        getDataSql();
        webView = (WebView) findViewById(R.id.webLines);
        webView.addJavascriptInterface(new ShowHistogramActivity.WebAppInterface(), "Android");
        configureWebView(webView);
        enableZoom(webView);
        webView.loadUrl("file:///android_asset/histogram.html");
    }

    public void getDataSql() {
        AdminSQLiteOpenHelper admin = new AdminSQLiteOpenHelper(this);
        SQLiteDatabase bd = admin.getWritableDatabase();
        Cursor raw = bd.rawQuery(
                "select idMesure," + capteur + " from Mesure where idUser ='" + user + "' and idMesure > " + dateFrom.getTime() + " and idMesure < " + dateTo.getTime(), null);

        if (!raw.moveToFirst()) {
            this.finish();
            Toast.makeText(getApplicationContext(), "No data!", Toast.LENGTH_SHORT).show();
        } else {
            do {
                long stringDateSql = raw.getLong(0);
                Date dateSql = new Date(stringDateSql);
                double aux = raw.getDouble(1);

                valeurs.add(aux);
                dates.add(dateSql);
            } while (raw.moveToNext());
        }
    }

    public class WebAppInterface {

        @JavascriptInterface
        public String getNom() {
            return getIntent().getExtras().getString("nom");
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