package davidfdez.capteuratmospherique;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;


public class ChartsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_charts);
    }

    public void openLineChart(View view) {
        Intent intent = new Intent(ChartsActivity.this, SelectLineChartActivity.class);
        startActivity(intent);
    }

    public void openCalendar(View view) {
        Intent intent = new Intent(ChartsActivity.this, CalendarActivity.class);
        startActivity(intent);
    }

    public void openHistogram(View view) {
        Intent intent = new Intent(ChartsActivity.this, SelectHistogramActivity.class);
        startActivity(intent);
    }

    public void openClustering(View view) {
        Intent intent = new Intent(ChartsActivity.this, ClusteringActivity.class);
        startActivity(intent);
    }

}