package cl.tdc.felipe.tdc.preferences;


import android.content.Context;
import android.content.SharedPreferences;

public class PreferencesTDC  {
    public static final String NAME = "TDC_PREFERENCES";
    public static final String SETTING_URL = "URL";
    public static final String SETTING_IMEI = "IMEI";
    public static final String SETTING_IMSI = "IMSI";
    public static final String SETTING_DAEMON_TIME = "DAEMON_TIME";
    public Context mContext;
    public SharedPreferences sharedPreferences;

    public PreferencesTDC(Context context){
        super();
        mContext = context;
        sharedPreferences = mContext.getSharedPreferences(NAME,Context.MODE_PRIVATE);
    }

    public void setURL(String data){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(SETTING_URL,data);
        editor.commit();
    }

    public void setIMEI(String data){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(SETTING_IMEI,data);
        editor.commit();
    }

    public void setIMSI(String data){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(SETTING_IMSI,data);
        editor.commit();
    }

    public void setDaemonTime(int data){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(SETTING_DAEMON_TIME,data);
        editor.commit();
    }


    public String getURL(){
        return sharedPreferences.getString(SETTING_URL,null);
    }

    public String getIMEI(){
        return sharedPreferences.getString(SETTING_IMEI,null);
    }

    public String getIMSI(){
        return sharedPreferences.getString(SETTING_IMSI,null);
    }

    public int getDaemonTime(){
        return sharedPreferences.getInt(SETTING_DAEMON_TIME,1000*60*3);
    }


}
