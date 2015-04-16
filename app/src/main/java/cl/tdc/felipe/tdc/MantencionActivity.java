package cl.tdc.felipe.tdc;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import cl.tdc.felipe.tdc.R;

/**
 * Created by Felipe on 13/02/2015.
 */
public class MantencionActivity extends Activity {
    public static Activity actividad;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mantenimiento);
        actividad = this;
    }

    // TODO: funcion onClick del botón apagar.

    public void onClick_apagar(View v){
        MainActivity.actividad.finish();
        finish();
    }

    public void onClick_faena(View v){
        startActivity(new Intent(this,FormularioFaenaActivity.class));
    }

    public void onClick_niveles(View v){
        startActivity(new Intent(this,FormularioNivelesActivity.class));
    }

    public void onClick_preventivo(View v){
        startActivity(new Intent(this,FormularioPreventivoActivity.class));
    }

    public void onClick_correctivo(View v){
        startActivity(new Intent(this,FormularioCorrectivoActivity.class));
    }

    public void onClick_back(View v){
        finish();
    }
}
