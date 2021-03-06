package davidfdez.capteuratmospherique;

import android.app.ProgressDialog;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.IBinder;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

public class BluetoothService extends Service {
    private static final UUID myUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    private String user = null;
    private Handler handler;
    private ProgressDialog progress;
    private String address = null;
    private BluetoothAdapter myBluetooth = null;
    private BluetoothSocket btSocket = null;
    private boolean isBtConnected = false;
    private BufferedReader reader;
    private AdminSQLiteOpenHelper admin = new AdminSQLiteOpenHelper(this);

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        address = intent.getStringExtra(SettingsActivity.EXTRA_ADDRESS); //receive the address of the bluetooth device
        user = intent.getStringExtra("user");
        new ConnectBT().execute();
        handler = new Handler();

        final Runnable r = new Runnable() {
            public void run() {
                if (isBtConnected) {
                    try {
                        InputStream in = btSocket.getInputStream();
                        reader = new BufferedReader(new InputStreamReader(in));
                        String line;
                        line = reader.readLine();
                        if (line != null) {
                            if (!line.equals("Heating")) {
                                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                                Date date = new Date();
                                long dateLong = date.getTime();
                                String[] splitted = line.split(",");
                                if(splitted.length>8) {
                                    double CO2 = Integer.parseInt(splitted[0]);
                                    double humidity = Double.parseDouble(splitted[1]);
                                    double temperature = Double.parseDouble(splitted[2]);
                                    double luminosity = Double.parseDouble(splitted[3]);
                                    double color = Double.parseDouble(splitted[4]);
                                    String latitude = (splitted[6].equals("N") ? "" : "-") + splitted[5];
                                    String longitude = (splitted[8].equals("E") ? "" : "-") + splitted[7];
                                    admin.setMeasures(dateLong, user, CO2, humidity, temperature, luminosity, color, latitude, longitude);
                                }
                            } else {
                                Toast.makeText(getApplicationContext(), line, Toast.LENGTH_SHORT).show();
                                Disconnect();
                                handler.removeCallbacks(this);
                            }
                        }
                    } catch (IOException | NumberFormatException | ArrayIndexOutOfBoundsException e) {
                        Toast.makeText(getApplicationContext(), "Error", Toast.LENGTH_SHORT).show();
                    }
                }
                handler.postDelayed(this, 5000);
            }
        };
        handler.postDelayed(r, 5000);
        return Service.START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        //Actually this should be a correct IBinder implementation
        return null;
    }

    private void Disconnect() {
        if (btSocket != null) { //If the btSocket is busy
            try {
                btSocket.close(); //close connection
                isBtConnected = false;
            } catch (IOException e) {
                Toast.makeText(getApplicationContext(), "Error", Toast.LENGTH_SHORT).show();
            }
        }

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Disconnect();
    }

    private class ConnectBT extends AsyncTask<Void, Void, Void> {
        private boolean ConnectSuccess = true; //if it's here, it's almost connected

        @Override
        protected Void doInBackground(Void... devices) { //while the progress dialog is shown, the connection is done in background
            try {
                if (btSocket == null || !isBtConnected) {
                    myBluetooth = BluetoothAdapter.getDefaultAdapter();//get the mobile bluetooth device
                    BluetoothDevice dispositivo = myBluetooth.getRemoteDevice(address);//connects to the device's address and checks if it's available
                    btSocket = dispositivo.createInsecureRfcommSocketToServiceRecord(myUUID);//create a RFCOMM (SPP) connection
                    BluetoothAdapter.getDefaultAdapter().cancelDiscovery();
                    btSocket.connect();//start connection
                }
            } catch (IOException e) {
                ConnectSuccess = false;//if the try failed, you can check the exception here
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) { //after the doInBackground, it checks if everything went fine
            super.onPostExecute(result);

            if (!ConnectSuccess)
                Toast.makeText(getApplicationContext(), "Error", Toast.LENGTH_SHORT).show();
            else {
                Toast.makeText(getApplicationContext(), "Connected", Toast.LENGTH_SHORT).show();
                isBtConnected = true;
            }
        }

    }
}