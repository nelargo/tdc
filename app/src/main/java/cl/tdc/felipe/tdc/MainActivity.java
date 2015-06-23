package cl.tdc.felipe.tdc;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.github.gcacace.signaturepad.views.SignaturePad;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.util.ArrayList;

import cl.tdc.felipe.tdc.daemon.MyPhoneStateListener;
import cl.tdc.felipe.tdc.daemon.PositionTrackerTDC;
import cl.tdc.felipe.tdc.daemon.WifiTrackerTDC;
import cl.tdc.felipe.tdc.preferences.PreferencesTDC;
import cl.tdc.felipe.tdc.webservice.SoapRequest;
import cl.tdc.felipe.tdc.webservice.XMLParser;

public class MainActivity extends ActionBarActivity {
    private static final String TAG = "MAINACTIVITY";
    public static Activity actividad;
    public static Intent service_wifi, service_pos;
    public static String IMEI;
    public static PreferencesTDC preferencesTDC;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_horizontal);
        Log.i(TAG, "MainActyvity Start");
        actividad = this;
        preferencesTDC = new PreferencesTDC(this);

        TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        IMEI = telephonyManager.getDeviceId();

        service_wifi = new Intent(this, WifiTrackerTDC.class);
        //startService(service_wifi);
        service_pos = new Intent(this, PositionTrackerTDC.class);
        startService(service_pos);
        settings();

    }

    public void onClick_apagar(View v) {
        finish();
    }

    public void onClick_QR(View v) {
        IntentIntegrator integrator = new IntentIntegrator(this);
        integrator.initiateScan();
    }


    // MANTENIMIENTO
    public void onClick_btn1(View v) {
        startActivity(new Intent(this, MantencionActivity.class));
    }

    // AGENDA.
    public void onClick_btn2(View v) {
        //startActivity(new Intent(this,AgendaActivity.class));
        AgendaTask agendaTask = new AgendaTask(this);
        agendaTask.execute();
    }

    // NOTIFICAR AVERIA
    public void onClick_btn3(View v) {
        startActivity(new Intent(this, AveriaActivity.class));
    }

    // SITIOS CERCANOS
    public void onClick_btn4(View v) {
        try {
            startActivity(new Intent(this, CercanosActivity.class));
        } catch (Exception e) {
            AlertDialog.Builder b = new AlertDialog.Builder(this);
            b.setMessage(e.getMessage() + ":\n" + e.getCause());
            b.setTitle("Error al cargar Sitios Cercanos");
            b.setNeutralButton("Cerrar", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                }
            });
            b.show();
        }
    }


    void settings() {
        TelephonyManager fono = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        PreferencesTDC preferencesTDC = new PreferencesTDC(this);
        if (!preferencesTDC.sharedPreferences.contains(PreferencesTDC.SETTING_IMEI))
            preferencesTDC.setIMEI(fono.getDeviceId());
        if (!preferencesTDC.sharedPreferences.contains(PreferencesTDC.SETTING_IMSI))
            preferencesTDC.setIMSI(fono.getSimSerialNumber());
    }


    //-----------------TASK ASINCRONICO------------------------------------

    private class AgendaTask extends AsyncTask<String, String, ArrayList<String>> {
        ProgressDialog progressDialog;
        Context tContext;
        String ATAG = "MAINTASK";

        public AgendaTask(Context context) {
            this.tContext = context;
        }

        @Override
        protected void onPreExecute() {
            progressDialog = new ProgressDialog(tContext);
            progressDialog.setTitle("Espere por favor...");
            progressDialog.show();
        }


        @Override
        protected void onProgressUpdate(String... values) {
            progressDialog.setMessage(values[0]);
        }

        @Override
        protected ArrayList<String> doInBackground(String... strings) {
            String result;
            try {
                publishProgress("Verificando Jornada...");
                String query = SoapRequest.updateTechnician(IMEI);
                ArrayList<String> response = XMLParser.getReturnCode(query);
                return response;
            } catch (Exception e) {
                Log.e(ATAG, e.getMessage() + ":\n" + e.getCause());
                return null; //Error
            }

        }

        @Override
        protected void onPostExecute(ArrayList<String> s) {
            if (progressDialog.isShowing())
                progressDialog.dismiss();

            if (s == null) {
                Toast.makeText(tContext, "Error en la conexion. Intente nuevamente.", Toast.LENGTH_LONG).show();
            } else {
                if (s.get(0).compareTo("0") == 0) {
                    Intent i = new Intent(tContext, AgendaActivity.class);
                    i.putExtra("RESPONSE", s);
                    startActivity(i);
                } else {
                    Toast.makeText(getApplicationContext(), s.get(1), Toast.LENGTH_LONG).show();
                }

            }
        }
    }


}
