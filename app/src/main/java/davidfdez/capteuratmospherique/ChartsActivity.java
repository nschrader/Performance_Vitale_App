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
        createIntentAndStartActivity(SelectLineChartActivity.class);
    }

    public void openCalendar(View view) {
        createIntentAndStartActivity(CalendarActivity.class);
    }

    public void openHistogram(View view) {
        createIntentAndStartActivity(SelectHistogramActivity.class);
    }

    public void openClustering(View view) {
        createIntentAndStartActivity(ClusteringActivity.class);
    }

    private void createIntentAndStartActivity(Class<?> clazz) {
        Intent intent = new Intent(ChartsActivity.this, clazz);
        intent.putExtras(getIntent());
        startActivity(intent);
    }

}