/*
 * Dibuat Oleh Arief Izhharuddin pada 9/17/17 5:10 AM
 * Copyright (c) 2017. All rights reserved
 *
 * Terakhir dimodifikasi 9/17/17 5:10 AM
 * username IzhharuddinArief
 */

package com.izhharuddinarief.tugasakhir.ui;

import android.Manifest;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v4.internal.view.SupportMenu;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.izhharuddinarief.tugasakhir.R;
import com.izhharuddinarief.tugasakhir.parser.DirectionsJSONParser;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class RuteWisata extends AppCompatActivity implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {

    private GoogleMap mMap;
    ArrayList<LatLng> MarkerPoints;
    GoogleApiClient mGoogleApiClient;
    Location mLastLocation;
    Marker mCurrLocationMarker;
    LocationRequest mLocationRequest;
    String Nama, Lat, Lng;
    CameraPosition cameraPosition;
    LocationManager locationManager;
    TextView lokasi;
    double mLatitude = 0.0d;
    double mLongitude = 0.0d;
    int mMode = 0;
    Marker marker;
    RadioButton rbBiCycling;
    RadioButton rbDriving;
    RadioButton rbWalking;
    RadioGroup rgModes;
    TextView tvDistanceDuration;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rute);
        // inisialisai
        MarkerPoints = new ArrayList<>();

        lokasi = (TextView) findViewById(R.id.lokasi);
        tvDistanceDuration = (TextView) findViewById(R.id.tv_jarak_waktu);
        rbDriving = (RadioButton) findViewById(R.id.rb_driving);
        rbBiCycling = (RadioButton) findViewById(R.id.rb_bicycling);
        rbWalking = (RadioButton) findViewById(R.id.rb_walking);
        rgModes = (RadioGroup) findViewById(R.id.rg_modes);

        // Get Location Manager and check for GPS & Network location services
        LocationManager lm = (LocationManager) getSystemService(LOCATION_SERVICE);
        if(!lm.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                !lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
            // Build the alert dialog
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("GPS TIDAK AKTIF! ");
            builder.setMessage("Silahkan ke menu setting dan aktifkan GPS!");
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialogInterface, int i) {
                    // Show location settings when the user acknowledges the alert dialog
                    Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    startActivity(intent);
                }
            });
            Dialog alertDialog = builder.create();
            alertDialog.setCanceledOnTouchOutside(false);
            alertDialog.show();
        } else {
            /*Toast.makeText(RuteWisata.this, "Silahkan, Tap pada peta untuk menampilkan rute!!! =)",
                    Toast.LENGTH_LONG)
                    .show();*/

            AlertDialog.Builder alert = new AlertDialog.Builder(this);
            alert.setIcon(R.drawable.warning);
            alert.setTitle("INFORMASI!");
            alert.setMessage("Tekan pada peta untuk menampilkan rute.");
            alert.setCancelable(true);
            alert.setPositiveButton("OK!", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            }).show();


        }
        Nama = getIntent().getStringExtra("nama");
        Lat = getIntent().getStringExtra("lat");
        Lng = getIntent().getStringExtra("lng");
        lokasi.setText(Nama);
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.getUiSettings().setCompassEnabled(true);
        // mMap.getUiSettings().setMyLocationButtonEnabled(true);
        //Initialize Google Play Services
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                buildGoogleApiClient();
                mMap.setMyLocationEnabled(true);
            }
        }
        else {
            buildGoogleApiClient();
            mMap.setMyLocationEnabled(true);
        }
        // Setting onclick event listener for the map
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            public void onMapClick(LatLng dest) {
                if (MarkerPoints.size() > 1) {
                    MarkerPoints.clear();
                    mMap.clear();
                    drawMarker(new LatLng(mLatitude, mLongitude));
                }
                dest = new LatLng(Double.parseDouble(Lat),
                        Double.parseDouble(Lng));
                marker = mMap.addMarker(new MarkerOptions()
                        .title(Nama)
                        .position(dest));
                drawMarker(dest);
                if (MarkerPoints.size() >= 2) {
                    String url = getDirectionsUrl(MarkerPoints.get(0), dest);
                    new FetchUrl().execute(url);
                }
            }
        });

        rgModes.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (MarkerPoints.size() >= 2) {
                    String url = getDirectionsUrl(MarkerPoints.get(0),
                            new LatLng(Double.parseDouble(Lat),
                                    Double.parseDouble(Lng)));
                    new FetchUrl().execute(url);
                }
            }
        });
    }
    private String getDirectionsUrl(LatLng origin, LatLng dest) {
        String str_origin = "origin=" + origin.latitude + "," + origin.longitude;
        String str_dest = "destination=" + dest.latitude + "," + dest.longitude;
        String sensor = "sensor=true";
        String mode = "mode=driving";
        if (this.rbDriving.isChecked()) {
            mode = "mode=driving";
            this.mMode = 0;
        } else if (this.rbBiCycling.isChecked()) {
            mode = "mode=cycling";
            this.mMode = 1;
        } else if (this.rbWalking.isChecked()) {
            mode = "mode=walking";
            this.mMode = 2;
        }

        return "https://maps.googleapis.com/maps/api/directions/" + "json"
                + "?" + (str_origin + "&" + str_dest + "&" + sensor + "&" + mode);
    }

    /**
     * A method to download json data from url
     */
    private String downloadUrl(String strUrl) throws IOException {
        String data = "";
        HttpURLConnection urlConnection;
        URL url = new URL(strUrl);
        // Creating an http connection to communicate with url
        urlConnection = (HttpURLConnection) url.openConnection();
        // Connecting to url
        urlConnection.connect();
        // Reading data from url
        try (InputStream iStream = urlConnection.getInputStream()) {
            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
            data = sb.toString();
            br.close();
        } catch (Exception e) {
            Log.d("Exception", e.toString());
        } finally {
            urlConnection.disconnect();
        }
        Log.d("Data =", "url"+ data);
        return data;
    }
    // Fetches data from url passed
    private class FetchUrl extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... url) {
            // For storing data from web service
            String data = "";
            try {
                // Fetching the data from web service
                data = downloadUrl(url[0]);
                Log.d("Background Task data", data);
            } catch (Exception e) {
                Log.d("Background Task", e.toString());
            }
            return data;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            ParserTask parserTask = new ParserTask();
            // Invokes the thread for parsing the JSON data
            parserTask.execute(result);
        }
    }

    /**
     * A class to parse the Google Places in JSON format
     */
    private class ParserTask extends AsyncTask<String, Integer, List<List<HashMap<String, String>>>> {

        protected List<List<HashMap<String, String>>> doInBackground(String... jsonData) {
            List<List<HashMap<String, String>>> routes = null;
            try {
                routes = new DirectionsJSONParser().parse(new JSONObject(jsonData[0]));
            } catch (Exception e) {
                e.printStackTrace();
            }
            return routes;
        }
        // Executes in UI thread, after the parsing process
        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> result) {
            PolylineOptions lineOptions = null;
            String distance = "";
            String duration = "";
            for (int i = 0; i < result.size(); i++) {
                ArrayList<LatLng> points = new ArrayList<>();
                lineOptions = new PolylineOptions();
                List<HashMap<String, String>> path = result.get(i);
                for (int j = 0; j < path.size(); j++) {
                    HashMap<String, String> point = path.get(j);
                    if (j == 0) {
                        distance = point.get("distance");
                    } else if (j == 1) {
                        duration = point.get("duration");
                    } else {
                        points.add(new LatLng(Double.parseDouble(point.get("lat")), Double.parseDouble(point.get("lng"))));
                    }
                }

                lineOptions.addAll(points);
                lineOptions.width(5.0f);
                if (RuteWisata.this.mMode == 0) {
                    lineOptions.color(SupportMenu.CATEGORY_MASK);
                } else if (RuteWisata.this.mMode == 1) {
                    lineOptions.color(-16711936);
                } else if (RuteWisata.this.mMode == 2) {
                    lineOptions.color(-16776961);
                }
            }
            tvDistanceDuration.setText("Jarak : " + distance + " , Waktu Tempuh : " + duration);
            mMap.addPolyline(lineOptions);
        }
    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnected(Bundle bundle) {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(1000);
        mLocationRequest.setFastestInterval(1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onLocationChanged(Location location) {
        if (MarkerPoints.size() < 2) {
            mLatitude = location.getLatitude();
            mLongitude = location.getLongitude();
            LatLng point = new LatLng(mLatitude, mLongitude);
            cameraPosition = new CameraPosition
                    .Builder()
                    .target(point)
                    .zoom(9.0f)
                    .build();

            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition), 1000, null);
            drawMarker(point);
        }
        mLastLocation = location;
        if (mCurrLocationMarker != null) {
            mCurrLocationMarker.remove();
        }
    }

    private void drawMarker(LatLng point) {
        MarkerPoints.add(point);
        MarkerOptions options = new MarkerOptions();
        options.position(point)
                .title("Posisi anda!");
        if (MarkerPoints.size() == 1) {
            options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
        }
        else if (MarkerPoints.size() == 2) {
            options.icon(BitmapDescriptorFactory.defaultMarker(0.0f));
        }
        mMap.addMarker(options);
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_peta, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId())
        {
            case R.id.menu_layers_normal:
                mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                return true;
            case R.id.menu_layers_satellite:
                mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
                return true;
            case R.id.menu_layers_hybrid:
                mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
                return true;
            case R.id.menu_layers_terrain:
                mMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
                return true;
            case android.R.id.home:
                onBackPressed();
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}