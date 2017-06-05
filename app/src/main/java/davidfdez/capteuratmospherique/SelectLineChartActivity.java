package davidfdez.capteuratmospherique;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.RadioButton;

import java.util.Calendar;
import java.util.Date;


public class SelectLineChartActivity extends AppCompatActivity {
    private int year, month, day;
    private Button buttonDateFrom;
    private Button buttonDateTo;
    private Date dateFrom;
    private Date dateTo;
    private RadioButton temperature, co2, humidite, chaleur, eclairage, performance;

    @SuppressWarnings("deprecation")
    private DatePickerDialog.OnDateSetListener myDateListener = new
            DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker arg0, int arg1, int arg2, int arg3) {
                    dateFrom = new Date(arg1, arg2, arg3);
                    buttonDateFrom.setText("From: " + String.valueOf(dateFrom.getDate()) + "/" + String.valueOf(dateFrom.getMonth() + 1) + "/" + String.valueOf(dateFrom.getYear()));
                }
            };

    @SuppressWarnings("deprecation")
    private DatePickerDialog.OnDateSetListener myDateListener2 = new
            DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker arg0, int arg1, int arg2, int arg3) {
                    dateTo = new Date(arg1, arg2, arg3);
                    buttonDateTo.setText("To: " + String.valueOf(dateTo.getDate()) + "/" + String.valueOf(dateTo.getMonth() + 1) + "/" + String.valueOf(dateTo.getYear()));
                }
            };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Calendar calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);

        dateFrom = new Date(2016, 10, 10);
        dateTo = new Date(2020, 11, 11);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_line_chart);

        buttonDateFrom = (Button) findViewById(R.id.date1);
        buttonDateTo = (Button) findViewById(R.id.date2);
        temperature = (RadioButton) findViewById(R.id.temperature);
        humidite = (RadioButton) findViewById(R.id.humidite);
        co2 = (RadioButton) findViewById(R.id.co2);
        chaleur = (RadioButton) findViewById(R.id.chaleur);
        eclairage = (RadioButton) findViewById(R.id.eclairage);
        performance = (RadioButton) findViewById(R.id.performance);
        temperature.setChecked(true);
        buttonDateFrom.setText(R.string.selectDateFrom);
        buttonDateTo.setText(R.string.selectDateTo);

    }

    @SuppressWarnings("deprecation")
    public void setDate(View view) {
        showDialog(999);

    }

    @SuppressWarnings("deprecation")
    public void setDate2(View view) {
        showDialog(1000);
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        if (id == 999) {
            return new DatePickerDialog(this,
                    myDateListener, year, month, day);
        }
        if (id == 1000) {
            return new DatePickerDialog(this,
                    myDateListener2, year, month, day);
        }
        return null;
    }

    @SuppressWarnings("deprecation")
    public void showChart(View view) {
        String capteur = null;
        CharSequence nom = null;
        if (temperature.isChecked() == true) {
            capteur = "Temperature";
            nom = temperature.getText();
        } else if (co2.isChecked() == true) {
            capteur = "CO2Mesure";
            nom = co2.getText();
        } else if (eclairage.isChecked() == true) {
            capteur = "Luminosite";
            nom = eclairage.getText();
        } else if (chaleur.isChecked() == true) {
            capteur = "TempLum";
            nom = chaleur.getText();
        } else if (performance.isChecked() == true) {
            capteur = "Performance";
            nom = performance.getText();
        } else if (humidite.isChecked() == true) {
            capteur = "Humidite";
            nom = humidite.getText();
        } else
            return;

        Intent i = new Intent(this, ShowLineChartActivity.class);
        i.putExtras(getIntent());
        i.putExtra("nom", nom.toString());
        i.putExtra("capteur", capteur);
        i.putExtra("yearFrom", dateFrom.getYear() - 1900);
        i.putExtra("monthFrom", dateFrom.getMonth());
        i.putExtra("dayFrom", dateFrom.getDate());
        i.putExtra("yearTo", dateTo.getYear() - 1900);
        i.putExtra("monthTo", dateTo.getMonth());
        i.putExtra("dayTo", dateTo.getDate() + 1);

        startActivity(i);
    }

}



