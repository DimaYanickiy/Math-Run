package com.matematics.mathru;

import android.content.Context;
import android.content.SharedPreferences;

public class Saver {

    SharedPreferences sp;

    public Saver(Context context){
        sp = context.getSharedPreferences("PREFS", Context.MODE_PRIVATE);
    }

    public int loadFirstRef() {
        int n = sp.getInt("FIRST_REF", 0);
        return n;
    }

    public void saveFirstRef(int firstref) {
        sp.edit().putInt("FIRST_REF", firstref).apply();
    }

    public boolean loadFirstAppsFlyer() {
        boolean k = sp.getBoolean("FF", true);
        return k;
    }

    public void setFirstAppsFlyer(boolean ff) {
        sp.edit().putBoolean("FF", ff).apply();
    }

    public boolean loadFirst() {
        boolean h = sp.getBoolean("FR", true);
        return h;
    }

    public void saveFirst(boolean fr) {
        sp.edit().putBoolean("FR", fr).apply();
    }

    public String loadPoint() {
        String p = sp.getString("P", "");
        return p;
    }

    public void savePoint(String point) {
        sp.edit().putString("P", point).apply();
    }
}
