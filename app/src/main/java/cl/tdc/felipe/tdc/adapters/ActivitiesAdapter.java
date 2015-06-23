package cl.tdc.felipe.tdc.adapters;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.support.v4.view.MotionEventCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import java.util.List;
import cl.tdc.felipe.tdc.R;

public class ActivitiesAdapter extends RecyclerView.Adapter<ActivitiesAdapter.ViewHolder> {
    private List<Actividad> mActividad;
    private Context mContext;
    private LayoutInflater inflater;
    private ClickListener clickListener;

    public ActivitiesAdapter(Context context, ClickListener clickListener, List<Actividad> actividad) {
        this.mContext = context;
        this.mActividad = actividad;
        this.inflater = LayoutInflater.from(mContext);
        this.clickListener = clickListener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, final int i) {
        View v;
        ViewHolder vh;
        if (i == 0) {
            v = inflater.inflate(R.layout.actividades_view_asigned, parent, false);
            ((ImageButton)v.findViewById(R.id.card_complete)).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    clickListener.itemClicked(view, 0, mActividad.get(i));
                }
            });

            ((ImageButton)v.findViewById(R.id.card_map)).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    final Dialog d = new Dialog(mContext);

                    final View dialog = LayoutInflater.from(mContext).inflate(R.layout.map_dialog, null);
                    final ImageView map = (ImageView)dialog.findViewById(R.id.ImageMapView);
                    final ProgressBar progressBar = (ProgressBar)dialog.findViewById(R.id.progressMap);
                    final ImageButton boton = (ImageButton)dialog.findViewById(R.id.button);
                    boton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            d.dismiss();
                        }
                    });
                    setupIL();
                    String latitud = mActividad.get(0).getCoordX();
                    String longitud = mActividad.get(0).getCoordY();
                    String url = "http://maps.google.com/maps/api/staticmap?center="+longitud+","+latitud+"&zoom=8&size=1000x500&maptype=hybrid&markers=color:red|label:AQUI|"+longitud+","+latitud+"&sensor=false";
                    ImageLoader.getInstance().displayImage(url, map, new ImageLoadingListener() {
                        @Override
                        public void onLoadingStarted(String imageUri, View view) {

                        }

                        @Override
                        public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                            d.dismiss();
                            Toast.makeText(mContext, "No se pudo cargar la imagen.", Toast.LENGTH_LONG).show();
                        }

                        @Override
                        public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                            progressBar.setVisibility(View.GONE);
                            map.setImageBitmap(loadedImage);
                        }

                        @Override
                        public void onLoadingCancelled(String imageUri, View view) {
                            d.dismiss();
                            Toast.makeText(mContext, "No se pudo cargar la imagen.", Toast.LENGTH_LONG).show();
                        }
                    });
                    d.setContentView(dialog);
                    d.setTitle("Ubicacion");
                    d.show();
                }
            });

        } else {
            v = inflater.inflate(R.layout.actividades_view, parent, false);
        }
        vh = new ViewHolder(v);
        return vh;
    }

    void setupIL(){
        DisplayImageOptions options = new DisplayImageOptions.Builder()
                .resetViewBeforeLoading(false)  // default
                .delayBeforeLoading(1000)
                .cacheOnDisk(true) // default
                .build();

        ImageLoaderConfiguration.Builder config = new ImageLoaderConfiguration.Builder(mContext);
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

    @Override
    public void onBindViewHolder(ViewHolder vh, int position) {
        Actividad current = mActividad.get(position);
        vh.actividad.setText(current.getActivityId());
        vh.code.setText(current.getZoneCode());
        vh.duracion.setText(current.getDurationKeyName());
        //vh.estado.setText(current.getStatus());
        vh.estado.setVisibility(View.GONE);
        vh.name.setText(current.getZoneName());
        vh.slaEnd.setText(current.getSlaEnd().substring(0, current.getSlaEnd().length() - 6));
        vh.tipo.setText(current.getType());

    }

    @Override
    public int getItemCount() {
        return mActividad.size();
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        RelativeLayout card;
        TextView actividad, tipo, slaEnd, estado, code, name, duracion;
        View view;

        public ViewHolder(View v) {
            super(v);
            view = v;
            card = (RelativeLayout) v.findViewById(R.id.contentCard);
            actividad = (TextView) v.findViewById(R.id.tv_actividad);
            tipo = (TextView) v.findViewById(R.id.tv_tipo);
            slaEnd = (TextView) v.findViewById(R.id.tv_fecha);
            estado = (TextView) v.findViewById(R.id.tv_estado);
            code = (TextView) v.findViewById(R.id.tv_codigo);
            name = (TextView) v.findViewById(R.id.tv_nombre);
            duracion = (TextView) v.findViewById(R.id.tv_duracion);
        }


    }

    public interface ClickListener {
        public void itemClicked(View view, int type, Actividad actividad);
    }


}
