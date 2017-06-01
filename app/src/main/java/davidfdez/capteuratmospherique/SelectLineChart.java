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


public class SelectLineChart extends AppCompatActivity {
    private int year, month, day;
    private Button buttonDateFrom;
    private Button buttonDateTo;
    private Date dateFrom;
    private Date dateTo;
    private RadioButton temperature, humidite, co2, chaleur, eclairage;
    @SuppressWarnings("deprecation")
    private DatePickerDialog.OnDateSetListener myDateListener = new
            DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker arg0,
                                      int arg1, int arg2, int arg3) {
                    // TODO Auto-generated method stub

                    dateFrom = new Date(arg1, arg2 + 1, arg3);
                    buttonDateFrom.setText("From: " + String.valueOf(dateFrom.getDate()) + "/" + String.valueOf(dateFrom.getMonth()) + "/" + String.valueOf(dateFrom.getYear()));
                }
            };
    @SuppressWarnings("deprecation")
    private DatePickerDialog.OnDateSetListener myDateListener2 = new
            DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker arg0,
                                      int arg1, int arg2, int arg3) {
                    // TODO Auto-generated method stub

                    dateTo = new Date(arg1, arg2 + 1, arg3);
                    buttonDateTo.setText("To: " + String.valueOf(dateTo.getDate()) + "/" + String.valueOf(dateTo.getMonth()) + "/" + String.valueOf(dateTo.getYear()));
                }
            };

    @Override
    protected void onCreate(Bundle savedInstanceState) {


        Calendar calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);

        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);
        dateFrom = null;
        dateTo = null;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_line_chart);
        buttonDateFrom = (Button) findViewById(R.id.date1);
        buttonDateTo = (Button) findViewById(R.id.date2);
        temperature = (RadioButton) findViewById(R.id.temperature);
        humidite = (RadioButton) findViewById(R.id.humidite);
        co2 = (RadioButton) findViewById(R.id.co2);
        chaleur = (RadioButton) findViewById(R.id.chaleur);
        eclairage = (RadioButton) findViewById(R.id.eclairage);

        buttonDateFrom.setText("Select Date From");
        buttonDateTo.setText("Select Date To");

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
        // TODO Auto-generated method stub
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
        if (temperature.isChecked() == true) {
            capteur = "Temperature";
        } else if (co2.isChecked() == true) {
            capteur = "CO2";
        } else if (eclairage.isChecked() == true) {
            capteur = "Eclairage";
        } else if (chaleur.isChecked() == true) {
            capteur = "Chaleur lumiere";
        } else {
            capteur = "Humidite";
        }

        Intent i = new Intent(this, ShowLineChart.class);
        i.putExtra("capteur", capteur);
        i.putExtra("yearFrom", dateFrom.getYear());
        i.putExtra("monthFrom", dateFrom.getMonth());
        i.putExtra("dayFrom", dateFrom.getDate());
        i.putExtra("yearTo", dateTo.getYear());
        i.putExtra("monthTo", dateTo.getMonth());
        i.putExtra("dayTo", dateTo.getDate());

        startActivity(i);


    }

}



