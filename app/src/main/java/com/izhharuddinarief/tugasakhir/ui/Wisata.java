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
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;

import com.izhharuddinarief.tugasakhir.R;
import com.izhharuddinarief.tugasakhir.apiSERVER;
import com.izhharuddinarief.tugasakhir.adapter.WisataAdapter;
import com.izhharuddinarief.tugasakhir.parser.JSONParser;

import org.apache.http.NameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class Wisata extends AppCompatActivity {

    public static final String TAG_ID = "id";
    public static final String TAG_NAMA = "nama";
    public static final String TAG_ALAMAT = "alamat";
    public static final String TAG_GAMBAR = "gambar";

    apiSERVER serverlink = new apiSERVER();
    ArrayList<HashMap<String, String>> DaftarWisata = new ArrayList<>();
    WisataAdapter adapter;
    JSONParser jParser = new JSONParser();
    ListView listView;
    ProgressDialog pDialog;
    JSONArray string_json = null;
    static final int tampil_error=1;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wisata);

        if (cek_internet(this))
        {
            new AmbilDataWisata().execute();
                 }
        else {
            TextView konek = (TextView) findViewById(R.id.konek);
            konek.setText("Tidak ada koneksi internet. " +
                    " \n Silahkan, cek koneksi internet kamu dan coba lagi.");
            TextView load = (TextView) findViewById(R.id.load);
            load.setText("Muat ulang");
            load.setOnClickListener(new View.OnClickListener() {

                public void onClick(View v) {
                    Intent intent = new Intent(getApplicationContext(), Wisata.class);
                    startActivity(intent);
                }
            });
            showDialog(tampil_error);
        }
        listView = (ListView) findViewById(R.id.list);
        listView.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                Intent intent = new Intent(Wisata.this, DetailWisata.class);
                intent.putExtra("nama", (String) ((HashMap) Wisata.this.DaftarWisata.get(position)).get("nama"));
                Wisata.this.startActivity(intent);
            }
        });
    }

    private class AmbilDataWisata extends AsyncTask<String, String, String> {
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(Wisata.this);
            pDialog.setMessage("Mohon tunggu...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        protected String doInBackground(String... args) {
            try {
                JSONObject json = Wisata.this.jParser.makeHttpRequest(serverlink.url_wisata_json, "GET", new ArrayList<NameValuePair>());
                string_json = json.getJSONArray("wisata");
                Log.d("Select Data", "" + json);
                runOnUiThread(new Runnable() {
                    public void run() {
                        int i = 0;
                        while (i < string_json.length()) {
                            try {
                                JSONObject c = string_json.getJSONObject(i);
                                String id = c.getString(TAG_ID).trim();
                                String nama = c.getString(TAG_NAMA).trim();
                                String alamat = c.getString(TAG_ALAMAT).trim();
                                String link_image = c.getString(TAG_GAMBAR).trim();

                                HashMap<String, String> map = new HashMap<>();

                                map.put(TAG_ID, id);
                                map.put(TAG_NAMA, nama);
                                map.put(TAG_ALAMAT, alamat);
                                map.put(TAG_GAMBAR, link_image);

                                DaftarWisata.add(map);
                                i++;
                            } catch (JSONException e) {
                                e.printStackTrace();
                                return;
                            }
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
            SetListViewAdapter(DaftarWisata);
        }
    }

    public void SetListViewAdapter(ArrayList<HashMap<String, String>> wisata) {
        adapter = new WisataAdapter(this, wisata);
        listView.setAdapter(adapter);
    }
        public boolean cek_internet (Context cek) {
        ConnectivityManager cm = (ConnectivityManager) cek.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = cm.getActiveNetworkInfo();
        return info != null && info.isConnected();
    }

    @Override
    public boolean  onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed(){
        Intent intent = new Intent(this, MainActivity.class);
        finish();
        startActivity(intent);
    }
}
