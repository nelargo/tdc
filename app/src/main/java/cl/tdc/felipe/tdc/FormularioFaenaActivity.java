package cl.tdc.felipe.tdc;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Felipe on 13/02/2015.
 */
public class FormularioFaenaActivity extends Activity {
    public static Activity actividad;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_faena_combustible);
        actividad = this;

        init();
    }

    public void init(){


    }

    public void openDateTimePicker(View v){
        final Calendar c = Calendar.getInstance();
        int mYear = c.get(Calendar.YEAR);
        int mMonth = c.get(Calendar.MONTH);
        int mDay = c.get(Calendar.DAY_OF_MONTH);

        c.set(Calendar.MILLISECOND, c.MILLISECOND - 1000);

        final EditText et = (EditText)findViewById(R.id.edtxt_fechafaena);



        DatePickerDialog dpd = new DatePickerDialog(this,
                new DatePickerDialog.OnDateSetListener() {

                    @Override
                    public void onDateSet(DatePicker view, int year,
                                          int monthOfYear, int dayOfMonth) {
                        et.setText(dayOfMonth + "-"
                                + (monthOfYear + 1) + "-" + year);
                    }
                }, mYear, mMonth, mDay);
        dpd.getDatePicker().setCalendarViewShown(true);
        dpd.getDatePicker().setSpinnersShown(false);
        dpd.show();
    }

    // TODO: funcion onClick del botón apagar.

    public void onClick_apagar(View v){
        MainActivity.actividad.finish();
        MantencionActivity.actividad.finish();
        finish();
    }

    public void onClick_back(View v){
        finish();
    }

    public void onClick_enviar(View v){
        Toast.makeText(getApplicationContext(),
                "Formulario Enviado con Éxito",
                Toast.LENGTH_LONG
        ).show();
        finish();
    }

    public ArrayList<String[]> listadummy(){
        ArrayList<String[]> items = new ArrayList<String[]>();
        String[] datos = new String[2];

        datos[0] = "Principal";datos[1]="";
        items.add(datos);

        datos[0] = "15/02/2015";datos[1]="";
        items.add(datos);

        datos[0] = "200";datos[1]="150";
        items.add(datos);


        return items;
    }
}
