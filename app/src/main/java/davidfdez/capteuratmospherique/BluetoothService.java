package davidfdez.capteuratmospherique;

import android.app.ProgressDialog;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.UUID;
import java.util.Date ;
import java.text.SimpleDateFormat;

public class BluetoothService extends Service {
    private String user = null;
    private Handler handler;
    private ProgressDialog progress;
    String address = null;
    BluetoothAdapter myBluetooth = null;
    BluetoothSocket btSocket = null;
    private boolean isBtConnected = false;
    static final UUID myUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    private BufferedReader reader;
    private int mesure=0;
    AdminSQLiteOpenHelper admin = new AdminSQLiteOpenHelper(this, "administracion", null, 1);
    @Override
    public void onCreate() {
        super.onCreate();



    }
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
                        if(line!=null) {
                            //Toast.makeText(getApplicationContext(), line, Toast.LENGTH_SHORT).show();
                            if(!line.equals("Heating")){
                                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                                Date date = new Date();
                            String []  splitted = line.split(",");
                            if(admin.introduireDesMesures("" + date ,user ,Integer.parseInt(splitted[0]), Double.parseDouble(splitted[1]), Double.parseDouble(splitted[2]),Integer.parseInt(splitted[3]),Integer.parseInt(splitted[4]), splitted[5]+","+splitted[6], splitted[7]+","+splitted[8])) {
                                    //Toast.makeText(getApplicationContext(), "True" , Toast.LENGTH_SHORT).show();
                                    mesure++;
                                }else{
                                //Toast.makeText(getApplicationContext(), "False" , Toast.LENGTH_SHORT).show();
                                 }
                            }
                            //
                            //admin.introduireDesMesures("a","a");

                        }


                    } catch (IOException e){
                        msg("Error");
                    }
                }

                handler.postDelayed(this, 1000);

            }
        };

        handler.postDelayed(r, 1000);
        return Service.START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        //TODO for communication return IBinder implementation
        return null;
    }
    private void msg(String s)
    {
        Toast.makeText(getApplicationContext(),s,Toast.LENGTH_LONG).show();
    }
    private class ConnectBT extends AsyncTask<Void, Void, Void>  // UI thread
    {
        private boolean ConnectSuccess = true; //if it's here, it's almost connected

        @Override
        protected void onPreExecute()
        {
            //progress = ProgressDialog.show(BluetoothService.this, "Connecting...", "Please wait!!!");  //show a progress dialog
        }

        @Override
        protected Void doInBackground(Void... devices) //while the progress dialog is shown, the connection is done in background
        {
            try
            {
                if (btSocket == null || !isBtConnected)
                {
                    myBluetooth = BluetoothAdapter.getDefaultAdapter();//get the mobile bluetooth device
                    BluetoothDevice dispositivo = myBluetooth.getRemoteDevice(address);//connects to the device's address and checks if it's available
                    btSocket = dispositivo.createInsecureRfcommSocketToServiceRecord(myUUID);//create a RFCOMM (SPP) connection
                    BluetoothAdapter.getDefaultAdapter().cancelDiscovery();
                    btSocket.connect();//start connection
                }
            }
            catch (IOException e)
            {
                ConnectSuccess = false;//if the try failed, you can check the exception here
            }
            return null;
        }
        @Override
        protected void onPostExecute(Void result) //after the doInBackground, it checks if everything went fine
        {
            super.onPostExecute(result);

            if (!ConnectSuccess)
            {
                Toast.makeText(getApplicationContext(),
                        "Error.",
                        Toast.LENGTH_SHORT).show();

            }
            else
            {
                Toast.makeText(getApplicationContext(),
                        "Connected.",
                        Toast.LENGTH_SHORT).show();
                isBtConnected = true;
            }
            //progress.dismiss();
        }

    }
}