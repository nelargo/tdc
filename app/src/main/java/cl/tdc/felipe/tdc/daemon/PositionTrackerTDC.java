package cl.tdc.felipe.tdc.daemon;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Binder;
import android.os.Environment;
import android.os.IBinder;
import android.telephony.TelephonyManager;
import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import cl.tdc.felipe.tdc.MainActivity;
import cl.tdc.felipe.tdc.webservice.SoapRequest;

public class PositionTrackerTDC extends Service {
    private static final String TAG = "PositionTrackerTDC";
    private static final long MIN_PERIOD = 1000 * 60 * 5;
    private static final long MIN_DELAY = 1000 * 20;
    private static final String DIRECTORYNAME = "/TDC@";
    private static final String FILENAME = "pos_pendent.txt";
    public String LATITUDE;
    public String LONGITUDE;

    Timer mTimer;
    public MyLocationListener gps;
    private final IBinder mBinder = new MyBinder();

    @Override
    public void onCreate() {
        super.onCreate();
        gps = new MyLocationListener(this);

        /**
         * Timer: Cada MIN_PERIOD segundos revisa las redes wifi que capta.
         * Si el usuario esta conectado a internet wifi, la informacion de cada red identificada
         *  es enviada a la torre de control.
         * Si no, la informacion se guardara en /TDC@/wifi_pendent.txt en
         * la tarjeta SD.
         * Luego, si el usuario esta conectado a internet wifi, se revisa si el archivo
         * /TDC@/wifi_pendent.txt existe en la tarjeta SD.
         * Si existe, se envía la información almacenada a la torre de control.
         */
        this.mTimer = new Timer();
        this.mTimer.scheduleAtFixedRate(
                new TimerTask() {
                    @Override
                    public void run() {
                        try {

                            ConnectivityManager conMan = (ConnectivityManager) getSystemService(MainActivity.CONNECTIVITY_SERVICE);
                            NetworkInfo.State wifiState = conMan.getNetworkInfo(1).getState();
                            NetworkInfo.State ntwrkState = conMan.getNetworkInfo(0).getState();


                            /**
                             * SI ENCUENTRA CONEXION ENVIA LA POSICION A TDC
                             * SI NO LO GUARDA EN EL ARCHIVO
                             */

                            String latitude = String.valueOf(gps.getLatitude());
                            String longitude = String.valueOf(gps.getLongitude());
                            if(latitude.compareTo("0")!= 0 && longitude.compareTo("0")!=0) {
                                if (wifiState == NetworkInfo.State.CONNECTED || ntwrkState == NetworkInfo.State.CONNECTED) {
                                    /** ENVIAMOS LA INFO**/
                                    try {
                                        Log.i(TAG, "Enviando... LAT=" + latitude + " LON= " + longitude);
                                        LONGITUDE = longitude;
                                        LATITUDE = latitude;
                                        TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
                                        String query = SoapRequest.sendPosition(longitude, latitude, "", telephonyManager.getDeviceId());
                                        Log.i(TAG, "ENVIADO\n" + query);
                                    } catch (Exception e) {
                                        Log.e("POSITION", e.getMessage() + ": " + e.getCause());
                                    }

                                    /** REVISAMOS SI HAY DATOS PENDIENTES
                                     * Y TRATAMOS DE ENVIAR SI HAY CONEXION                             *
                                     **/

                                    if (Environment.getExternalStorageState().equals("mounted")) {
                                        File sdCard = Environment.getExternalStorageDirectory();
                                        File directory = new File(sdCard.getAbsolutePath()
                                                + DIRECTORYNAME);
                                        File file = new File(directory, FILENAME);
                                        if (file.exists() && (wifiState == NetworkInfo.State.CONNECTED || ntwrkState == NetworkInfo.State.CONNECTED)) {
                                            BufferedReader buffer = new BufferedReader(new FileReader(file));
                                            String line;
                                            buffer.readLine(); //leemos la linea en blanco

                                            while ((line = buffer.readLine()) != null) {
                                                try {
                                                    Log.i(TAG, "PENDIENTE: " + line);
                                                    String[] pendents = line.split(";");
                                                    TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
                                                    String query = SoapRequest.sendPosition(pendents[0], pendents[1], pendents[2], telephonyManager.getDeviceId());

                                                    Log.i(TAG, "ENVIADO\n" + query);
                                                } catch (Exception e) {
                                                    Log.e("POSITION", e.getMessage() + ": " + e.getCause());
                                                }
                                            }
                                            buffer.close();
                                            if (file.delete()) {
                                                Log.i(TAG, FILENAME + " BORRADO");
                                            }
                                        }
                                    }

                                } else {
                                    if (Environment.getExternalStorageState().equals("mounted")) {
                                        File sdCard = Environment.getExternalStorageDirectory();
                                        File directory = new File(sdCard.getAbsolutePath()
                                                + DIRECTORYNAME);
                                        if (!directory.exists())
                                            if (directory.mkdir())
                                                Log.i(TAG, "Directory \"" + DIRECTORYNAME + "\" created");
                                        /** GUARDAMOS LA INFO **/
                                        DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                                        Date fecha = new Date();
                                        String line = "\n" + longitude + ";" +
                                                latitude + ";" +
                                                formatter.format(fecha).toString();

                                        File file = new File(directory, FILENAME);
                                        FileWriter fw = new FileWriter(file, true);
                                        BufferedWriter out = new BufferedWriter(fw);
                                        Log.i(TAG, "AGREGADO A PENDIENES: " + line);
                                        out.write(line);
                                        out.flush();
                                        out.close();
                                    }
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
                , MIN_DELAY, MIN_PERIOD);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    public class MyBinder extends Binder {
        public PositionTrackerTDC getService() {
            return PositionTrackerTDC.this;
        }
    }
}
