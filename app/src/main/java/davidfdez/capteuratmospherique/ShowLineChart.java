package davidfdez.capteuratmospherique;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;

import java.util.Date;
import java.util.LinkedList;

@SuppressLint("SetJavaScriptEnabled")
public class ShowLineChart extends ActionBarActivity {
    WebView webView;
    LinkedList<Date> dates;
    LinkedList<Double> valeurs;
    private Date dateTo;
    private Date dateFrom;
    private String capteur;
    public double max;
    public double min;
    @SuppressWarnings("deprecation")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_line_chart);

        Bundle bundle = getIntent().getExtras();
        //dateTo =new Date(bundle.getString("dateTo"));
        //dateFrom=new Date(bundle.getString("dateFrom"));
        capteur=bundle.getString("capteur");
        int yearTo=bundle.getInt("yearTo");
        int monthTo=bundle.getInt("monthTo");
        int dayTo=bundle.getInt("dayTo");
        dateTo=new Date(yearTo,monthTo,dayTo);
        int yearFrom=bundle.getInt("yearFrom");
        int monthFrom=bundle.getInt("monthFrom");
        int dayFrom=bundle.getInt("dayFrom");
        dateFrom=new Date(yearFrom,monthFrom,dayFrom);

        dates=new LinkedList();
        valeurs=new LinkedList();
        /*
        valeurs.add(600.0);
        valeurs.add(100.0);
        dates.add(dateFrom);
        dates.add(dateTo);
        valeurs.add(600.0);
        valeurs.add(100.0);
        */
        //introducir sql
        //datos de tipo capteur(string)
        //entre dateFrom y dateTo
        for(int i=0;i<0;i++){
            valeurs.add(0.0);
            dates.add(dateTo);
        }
        max=0;
        min=100;
        if(capteur=="Temperature"){
            max=0;
            min=100;
        }
        else if(capteur=="Eclairage"){
            max=0;
            min=100;
        }
        else if(capteur=="CO2"){
            max=0;
            min=100;
        }
        else if(capteur=="Chaleur lumiere"){
            max=0;
            min=100;
        }
        else if(capteur=="Humidite"){
            max=0;
            min=100;
        }


        webView = (WebView)findViewById(R.id.webLines);
        webView.addJavascriptInterface(new WebAppInterface(), "Android");

        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setBuiltInZoomControls(true);
        webView.loadUrl("file:///android_asset/lineChart.html");



    }

    public class WebAppInterface {


        @JavascriptInterface
        public String getNom(){

            return capteur;

        }
        @JavascriptInterface
        public double getMax(){

            return 0;

        }
        @JavascriptInterface
        public double getMin(){

            return 100;

        }
        @JavascriptInterface
        public int getSize(){

            return dates.size();

        }
        @JavascriptInterface
        public double getValeur(int a){

            return valeurs.get(a);

        }
        @JavascriptInterface
        public int getYear(int a){

            return dates.get(a).getYear();

        }
        @JavascriptInterface
        public int getMonth(int a){

            return dates.get(a).getMonth();

        }
        @JavascriptInterface
        public int getDay(int a){

            return dates.get(a).getDate();

        }
        @JavascriptInterface
        public int getHour(int a){

            return dates.get(a).getHours();

        }
        @JavascriptInterface
        public int getMinute(int a){

            return dates.get(a).getMinutes();

        }







    }

}