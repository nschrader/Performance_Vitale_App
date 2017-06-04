package davidfdez.capteuratmospherique;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.webkit.WebView;

import java.util.Date;
import java.util.LinkedList;

public abstract class AbstractGraphsActivity extends AbstractChartsActivity {
    protected LinkedList<Date> dates = new LinkedList<>();
    protected LinkedList<Double> valeurs = new LinkedList<>();
    protected Date dateTo;
    protected Date dateFrom;
    protected String capteur;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
    }

    protected void enableZoom(WebView webView) {
        webView.getSettings().setBuiltInZoomControls(true);
        webView.getSettings().setDisplayZoomControls(true);
    }
}
