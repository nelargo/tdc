package cl.tdc.felipe.tdc.preferences;


import android.content.Context;
import android.content.SharedPreferences;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import cl.tdc.felipe.tdc.adapters.Actividad;
import cl.tdc.felipe.tdc.adapters.Actividades;

public class PreferencesTDC  {
    public static final String NAME = "TDC_PREFERENCES";
    public static final String SETTING_IMEI = "IMEI";
    public static final String SETTING_IMSI = "IMSI";

    public Context mContext;
    public SharedPreferences sharedPreferences;

    public PreferencesTDC(Context context) {
        super();
        mContext = context;
        sharedPreferences = mContext.getSharedPreferences(NAME, Context.MODE_PRIVATE);
    }

    public void setIMEI(String data){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(SETTING_IMEI,data);
        editor.commit();
    }

    public void stateActivity(Actividades a, boolean complete){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("ACTIVIDAD" + a.getIdActivity(), complete);
        editor.commit();
    }

    public void deleteStateActivity(Actividades a){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove("ACTIVIDAD"+a.getIdActivity());
        editor.commit();
    }

    public boolean isCompleteActivity(Actividades a){
        return sharedPreferences.getBoolean("ACTIVIDAD" + a.getIdActivity(), false);
    }


    public void setIMSI(String data){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(SETTING_IMSI, data);
        editor.commit();
    }



}
