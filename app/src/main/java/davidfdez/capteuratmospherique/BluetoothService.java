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
    private AdminSQLiteOpenHelper admin = new AdminSQLiteOpenHelper(this, "administracion", null, 1);

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        address = intent.getStringExtra(Settings.EXTRA_ADDRESS); //receive the address of the bluetooth device
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
                            // msg(line);
                            if (!line.equals("Heating")) {
                                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                                Date date = new Date();
                                long dateLong = date.getTime();
                                String[] splitted = line.split(",");
                                //TODO: calculate lat and long correctly
                                if (admin.introduireDesMesures(dateLong, user, Integer.parseInt(splitted[0]), Double.parseDouble(splitted[1]), Double.parseDouble(splitted[2]), Integer.parseInt(splitted[3]), Integer.parseInt(splitted[4]), splitted[5], splitted[7])) {
                                    // msg("Succeed");
                                } else {
                                    // msg("Error");
                                }
                            } else {
                                msg(line);
                                Disconnect();
                                handler.removeCallbacks(this);
                            }
                        }
                    } catch (IOException | NumberFormatException | ArrayIndexOutOfBoundsException e) {
                        msg("Error");
                    }
                }
                handler.postDelayed(this, 30000);
            }
        };
        handler.postDelayed(r, 30000);
        return Service.START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        //TODO for communication return IBinder implementation
        return null;
    }

    private void msg(String s) {
        Toast.makeText(getApplicationContext(), s, Toast.LENGTH_SHORT).show();
    }

    private void Disconnect() {
        if (btSocket != null) { //If the btSocket is busy
            try {
                btSocket.close(); //close connection
                isBtConnected = false;
            } catch (IOException e) {
                msg("Error");
            }
        }

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

            if (!ConnectSuccess) {
                Toast.makeText(getApplicationContext(),
                        "Error.",
                        Toast.LENGTH_SHORT).show();

            } else {
                Toast.makeText(getApplicationContext(),
                        "Connected.",
                        Toast.LENGTH_SHORT).show();
                isBtConnected = true;
            }
        }

    }
}