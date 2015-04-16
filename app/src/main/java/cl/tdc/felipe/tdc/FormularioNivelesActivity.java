package cl.tdc.felipe.tdc;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * Created by Felipe on 13/02/2015.
 */
public class FormularioNivelesActivity extends Activity {
    public static Activity actividad;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_niveles_combustible);
        actividad = this;

        init();
    }

    public void init(){


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
