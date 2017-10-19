/*
 * Dibuat Oleh Arief Izhharuddin pada 9/18/17 8:09 PM
 * Copyright (c) 2017. All rights reserved
 *
 * Terakhir dimodifikasi 9/18/17 2:02 AM
 * username IzhharuddinArief
 */

package com.izhharuddinarief.tugasakhir.ui;


import android.Manifest;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.daimajia.slider.library.Animations.DescriptionAnimation;
import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderTypes.BaseSliderView;
import com.daimajia.slider.library.SliderTypes.TextSliderView;
import com.daimajia.slider.library.Tricks.ViewPagerEx;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.izhharuddinarief.tugasakhir.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends AppCompatActivity implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        ResultCallback<LocationSettingsResult>,
        ViewPagerEx.OnPageChangeListener{

    // image slider
    private SliderLayout mDemoSlider;

    // GPS ENABLE DIALONG
    protected GoogleApiClient mGoogleApiClient;
    protected LocationRequest locationRequest;
    int REQUEST_CHECK_SETTINGS = 100;

    private static long back_pressed;
    ImageView btn_peta, btn_tentang, btn_wisata, btn_bantuan;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (Build.VERSION.SDK_INT < 23) {
            //Do not need to check the permission 
        } else {
            if (cekpermissionlokasiandroid_M()) {
                //If you have already permitted the permission
            }
        }
        // GPS ENABLE DIALONG
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this).build();
        mGoogleApiClient.connect();
        locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(30 * 1000);
        locationRequest.setFastestInterval(5 * 1000);

        // image slider
        mDemoSlider = (SliderLayout) findViewById(R.id.slider);
        // image slider
        HashMap<String, String> url_maps = new HashMap<>();

       // url_maps.put("Peta Destinasi Wisata Kab. Brebes",
         //       "http://wisatabrebes.jayanto.id/assets/img/wisata/Peta_Wisata_Brebes.jpg");
        url_maps.put("Agrowisata Kaligua",
                "http://wisatabrebes.jayanto.id/assets/img/wisata/Agrowisata_Kaligua.JPG");
        url_maps.put("Mangrove Pandansari",
                "http://wisatabrebes.jayanto.id/assets/img/wisata/Mangrove_Pandansari.jpg");
        url_maps.put("Waduk Malahayu",
                "http://wisatabrebes.jayanto.id/assets/img/wisata/Waduk_Malahayu.jpg");
        url_maps.put("Pantai Randusanga Indah ",
                "http://wisatabrebes.jayanto.id/assets/img/wisata/Pantai_Randusangan_Indahh.jpg");

        // image slider
        // when we show slider, we must create for or while, you can add it
        for (String name : url_maps.keySet()) {
            TextSliderView textSliderView = new TextSliderView(this);
            // initialize a SliderLayout
            textSliderView
                    .description(name)
                    .image(url_maps.get(name))
                    .setScaleType(BaseSliderView.ScaleType.Fit);

            //add your extra information
            textSliderView.bundle(new Bundle());
            textSliderView.getBundle()
                    .putString("extra", name);

            mDemoSlider.addSlider(textSliderView);
        }
        // image slider
        // you can change animasi, time page and anythink.. read more on github
        mDemoSlider.setPresetTransformer(SliderLayout.Transformer.Accordion);
        mDemoSlider.setPresetIndicator(SliderLayout.PresetIndicators.Right_Bottom);
        mDemoSlider.setCustomAnimation(new DescriptionAnimation());
        mDemoSlider.setDuration(5000);
        mDemoSlider.addOnPageChangeListener(this);
        // deklasrasi ImageButton
        btn_wisata = (ImageView) findViewById(R.id.btn_wisata);
        btn_peta = (ImageView) findViewById(R.id.btn_peta);
        btn_bantuan = (ImageView) findViewById(R.id.btn_bantuan);
        btn_tentang = (ImageView) findViewById(R.id.btn_tentang);

        // set klik imageButton
        btn_wisata.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                MainActivity.this.startActivity(new Intent(MainActivity.this, Wisata.class));
                MainActivity.this.finish();
            }
        });

        btn_peta.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                MainActivity.this.startActivity(new Intent(MainActivity.this, PetaWisata.class));
                MainActivity.this.finish();
            }
        });

        btn_bantuan.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                MainActivity.this.startActivity(new Intent(MainActivity.this, Bantuan.class));
                MainActivity.this.finish();
            }
        });

        btn_tentang.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                MainActivity.this.startActivity(new Intent(MainActivity.this, Tentang.class));
                MainActivity.this.finish();
            }
        });
    }

    private static final int MY_PERMISSIONS_REQUEST_ACCOUNTS = 1;
    private boolean cekpermissionlokasiandroid_M() {

        //Here i am checking for account Permission
        int accountPermission = ContextCompat.checkSelfPermission(this,

                Manifest.permission.ACCESS_FINE_LOCATION);
        List<String> listPermissionsNeeded = new ArrayList<>();

        if (accountPermission != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.ACCESS_FINE_LOCATION);
        }
        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(this,

                    listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]), MY_PERMISSIONS_REQUEST_ACCOUNTS);
            return false;
        }

        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_ACCOUNTS:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //Permission Granted Successfully. Write working code here.
                } else {
                    //You did not accept the request can not use the functionality.
                }
                break;
        }
    }

    public void onBackPressed() {
        if (back_pressed + 2000 < System.currentTimeMillis()) {
            Toast.makeText(this, "Tekan lagi untuk keluar dari Aplikasi!", Toast.LENGTH_SHORT).show();
        } else {
            finish();
            System.exit(0);
        }
        back_pressed = System.currentTimeMillis();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        // GPS ENABLE DIALONG
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest);
        builder.setAlwaysShow(true);
        PendingResult<LocationSettingsResult> result =
                LocationServices.SettingsApi.checkLocationSettings(
                        mGoogleApiClient,
                        builder.build()
                );
        result.setResultCallback(this);
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onResult(@NonNull LocationSettingsResult locationSettingsResult) {
        // GPS ENABLE DIALONG
        final Status status = locationSettingsResult.getStatus();
        switch (status.getStatusCode()) {
            case LocationSettingsStatusCodes.SUCCESS:
                break;
            case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                try {
                    status.startResolutionForResult(MainActivity.this, REQUEST_CHECK_SETTINGS);
                } catch (IntentSender.SendIntentException e) {
                    //failed to show
                }
                break;
            case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CHECK_SETTINGS) {
            if (resultCode == RESULT_OK) {
                Toast.makeText(getApplicationContext(), "GPS Aktif", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(getApplicationContext(), "GPS Tidak Aktif", Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    protected void onStop() {
        mDemoSlider.stopAutoCycle();
        super.onStop();
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        Log.d("Slider isi", "Page Changed: " + position);

    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }
}
