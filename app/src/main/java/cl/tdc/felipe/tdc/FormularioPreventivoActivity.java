package cl.tdc.felipe.tdc;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.util.ArrayList;
import java.util.List;

import cl.tdc.felipe.tdc.adapters.Actividad;
import cl.tdc.felipe.tdc.adapters.ActivitiesAdapter;
import cl.tdc.felipe.tdc.adapters.Componente;
import cl.tdc.felipe.tdc.adapters.ComponenteCantidad;
import cl.tdc.felipe.tdc.adapters.ComponentsAdapter;
import cl.tdc.felipe.tdc.webservice.SoapRequest;
import cl.tdc.felipe.tdc.webservice.XMLParser;

public class FormularioPreventivoActivity extends Activity {
    public static Activity mActividad;
    private Context context;
    public static String TAG = "PREVENTIVO";
    RecyclerView mRecyclerView;
    LinearLayoutManager mLayoutManager;
    ComponentsAdapter adapter;
    TextView mensajeVacio;

    int actividad;
    ArrayList<String> Response;
    ArrayList<Componente> componentes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mantenimiento_preventivo);
        mActividad = this;
        context = this;
        actividad = getIntent().getIntExtra("ACTIVIDAD", -1);
        Response = getIntent().getExtras().getStringArrayList("RESPUESTA");

        init();
        init_componentes();
    }

    private void init_componentes() {
        componentes = new ArrayList<>();
        String[] c = Response.get(3).split("&");
        String[] header = c[0].split(";");
        String[] detail = c[1].split(";");

        String bodega = header[0];
        String bodegaId = header[1];
        //idComp, nombre, tipo, codigo, idBod, bodega
        for (int i = 0; i < detail.length; i++) {
            String[] d = detail[i].substring(1, detail[i].length() - 1).replace("]", "").replace("[", ";").split(";");
            componentes.add(new Componente(Integer.parseInt(d[0]), d[2], d[1], d[3], Integer.parseInt(bodegaId), bodega));
        }
    }

    public void init() {
        mensajeVacio = (TextView) findViewById(R.id.texto_informacion);
        mRecyclerView = (RecyclerView) this.findViewById(R.id.lista_componentes);
        mLayoutManager = new LinearLayoutManager(this);
        adapter = new ComponentsAdapter(this);
        mRecyclerView.setAdapter(adapter);
        mRecyclerView.setLayoutManager(mLayoutManager);

    }

    public void onClick_apagar(View v) {
        MainActivity.actividad.finish();
        AgendaActivity.actividad.finish();
        finish();
    }

    public void agregar(View v) {
        CharSequence[] opciones = {"Leer Codigo","Desde la Lista"};
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setItems(opciones, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (i == 0) {
                    IntentIntegrator integrator = new IntentIntegrator(mActividad);
                    integrator.initiateScan();
                }
                if (i == 1) {
                    mostrarLista();
                }
                dialogInterface.dismiss();
            }
        });
        dialog.setNegativeButton("Volver", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        dialog.show();
    }

    private void mostrarLista(){
        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
        View vista = LayoutInflater.from(context).inflate(R.layout.new_component_view, null);
        final Spinner listado = (Spinner) vista.findViewById(R.id.list_components);
        final EditText cantidad = (EditText) vista.findViewById(R.id.cantidad);
        List<String> listC = new ArrayList<>();
        for (Componente c : componentes) {
            listC.add(c.getNombre());
        }

        ArrayAdapter<String> ad = new ArrayAdapter<>(context, android.R.layout.simple_spinner_dropdown_item, listC);
        listado.setAdapter(ad);

        builder.setPositiveButton("Agregar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                int position = listado.getSelectedItemPosition();
                if (cantidad.getText() != null) {
                    adapter.addItem(new ComponenteCantidad(componentes.get(position), Integer.parseInt(cantidad.getText().toString())));
                    mensajeVacio.setVisibility(View.GONE);
                    dialogInterface.dismiss();
                } else
                    Toast.makeText(getApplicationContext(), "Ingrese la cantidad utilizada", Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });

        builder.setView(vista);
        builder.show();
    }

    private void mostrarSeleccion(final Componente seleccion){
        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
        View vista = LayoutInflater.from(context).inflate(R.layout.new_componentcode_view, null);
        final EditText listado = (EditText) vista.findViewById(R.id.componente);
        final EditText cantidad = (EditText) vista.findViewById(R.id.cantidad);
        listado.setText(seleccion.getNombre());
        builder.setPositiveButton("Agregar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (cantidad.getText() != null) {
                    adapter.addItem(new ComponenteCantidad(seleccion, Integer.parseInt(cantidad.getText().toString())));
                    mensajeVacio.setVisibility(View.GONE);
                    dialogInterface.dismiss();
                } else
                    Toast.makeText(getApplicationContext(), "Ingrese la cantidad utilizada", Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });

        builder.setView(vista);
        builder.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null) {
            IntentResult scanResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
            if (scanResult != null) {
                Componente seleccion = null;
                for (Componente c : componentes) {
                    if (c.getCodigo().compareTo(scanResult.getContents()) == 0) {
                        seleccion = c;
                        break;
                    }
                }
                if (seleccion != null) {
                    mostrarSeleccion(seleccion);
                } else {
                    CharSequence[] opt = {"Reintentar", "Seleccionar de Listado"};
                    new AlertDialog.Builder(this)
                            .setTitle("No se encontro el codigo")
                            .setItems(opt, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    if (i == 0) {
                                        IntentIntegrator integrator = new IntentIntegrator(mActividad);
                                        integrator.initiateScan();
                                    }
                                    if (i == 1) {
                                        mostrarLista();
                                    }
                                    dialogInterface.dismiss();
                                }
                            })
                            .setNeutralButton("Volver", new DialogInterface.OnClickListener() {

                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            }).show();
                }
            } else
                Toast.makeText(this, "Error en la lectura.", Toast.LENGTH_SHORT).show();
        }
    }
    public void onClick_back(View v) {
        finish();
    }

    public void onClick_enviar(View v) {
        AlertDialog.Builder b = new AlertDialog.Builder(this);

        View c = LayoutInflater.from(this).inflate(R.layout.comentary_view, null);
        final EditText com = (EditText)c.findViewById(R.id.observacion);
        b.setPositiveButton("Enviar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                if(com.getText()!= null){
                    EnviarFormulario n = new EnviarFormulario(getApplicationContext(),  adapter.getAll(), com.getText().toString());
                    n.execute();
                    dialogInterface.dismiss();
                }
                else
                    Toast.makeText(getApplicationContext(),"Ingrese la observacion", Toast.LENGTH_SHORT).show();
            }
        });

        b.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        b.setTitle("Observaciones");
        b.setView(c);
        b.show();

    }

    class EnviarFormulario extends AsyncTask<String,String,ArrayList<String>>{
        Context mContext;
        List<ComponenteCantidad> c;
        String o;
        ProgressDialog p;

        public EnviarFormulario(Context context, List<ComponenteCantidad> componenteCantidads, String observacion) {
            mContext = context;
            c = componenteCantidads;
            o = observacion;
            p = new ProgressDialog(mContext);
        }

        @Override
        protected void onProgressUpdate(String... values) {
            p.setMessage(values[0]);
        }

        @Override
        protected void onPreExecute() {
            //p.show();
        }

        @Override
        protected ArrayList<String> doInBackground(String... strings) {
            try{
                publishProgress("Enviando Formulario");
                Log.d("ENVIO",""+actividad);
                String query = SoapRequest.sendForm1("", c, actividad, o);
                Log.d("ENVIO", "FORM "+query);
                ArrayList<String> parse = XMLParser.getReturnCode1(query);
                Log.d("ENVIO", "FORM "+parse.toString());

                if(parse.get(0).compareTo("0") == 0) {
                    publishProgress("Finalizando actividad");
                    query = SoapRequest.updateActivity(""+actividad);
                    Log.d("ENVIO", "UPDATE " + query);
                    parse = XMLParser.getReturnCode(query);
                }
                return parse;
            }catch (Exception e){
                return null;
            }

        }

        @Override
        protected void onPostExecute(ArrayList<String> strings) {
            if(strings == null){
                Toast.makeText(mContext, "Ha ocurrido un error, por favor reinente.", Toast.LENGTH_LONG).show();
            }else{
                if(strings.get(0).compareTo("0")==0){
                    setResult(0);
                    finish();
                }
                else{
                    Toast.makeText(mContext, strings.get(1), Toast.LENGTH_LONG).show();
                }
            }

            if(p.isShowing())p.dismiss();
        }
    }

}
