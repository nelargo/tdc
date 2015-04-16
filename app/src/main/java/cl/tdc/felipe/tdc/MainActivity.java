package cl.tdc.felipe.tdc;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import cl.tdc.felipe.tdc.daemon.DemonioTDC;
import cl.tdc.felipe.tdc.daemon.MyPhoneStateListener;
import cl.tdc.felipe.tdc.preferences.PreferencesTDC;


public class MainActivity extends ActionBarActivity {
    private static final String TAG = "MAINACTIVITY";
    public static Activity actividad;
    public static Intent service;

    ImageButton btn_repair,btn_agenda,btn_averia,btn_cerca;
    LinearLayout btn1,btn2,btn3,btn4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.i(TAG,"MainActyvity Start");
        actividad = this;
        service = new Intent(this,DemonioTDC.class);
        startService(service);

        btn_repair = (ImageButton)findViewById(R.id.btn_repair);
        int btn_width = btn_repair.getLayoutParams().width;
        Log.i(TAG,"ancho boton "+btn_width + " " + btn_repair.getWidth());

        /*btn_agenda = (ImageButton)findViewById(R.id.btn_agenda);
        btn_averia = (ImageButton)findViewById(R.id.btn_averia);
        btn_cerca = (ImageButton)findViewById(R.id.btn_cerca);


        int btn_width = btn_repair.getLayoutParams().width;

        btn_repair.setLayoutParams(new LinearLayout.LayoutParams(btn_width,btn_width));
        btn_agenda.setLayoutParams(new LinearLayout.LayoutParams(btn_width,btn_width));
        btn_averia.setLayoutParams(new LinearLayout.LayoutParams(btn_width,btn_width));
        btn_cerca.setLayoutParams(new LinearLayout.LayoutParams(btn_width,btn_width));
*/
        settings();

    }

    public void onClick_apagar(View v){
        finish();
    }

    public void onClick_QR(View v) {

        IntentIntegrator integrator = new IntentIntegrator(this);
        integrator.initiateScan();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        IntentResult scanResult = IntentIntegrator.parseActivityResult(requestCode,resultCode,data);
        if(scanResult != null){
            new AlertDialog.Builder(this)
                    .setTitle("Resultado Scaner")
                    .setMessage("Mensaje: "+scanResult.getContents()+"\nTipo: "+scanResult.getFormatName())
                    .setNeutralButton("Cerrar",new DialogInterface.OnClickListener(){

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    }).show();
        }else
            Toast.makeText(this,"Error en la lectura.", Toast.LENGTH_SHORT).show();
    }

    // MANTENIMIENTO
    public void onClick_btn1(View v){
        startActivity(new Intent(this,MantencionActivity.class));
    }

    // AGENDA.
    public void onClick_btn2(View v){
        startActivity(new Intent(this,AgendaActivity.class));
    }

    // NOTIFICAR AVERIA
    public void onClick_btn3(View v){
        startActivity(new Intent(this,AveriaActivity.class));
    }

    // SITIOS CERCANOS
    public void onClick_btn4(View v){
        //Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.google.com/maps"));
        //startActivity(browserIntent);
        startActivity(new Intent(this,CercanosActivity.class));
    }


    void settings(){
        TelephonyManager fono = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
        PreferencesTDC preferencesTDC = new PreferencesTDC(this);
        if(!preferencesTDC.sharedPreferences.contains(PreferencesTDC.SETTING_IMEI))
            preferencesTDC.setIMEI(fono.getDeviceId());
        if(!preferencesTDC.sharedPreferences.contains(PreferencesTDC.SETTING_IMSI))
            preferencesTDC.setIMSI(fono.getSimSerialNumber());
    }
   /* @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }*/
}
