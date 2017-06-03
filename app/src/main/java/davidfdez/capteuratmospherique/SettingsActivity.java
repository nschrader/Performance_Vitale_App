package davidfdez.capteuratmospherique;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Set;

public class SettingsActivity extends AppCompatActivity {
    public static String EXTRA_ADDRESS = "device_address";

    private Button btnPaired;
    private Button btnExport;
    private ListView devicelist;
    private String user = null;

    //Bluetooth
    private BluetoothAdapter myBluetooth = null;
    private Set<BluetoothDevice> pairedDevices;
    private AdminSQLiteOpenHelper admin = new AdminSQLiteOpenHelper(this);
    private AdapterView.OnItemClickListener myListClickListener = new AdapterView.OnItemClickListener() {
        public void onItemClick(AdapterView<?> av, View v, int arg2, long arg3) {
            // Get the device MAC address, the last 17 chars in the View
            String info = ((TextView) v).getText().toString();
            String address = info.substring(info.length() - 17);

            // Make an intent to start next activity.
            Intent serviceIntent = new Intent(SettingsActivity.this, BluetoothService.class);
            serviceIntent.putExtra(EXTRA_ADDRESS, address);
            serviceIntent.putExtra("user", user);

            startService(serviceIntent);

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        Bundle bundle = getIntent().getExtras();
        user = bundle.getString("user");

        //Calling widgets
        btnPaired = (Button) findViewById(R.id.button);
        devicelist = (ListView) findViewById(R.id.listView);
        btnExport = (Button) findViewById(R.id.expButton);

        //if the device has bluetooth
        myBluetooth = BluetoothAdapter.getDefaultAdapter();

        btnPaired.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (myBluetooth == null) {
                    Toast.makeText(getApplicationContext(), "Bluetooth Device Not Available", Toast.LENGTH_LONG).show();
                } else if (!myBluetooth.isEnabled()) {
                    //Ask to the user turn the bluetooth on
                    Intent turnBTon = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                    startActivityForResult(turnBTon, 1);
                } else
                    pairedDevicesList();
            }
        });

    }

    private void pairedDevicesList() {
        pairedDevices = myBluetooth.getBondedDevices();
        ArrayList list = new ArrayList();

        if (pairedDevices.size() > 0) {
            for (BluetoothDevice bt : pairedDevices) {
                list.add(bt.getName() + "\n" + bt.getAddress()); //Get the device's name and the address
            }
        } else {
            Toast.makeText(getApplicationContext(), "No Paired Bluetooth Devices Found.", Toast.LENGTH_LONG).show();
        }

        final ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, list);
        devicelist.setAdapter(adapter);
        devicelist.setOnItemClickListener(myListClickListener); //Method called when the device from the list is clicked

    }

    private boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(state);
    }

    private void writeDBToFile(PrintWriter printWriter) {
        AdminSQLiteOpenHelper admin = new AdminSQLiteOpenHelper(this);
        SQLiteDatabase bd = admin.getWritableDatabase();
        Cursor fila = bd.rawQuery("select * from Mesure", null);
        if (fila.moveToFirst()) {
            printWriter.format("#time, user, performance, CO2, luminosity, latitude, longitude, temperature, humidity, color\n");
            do {
                printWriter.format("%s, %s, %s, %s, %s, %s, %s, %s, %s, %s\n",
                        fila.getString(0),
                        fila.getString(1),
                        String.valueOf(fila.getDouble(2)),
                        String.valueOf(fila.getDouble(3)),
                        String.valueOf(fila.getDouble(4)),
                        fila.getString(5),
                        fila.getString(6),
                        String.valueOf(fila.getDouble(7)),
                        String.valueOf(fila.getDouble(8)),
                        String.valueOf(fila.getDouble(9)));
            } while (fila.moveToNext());
        }
        fila.close();
    }

    public void grabar(View v) {
        String nomarchivo = "db.csv";
        if (!isExternalStorageWritable())
            Toast.makeText(this, "External storage not writable", Toast.LENGTH_SHORT).show();
        File path = getApplicationContext().getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS);
        File file = new File(path, nomarchivo);
        Toast.makeText(this, "Saved to" + file.getAbsolutePath(), Toast.LENGTH_SHORT).show();
        if (!file.canWrite())
            file = new File(path, nomarchivo);
        try {
            PrintWriter printWriter = new PrintWriter(file);
            writeDBToFile(printWriter);
            printWriter.close();
        } catch (FileNotFoundException ioe) {
            ioe.printStackTrace();
        }
    }

    public void importData(View v) {
        String nomarchivo = "db.csv";
        File path = getApplicationContext().getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS);
        File file = new File(path, nomarchivo);
        try {
            BufferedReader stream = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
            String line;
            int i = 0;
            while ((line = stream.readLine()) != null) {
                if (line.startsWith("#"))
                    continue;
                String[] splitted = line.split(",");

                long time = Long.parseLong(splitted[0]);
                String user = splitted[1];
                double performance = Double.parseDouble(splitted[2]);
                double CO2 = Double.parseDouble(splitted[3]);
                double luminosity = Double.parseDouble(splitted[4]);
                String latitude = splitted[5];
                String longitude = splitted[6];
                double temperature = Double.parseDouble(splitted[7]);
                double humidity = Double.parseDouble(splitted[8]);
                double color = Double.parseDouble(splitted[9]);

                if (admin.setMeasures(time, user, CO2, humidity, temperature, luminosity, color, latitude, longitude))
                    i++;
            }
            Toast.makeText(this, "Imported " + i + " data sets", Toast.LENGTH_LONG).show();
        } catch (IOException | NumberFormatException | ArrayIndexOutOfBoundsException e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

}