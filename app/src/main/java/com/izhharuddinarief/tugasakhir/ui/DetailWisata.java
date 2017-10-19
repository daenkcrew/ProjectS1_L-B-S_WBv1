/*
 * Dibuat Oleh Arief Izhharuddin pada 9/17/17 5:10 AM
 * Copyright (c) 2017. All rights reserved
 *
 * Terakhir dimodifikasi 9/17/17 5:10 AM
 * username IzhharuddinArief
 */

package com.izhharuddinarief.tugasakhir.ui;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.izhharuddinarief.tugasakhir.R;
import com.izhharuddinarief.tugasakhir.apiSERVER;
import com.izhharuddinarief.tugasakhir.parser.JSONParser;
import com.squareup.picasso.Picasso;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class DetailWisata extends AppCompatActivity {

    public static final String TAG_NAMA = "nama";
    public static final String TAG_ALAMAT = "alamat";
    public static final String TAG_TELEPON = "telepon";
    public static final String TAG_TIKET = "tiket";
    public static final String TAG_LAT = "lat";
    public static final String TAG_LNG = "lng";
    public static final String TAG_FAS = "fasilitas";
    public static final String TAG_KET = "keterangan";
    public static final String TAG_GAMBAR = "gambar";

    apiSERVER serverlink = new apiSERVER();
    String key, Nama, Alamat, Telp, Tik, Lat, Lng, Ket, Fas, Gambar;
    TextView nama, alamat, telp, tik, lat, lng, fas, ket;
    JSONParser jParser = new JSONParser();
    ProgressDialog pDialog;
    JSONArray string_json = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_wisata);
        key = getIntent().getStringExtra(TAG_NAMA);
        nama = (TextView) findViewById(R.id.nama_wisata);
        alamat = (TextView) findViewById(R.id.alamat_wisata);
        telp = (TextView) findViewById(R.id.telepon_wisata);
        telp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String toCall = "tel:" + telp.getText().toString();
                startActivity(new Intent(Intent.ACTION_DIAL, Uri.parse(toCall)));
            }
        });

        tik =(TextView) findViewById(R.id.tiket);

        lat = (TextView) findViewById(R.id.lat);
        lng = (TextView) findViewById(R.id.lng);
        fas = (TextView) findViewById(R.id.fasilitas);
        ket = (TextView) findViewById(R.id.ket_wisata);
        ConnectivityManager conMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (conMgr.getActiveNetworkInfo() != null && conMgr.getActiveNetworkInfo().isAvailable()
                && conMgr.getActiveNetworkInfo().isConnected()) {
            new AmbilDataWisata().execute();
        } else {
            AlertDialog.Builder dialog = new AlertDialog.Builder(this);
            dialog.setTitle("Informasi");
            dialog.setMessage("Cek koneksi internet anda");
            dialog.setCancelable(true);
            dialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            }).show();
        }

        Button mapnavigasi = (Button) findViewById(R.id.map_navigasi);
        mapnavigasi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(DetailWisata.this, RuteWisata.class);
                intent.putExtra("nama", DetailWisata.this.Nama);
                intent.putExtra("lat", DetailWisata.this.Lat);
                intent.putExtra("lng", DetailWisata.this.Lng);
                startActivity(intent);
            }
        });
    }

    private class AmbilDataWisata extends AsyncTask<String, String, String> {

        protected void onPreExecute() {
            pDialog = new ProgressDialog(DetailWisata.this);
            pDialog.setMessage("Mohon tunggu...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        protected String doInBackground(String... args) {
            try {
                List<NameValuePair> params = new ArrayList<>();
                params.add(new BasicNameValuePair(TAG_NAMA, DetailWisata.this.key));
                JSONObject json = jParser.makeHttpRequest(serverlink.url_detail_wisata_json, "POST", params);
                string_json = json.getJSONArray("wisata");
                Log.e("Wisata: ", ">" + json);
                runOnUiThread(new Runnable() {
                    @SuppressLint("SetTextI18n")
                    public void run() {
                        ImageView img = (ImageView)findViewById(R.id.gambar_wisata);
                        try {
                            JSONObject jObj = string_json.getJSONObject(0);
                            Nama = jObj.getString(TAG_NAMA);
                            Alamat = jObj.getString(TAG_ALAMAT).trim();
                            Telp = jObj.getString(TAG_TELEPON).trim();
                            Tik = jObj.getString(TAG_TIKET).trim();
                            Lat = jObj.getString(TAG_LAT).trim();
                            Lng = jObj.getString(TAG_LNG).trim();
                            Ket = jObj.getString(TAG_KET).trim();
                            Fas = jObj.getString(TAG_FAS).trim();
                            Gambar = jObj.getString(TAG_GAMBAR).trim();
                            String url_gambar_detail = jObj.getString(TAG_GAMBAR);
                            nama.setText(Nama);
                            alamat.setText(Alamat);
                            telp.setText(Telp);
                            tik.setText(Tik);
                            lat.setText(Lat + ", ");
                            lng.setText(Lng);
                            fas.setText(Fas);
                            ket.setText(Ket);
                            Picasso.with(getApplicationContext())
                                    .load(url_gambar_detail)
                                    .error(R.drawable.placeholder)
                                    .into(img);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }
        protected void onPostExecute(String file_url) {

            pDialog.dismiss();
        }
    }
    public void onBackPressed() {
        finish();
    }
}