/*
 * Dibuat Oleh Arief Izhharuddin pada 9/17/17 5:10 AM
 * Copyright (c) 2017. All rights reserved
 *
 * Terakhir dimodifikasi 9/17/17 5:10 AM
 * username IzhharuddinArief
 */

package com.izhharuddinarief.tugasakhir.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.izhharuddinarief.tugasakhir.R;
import com.izhharuddinarief.tugasakhir.ui.Wisata;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;

public class WisataAdapter extends BaseAdapter {

    private Activity activity;
    private ArrayList<HashMap<String, String>> data;
    private static LayoutInflater inflater = null;
    public WisataAdapter(Activity a, ArrayList<HashMap<String, String>> d)
    {
        activity = a;
        data = d;
        inflater = (LayoutInflater) a
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public int getCount() {
        return this.data.size();
    }

    public Object getItem(int position) {
        return position;
    }

    public long getItemId(int position) {
        return position;
    }

    @SuppressLint("InflateParams")
    public View getView(int position, View convertView, ViewGroup parent) {

        View vi = convertView;
        if (convertView == null)
            vi = inflater.inflate(R.layout.list_wisata, null);

        TextView id = (TextView) vi.findViewById(R.id.id_wisata);
        TextView nama = (TextView) vi.findViewById(R.id.nama_wisata);

        TextView alamat = (TextView) vi.findViewById(R.id.alamat_wisata);
        ImageView thumb_image = (ImageView) vi.findViewById(R.id.gambar_wisata);


        HashMap<String, String> daftar_wisata;
        daftar_wisata = data.get(position);


        id.setText(daftar_wisata.get(Wisata.TAG_ID));
        nama.setText(daftar_wisata.get(Wisata.TAG_NAMA));

        alamat.setText(daftar_wisata.get(Wisata.TAG_ALAMAT));

        Picasso.with(activity.getApplicationContext())
                .load(daftar_wisata.get(Wisata.TAG_GAMBAR))
                .error(R.drawable.placeholder)
                .into(thumb_image);

        return vi;
    }
}
