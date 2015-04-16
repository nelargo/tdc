package cl.tdc.felipe.tdc;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import cl.tdc.felipe.tdc.daemon.MyLocationListener;

public class CercanosActivity extends FragmentActivity implements GoogleMap.OnMapClickListener{
    public static Activity actividad;
    public static final int ZOOM_LEVEL = 15;

    MyLocationListener gps;

    private GoogleMap mapa;
    private LatLng myPosition;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cercanos);
        actividad = this;

        gps = new MyLocationListener(this);
        mapa = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map)).getMap();
        mapa.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        mapa.setMyLocationEnabled(true);
        mapa.getUiSettings().setZoomControlsEnabled(false);
        mapa.getUiSettings().setCompassEnabled(true);
        mapa.getUiSettings().setMyLocationButtonEnabled(true);
        mapa.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(gps.getLatitude(),gps.getLongitude()),ZOOM_LEVEL));
        mapa.setOnMapClickListener(this);

        mapa.addMarker(new MarkerOptions().position(new LatLng(gps.getLatitude(),gps.getLongitude())).title("Titulo").snippet("Breve información sobre esta ubicación.\n Quizás se pueden poner varias líneas.\n\n Fin.").icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_punto_ubicacion_red)));



    }

    // TODO: funcion onClick del botón apagar.

    public void onClick_apagar(View v){
        MainActivity.actividad.finish();
        finish();
    }

    public void onClick_back(View v){
        finish();
    }

    @Override
    public void onMapClick(LatLng latLng) {

    }
}
