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

public class Settings extends AppCompatActivity {
    public static String EXTRA_ADDRESS = "device_address";

    private Button btnPaired;
    private Button btnExport;
    private ListView devicelist;
    private String user = null;

    //Bluetooth
    private BluetoothAdapter myBluetooth = null;
    private Set<BluetoothDevice> pairedDevices;
    private AdminSQLiteOpenHelper admin = new AdminSQLiteOpenHelper(this, "administracion", null, 1);
    private AdapterView.OnItemClickListener myListClickListener = new AdapterView.OnItemClickListener() {
        public void onItemClick(AdapterView<?> av, View v, int arg2, long arg3) {
            // Get the device MAC address, the last 17 chars in the View
            String info = ((TextView) v).getText().toString();
            String address = info.substring(info.length() - 17);

            // Make an intent to start next activity.
            Intent serviceIntent = new Intent(Settings.this, BluetoothService.class);
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
        AdminSQLiteOpenHelper admin = new AdminSQLiteOpenHelper(this, "administracion", null, 1);
        SQLiteDatabase bd = admin.getWritableDatabase();
        Cursor fila = bd.rawQuery("select * from Mesure", null);
        if (fila.moveToFirst()) {
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
            Toast.makeText(this, "File not writable", Toast.LENGTH_SHORT).show();
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
            while ((line = stream.readLine()) != null) {
                String[] splitted = line.split(",");
                if (admin.introduireDesMesures(Long.parseLong(splitted[0]), splitted[1], Integer.parseInt(splitted[3]), Double.parseDouble(splitted[4]), Double.parseDouble(splitted[5]), Integer.parseInt(splitted[6]), Integer.parseInt(splitted[7]), splitted[8], splitted[9])) {
                    Toast.makeText(this, "Success", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Fail", Toast.LENGTH_SHORT).show();
                }
            }
        } catch (IOException e) {
            Toast.makeText(this, "File not found", Toast.LENGTH_SHORT).show();
        }
    }

}