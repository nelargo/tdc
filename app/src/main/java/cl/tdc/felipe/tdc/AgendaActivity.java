package cl.tdc.felipe.tdc;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import cl.tdc.felipe.tdc.adapters.AdapterListAgenda;
import cl.tdc.felipe.tdc.adapters.ItemListAgenda;

/**
 * Created by Felipe on 13/02/2015.
 */
public class AgendaActivity extends Activity{
    public static Activity actividad;
    public ListView lv;
    public ImageButton add, calendar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agenda);
        actividad = this;

        init();
    }

    public void init(){
        add = (ImageButton)findViewById(R.id.agenda_add);

        lv = (ListView) findViewById(R.id.agenda_listado);
        AdapterListAgenda adapter = new AdapterListAgenda(this,listadummy());
        lv.setAdapter(adapter);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(
                        getApplicationContext(),
                        ((TextView)view.findViewById(R.id.itemlistagenda_where)).getText(),
                        Toast.LENGTH_SHORT
                ).show();
            }
        });
    }

    // TODO: funcion onClick del botón apagar.

    public void onClick_new(View v){
        new AlertDialog.Builder(this)
                .setTitle("Agregar Nueva Entrada")
                .setPositiveButton("Crear", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                }).create().show();
    }

    public void onClick_apagar(View v){
        MainActivity.actividad.finish();
        finish();
    }

    public void onClick_back(View v){
        finish();
    }

    public ArrayList<ItemListAgenda> listadummy(){
        ArrayList<ItemListAgenda> items = new ArrayList<ItemListAgenda>();
        ItemListAgenda dummy;

        for(int i=1; i<10; i++){
            String hora = ""+(i+6)+":00";
            String dir = "Calle Dummy "+i+" #60"+i+" Comuna "+i+", Ciudad "+i;
            String que = "Faena Tipo "+(i%4);
            dummy = new ItemListAgenda(i%2,hora,dir,que);
            items.add(dummy);
            dummy = null;
        }

        return items;
    }
}
