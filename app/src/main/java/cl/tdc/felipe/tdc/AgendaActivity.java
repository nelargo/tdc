package cl.tdc.felipe.tdc;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;

import java.util.ArrayList;

import cl.tdc.felipe.tdc.adapters.Actividad;
import cl.tdc.felipe.tdc.adapters.Actividades;
import cl.tdc.felipe.tdc.adapters.Maintenance;
import cl.tdc.felipe.tdc.preferences.PreferencesTDC;
import cl.tdc.felipe.tdc.webservice.SoapRequest;
import cl.tdc.felipe.tdc.webservice.XMLParser;
import cl.tdc.felipe.tdc.webservice.dummy;

public class AgendaActivity extends Activity {
    private static int REQUEST_FORMULARIO = 0;
    private static int RESULT_OK = 0;
    private static int RESULT_NOK = 1;
    private static int RESULT_CANCELED = 1;

    public static Activity actividad;
    private String IMEI;
    private ViewPager mPager;
    private PagerAdapter mPagerAdapter;
    ArrayList<Maintenance> m;
    PreferencesTDC pref;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agenda_new);
        actividad = this;
        pref = new PreferencesTDC(this);
        init();
        init_ImageLoader();
    }

    private void init() {
        TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        IMEI = telephonyManager.getDeviceId();

        mPager = (ViewPager) findViewById(R.id.agenda_contentPager);
        mPager.setPageTransformer(true, new ZoomOutPageTransformer());
        AgendaTask agenda = new AgendaTask(this);
        agenda.execute();
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


    private class CompletarActividad extends AsyncTask<String, String, ArrayList<String>> {
        private String CTAG = "COMPLETARACTIVIDAD";
        Context mContext;
        ProgressDialog progressDialog;
        int idMaintenance;

        public CompletarActividad(Context c, int ID) {
            this.mContext = c;
            this.idMaintenance = ID;
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
                /*publishProgress("Abriendo formulario...");
                String query = SoapRequest.typeActivity(IMEI, idMaintenance);
                Log.d(CTAG, "TYPE\n" + query);
                ArrayList<String> parse = XMLParser.getTypeActivity(query);
                Log.d(CTAG, "TYPE\n" + parse.toString());
                */
                publishProgress("Cierre de Actividad...");
                String query = SoapRequest.FormClosing(IMEI, idMaintenance);
                ArrayList<String> parse = XMLParser.getReturnCode1(query);

                return parse;
            } catch (Exception e) {
                Log.e(CTAG, e.getMessage() + ":\n" + e.getCause());
                return null;
            }
        }

        @Override
        protected void onPostExecute(ArrayList<String> s) {
            if (s != null) {
                if (s.get(0).compareTo("0") == 0) {
                    Toast.makeText(mContext, s.get(1),Toast.LENGTH_LONG).show();
                    AgendaActivity.actividad.finish();
                    /*Intent i = new Intent(mContext, FormularioPreventivoActivity.class);
                    i.putExtra("ACTIVIDAD", idMaintenance);
                    i.putStringArrayListExtra("RESPUESTA", s);
                    startActivityForResult(i, REQUEST_FORMULARIO);*/
                } else {
                    Toast.makeText(mContext, s.get(1), Toast.LENGTH_LONG).show();
                }
            }

            if (progressDialog.isShowing())
                progressDialog.dismiss();
        }
    }


    private class AgendaTask extends AsyncTask<String, String, ArrayList<Maintenance>> {
        ProgressDialog progressDialog;
        Context tContext;
        String ATAG = "AGENDATASK";

        String message = "";
        Boolean connected = false;

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
        protected ArrayList<Maintenance> doInBackground(String... strings) {
            try {
                publishProgress("Cargando Actividades...");
                String query = SoapRequest.getInformation(IMEI);
                Log.d("FRAGMENT", query);
                //query = dummy.getInformation;
                ArrayList<String> re = XMLParser.getReturnCode2(query);
                Log.d("FRAGMENT", re.toString());
                if (re.get(0).compareTo("0") == 0) {
                    return XMLParser.getListadoActividades(query);
                } else {
                    connected = true;
                    message = re.get(1);
                    return null;
                }
            } catch (Exception e) {
                Log.e(ATAG, e.getMessage() + ":\n" + e.getCause());
                return null; //Error
            }

        }

        @Override
        protected void onPostExecute(final ArrayList<Maintenance> s) {
            if (progressDialog.isShowing())
                progressDialog.dismiss();

            if (s == null) {
                if (connected) {
                    Toast.makeText(tContext, message, Toast.LENGTH_LONG).show();

                }
                else
                    Toast.makeText(tContext, "Error en la conexion. Intente nuevamente.", Toast.LENGTH_LONG).show();

                finish();
            } else {
                mPagerAdapter = new PagerAdapter() {
                    @Override
                    public int getCount() {
                        return s.size();
                    }

                    @Override
                    public CharSequence getPageTitle(int position) {
                        if(position == 0){
                            return "ASIGNADA";
                        }else {
                            return "FINALIZADA "+position;
                        }
                    }

                    @Override
                    public boolean isViewFromObject(View view, Object object) {
                        return view == object;
                    }

                    @Override
                    public Object instantiateItem(ViewGroup container, int position) {
                        final Maintenance m = s.get(position);
                        final Boolean terminated;
                        if (m.getStatus().compareTo("TERMINATED") == 0)
                            terminated = true;
                        else
                            terminated = false;
                        View rootView = LayoutInflater.from(tContext).inflate(R.layout.agenda_view, null, false);
                        TextView tDate = (TextView) rootView.findViewById(R.id.tDate);
                        TextView tSystem = (TextView) rootView.findViewById(R.id.tSystem);
                        TextView tAddress = (TextView) rootView.findViewById(R.id.tAddress);
                        TextView tStation = (TextView) rootView.findViewById(R.id.tStation);
                        TextView tStatus = (TextView) rootView.findViewById(R.id.tStatus);
                        TextView tType = (TextView) rootView.findViewById(R.id.tType);

                        final ImageButton bComplete = (ImageButton) rootView.findViewById(R.id.bComplete);
                        bComplete.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                CompletarActividad c = new CompletarActividad(tContext, m.getIdMaintenance());
                                c.execute();
                            }
                        });
                        bComplete.setEnabled(false);
                        bComplete.bringToFront();


                        //final ImageButton bMap = (ImageButton) rootView.findViewById(R.id.bMap);
                        //bMap.setVisibility(View.GONE);

                        ImageView iMap = (ImageView) rootView.findViewById(R.id.iMap);
                        LinearLayout lActivities = (LinearLayout) rootView.findViewById(R.id.lActivities);
                        final ProgressBar pProgress = (ProgressBar) rootView.findViewById(R.id.pProgress);

                        Double desplazamiento = 0.012;
                        String url = "http://maps.google.com/maps/api/staticmap?center=" +
                                m.getLatitude() +
                                "," +
                                (m.getLongitude() - desplazamiento) +
                                "&zoom=14&size=600x200&maptype=roadmap&markers=color:red|color:red|label:P|" +
                                m.getLatitude() +
                                "," +
                                m.getLongitude() + "" +
                                "&sensor=false";
                        ImageLoader.getInstance().displayImage(url, iMap);


                        int max = m.getActivities().size();
                        pProgress.setMax(max);
                        for (final Actividades a : m.getActivities()) {
                            View vista = LayoutInflater.from(tContext).inflate(R.layout.activity_view, null, false);
                            ((TextView) vista.findViewById(R.id.tName)).setText(a.getName());
                            if (a.getDescription() == null)
                                ((TextView) vista.findViewById(R.id.tDescription)).setText("Sin Descripción");
                            else
                                ((TextView) vista.findViewById(R.id.tDescription)).setText(a.getDescription());
                            CheckBox checkBox = (CheckBox) vista.findViewById(R.id.chCompleted);
                            if (terminated) {
                                checkBox.setEnabled(false);
                                checkBox.setChecked(true);
                                pProgress.setProgress(pProgress.getMax());
                                bComplete.setEnabled(false);
                            }
                            if (!terminated) {
                                checkBox.setEnabled(true);
                            }

                            checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                                @Override
                                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                                    int progress = pProgress.getProgress();
                                    if (b) {
                                        pProgress.setProgress(progress + 1);
                                        if (!terminated)
                                            pref.stateActivity(a, true);
                                        Log.d("FRAGMENT", "Actividad: " + a.getName() + " estado completada");
                                    } else {
                                        pProgress.setProgress(progress - 1);
                                        if (!terminated)
                                            pref.stateActivity(a, false);
                                        Log.d("FRAGMENT", "Actividad: " + a.getName() + " estado no completada");
                                    }

                                    if (pProgress.getProgress() == pProgress.getMax()) {
                                        bComplete.setEnabled(true);
                                    } else {
                                        bComplete.setEnabled(false);
                                    }

                                }
                            });

                            if (!terminated && pref.isCompleteActivity(a)) {
                                checkBox.setChecked(true);
                                if (pProgress.getProgress() == pProgress.getMax()) {
                                    bComplete.setEnabled(true);
                                } else {
                                    bComplete.setEnabled(false);
                                }
                            }


                            lActivities.addView(vista);
                        }
                        tDate.setText(m.getDate());
                        tSystem.setText(m.getSystem());
                        tAddress.setText(m.getAddress());
                        tStation.setText(m.getStation());
                        tStatus.setText(m.getStatus());
                        tType.setText(m.getType());

                        ((ViewPager) container).addView(rootView);

                        return rootView;
                    }
                };
                mPager.setAdapter(mPagerAdapter);

                Log.d(ATAG, s.toString());
            }
        }



    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_FORMULARIO) {
            if (resultCode == RESULT_OK) {
                AgendaTask agenda = new AgendaTask(this);
                agenda.execute();
            }

        }
    }

    public class ZoomOutPageTransformer implements ViewPager.PageTransformer {
        private static final float MIN_SCALE = 0.85f;
        private static final float MIN_ALPHA = 0.5f;

        public void transformPage(View view, float position) {
            int pageWidth = view.getWidth();
            int pageHeight = view.getHeight();

            if (position < -1) { // [-Infinity,-1)
                // This page is way off-screen to the left.
                view.setAlpha(0);

            } else if (position <= 1) { // [-1,1]
                // Modify the default slide transition to shrink the page as well
                float scaleFactor = Math.max(MIN_SCALE, 1 - Math.abs(position));
                float vertMargin = pageHeight * (1 - scaleFactor) / 2;
                float horzMargin = pageWidth * (1 - scaleFactor) / 2;
                if (position < 0) {
                    view.setTranslationX(horzMargin - vertMargin / 2);
                } else {
                    view.setTranslationX(-horzMargin + vertMargin / 2);
                }

                // Scale the page down (between MIN_SCALE and 1)
                view.setScaleX(scaleFactor);
                view.setScaleY(scaleFactor);

                // Fade the page relative to its size.
                view.setAlpha(MIN_ALPHA +
                        (scaleFactor - MIN_SCALE) /
                                (1 - MIN_SCALE) * (1 - MIN_ALPHA));

            } else { // (1,+Infinity]
                // This page is way off-screen to the right.
                view.setAlpha(0);
            }
        }
    }
}
