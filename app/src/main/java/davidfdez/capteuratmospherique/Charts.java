package davidfdez.capteuratmospherique;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;


public class Charts extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_charts);

    }

    public void openLineChart(View view) {
        Intent intent = new Intent(Charts.this, SelectLineChart.class);
        startActivity(intent);
    }

    public void openCalendar(View view) {
        Intent intent = new Intent(Charts.this, SelectLineChart.class);
        startActivity(intent);
    }

    public void openHistogram(View view) {
        Intent intent = new Intent(Charts.this, SelectLineChart.class);
        startActivity(intent);
    }

    public void openClustering(View view) {
        Intent intent = new Intent(Charts.this, SelectLineChart.class);
        startActivity(intent);
    }


}