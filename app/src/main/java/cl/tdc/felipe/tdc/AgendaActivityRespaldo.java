package cl.tdc.felipe.tdc;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;

import java.util.ArrayList;
import java.util.List;

import cl.tdc.felipe.tdc.adapters.Actividad;
import cl.tdc.felipe.tdc.adapters.ActivitiesAdapter;
import cl.tdc.felipe.tdc.webservice.SoapRequest;
import cl.tdc.felipe.tdc.webservice.XMLParser;

public class AgendaActivityRespaldo extends Activity implements ActivitiesAdapter.ClickListener {
    public static Activity actividad;
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private String IMEI;
    private String ListResponse;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.agenda_view);
        actividad = this;
        ListResponse = getIntent().getStringExtra("RESPONSE");
        //init();
        init_ImageLoader();
    }

    private void init() {
        //mRecyclerView = (RecyclerView) this.findViewById(R.id.recyclerActivities);
        mLayoutManager = new LinearLayoutManager(this);

        TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        IMEI = telephonyManager.getDeviceId();

        /*try {
            List<Actividad> actividades = StringToListActivities(XMLParser.getListadoActividades(ListResponse));
            init_RecyclerView(actividades);
        } catch (Exception e) {
            Log.e("AGENDAACTIVITY", e.getMessage() + ":\n" + e.getCause());
        }*/

    }

    private List<Actividad> StringToListActivities(ArrayList<String> todo) {
        List<Actividad> actividades = new ArrayList<>();
        for (String a : todo) {
            String[] datos = a.split("~");
            actividades.add(new Actividad(Integer.parseInt(datos[0]), datos[1], datos[2], datos[3], datos[4], datos[5], datos[6], datos[7], datos[8], datos[9], datos[10], datos[11], Integer.parseInt(datos[12])));
        }
        return actividades;
    }

    public void init_RecyclerView(List<Actividad> a) {
        List<Actividad> actividades = new ArrayList<>();
        for(Actividad p: a){
            if(p.getStatus().compareTo("ASSIGNED")==0){
                actividades.add(0,p);
            }
            else
                actividades.add(p);
        }

        mAdapter = new ActivitiesAdapter(this, this, actividades);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setLayoutManager(mLayoutManager);
    }

    private void init_ImageLoader() {
        DisplayImageOptions options = new DisplayImageOptions.Builder()
                .resetViewBeforeLoading(false)  // default
                .delayBeforeLoading(1000)
                .cacheOnDisk(true) // default
                .build();

        ImageLoaderConfiguration.Builder config = new ImageLoaderConfiguration.Builder(this);
        config.threadPriority(Thread.NORM_PRIORITY - 2);
        config.defaultDisplayImageOptions(options);
        config.denyCacheImageMultipleSizesInMemory();
        config.diskCacheFileNameGenerator(new Md5FileNameGenerator());
        config.diskCacheSize(50 * 1024 * 1024); // 50 MiB
        config.tasksProcessingOrder(QueueProcessingType.LIFO);
        config.writeDebugLogs(); // Remove for release app

        // Initialize ImageLoader with configuration.
        ImageLoader.getInstance().init(config.build());
    }

    // TODO: funcion onClick del botón apagar.

    public void onClick_apagar(View v) {
        MainActivity.actividad.finish();
        finish();
    }

    public void onClick_back(View v) {
        finish();
    }


    @Override
    public void itemClicked(View view, int type, final Actividad actividad) {
        if (type == 0) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("¿Desea finalizar la actividad?");
            builder.setTitle("Termino de Actividad");
            builder.setPositiveButton("Continuar", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    CompletarActividad fin = new CompletarActividad(AgendaActivityRespaldo.this, actividad);
                    fin.execute();
                }
            });
            builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                }
            });
            builder.show();


        }
    }


    private class CompletarActividad extends AsyncTask<String, String, ArrayList<String>> {
        private String CTAG = "COMPLETARACTIVIDAD";
        Context mContext;
        ProgressDialog progressDialog;
        Actividad actividad;

        public CompletarActividad(Context c, Actividad actividad) {
            this.mContext = c;
            this.actividad = actividad;
            progressDialog = new ProgressDialog(mContext);
        }

        @Override
        protected void onProgressUpdate(String... values) {
            progressDialog.setMessage(values[0]);
        }

        @Override
        protected void onPreExecute() {
            progressDialog.show();
        }

        @Override
        protected ArrayList<String> doInBackground(String... strings) {
            try {
                publishProgress("Abriendo formulario...");
                String query = SoapRequest.typeActivity(IMEI, this.actividad.getId());
                Log.d(CTAG, "TYPE\n" + query);
                ArrayList<String> parse = XMLParser.getTypeActivity(query);
                Log.d(CTAG, "TYPE\n" + parse.toString());

                return parse;
            } catch (Exception e) {
                Log.e(CTAG, e.getMessage() + ":\n" + e.getCause());
                return null;
            }
        }

        @Override
        protected void onPostExecute(ArrayList<String> s) {
            if (s != null) {
                if(s.get(0).compareTo("0")== 0) {
                    Intent i = new Intent(mContext, FormularioPreventivoActivity.class);
                    i.putExtra("ACTIVIDAD", actividad.toString());
                    i.putStringArrayListExtra("RESPUESTA", s);
                    startActivityForResult(i, 0);
                }
                else{
                    Toast.makeText(mContext, s.get(1), Toast.LENGTH_LONG).show();
                }
            }

            if (progressDialog.isShowing())
                progressDialog.dismiss();
        }
    }


    private class AgendaTask extends AsyncTask<String, String, String> {
        ProgressDialog progressDialog;
        Context tContext;
        String ATAG = "MAINTASK";
        boolean jornadaOK = false;
        boolean listOK = false;

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
        protected String doInBackground(String... strings) {
            String result;
            try {
                publishProgress("Verificando Jornada...");
                String query = SoapRequest.updateTechnician(IMEI);
                ArrayList<String> response = XMLParser.getReturnCode(query);
                if (response.get(0).compareTo("0") == 0) {
                    publishProgress("OK");
                    jornadaOK = true;
                } else return query; //No se inicio jornada

                publishProgress("Listando Actividades...");
                result = SoapRequest.getInformation(IMEI);
                //result = dummy.getInformation;
                Log.d("ACTIVIDADES", result);
                return result; //OK
            } catch (Exception e) {
                Log.e(ATAG, e.getMessage() + ":\n" + e.getCause());
                return null; //Error
            }

        }

        @Override
        protected void onPostExecute(String s) {
            if (progressDialog.isShowing())
                progressDialog.dismiss();

            if (s == null) {
                Toast.makeText(tContext, "Error en la conexion. Intente nuevamente.", Toast.LENGTH_LONG).show();
                return;
            }

            if(!jornadaOK){
                try {
                    ArrayList<String> response = XMLParser.getReturnCode(s);
                    Toast.makeText(getApplicationContext(), response.get(1),Toast.LENGTH_LONG).show();
                }catch(Exception e){}
                return;
            }

            if (s.length() > 1) {
                try {
                    ArrayList<String> res = XMLParser.getReturnCode(s);
                    Log.d("ACTIVIDADES", res.toString());

                    if (res.get(0).compareTo("0") == 0 && res.size() >2) {
                        Intent i = new Intent(tContext, AgendaActivityRespaldo.class);
                        i.putExtra("RESPONSE", s);
                        startActivity(i);
                    }else if(res.size() == 2){
                        Toast.makeText(getApplicationContext(), "No se encontraron actividades", Toast.LENGTH_LONG).show();
                    }
                } catch (Exception e) {
                    Log.e(ATAG, e.getMessage() + ":\n" + e.getCause());
                }
            }


        }


    }




    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 0) {
            if (resultCode == 0) {
                //OK
            } else {
                //NOK
            }

        }
    }
}
