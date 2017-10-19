/*
 * Dibuat Oleh Arief Izhharuddin pada 9/17/17 5:10 AM
 * Copyright (c) 2017. All rights reserved
 *
 * Terakhir dimodifikasi 9/17/17 5:10 AM
 * username IzhharuddinArief
 */

package com.izhharuddinarief.tugasakhir.parser;

import android.util.Log;

import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Objects;

public class JSONParser {
    private static InputStream is = null;
    private static JSONObject jObj = null;
    private static String json = "";

    public JSONObject makeHttpRequest(String url, String method, List<NameValuePair> params) {
        try {
            if (Objects.equals(method, "POST")) {
                DefaultHttpClient httpClient = new DefaultHttpClient();
                HttpPost httpPost = new HttpPost(url);
                httpPost.setEntity(new UrlEncodedFormEntity(params));
                is = httpClient.execute(httpPost).getEntity().getContent();
            } else if (Objects.equals(method, "GET")) {
                is = new DefaultHttpClient().execute(new HttpGet(url + "?" + URLEncodedUtils.format(params, "utf-8"))).getEntity().getContent();
            }
        } catch (IOException e3) {
            e3.printStackTrace();
        }
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(is, "iso-8859-1"), 8);
            StringBuilder sb = new StringBuilder();
            while (true) {
                String line = reader.readLine();
                if (line == null) {
                    break;
                }
                sb.append(line).append("\n");
            }
            is.close();
            json = sb.toString();
        } catch (Exception e4) {
            Log.e("Buffer Error", "Error converting result " + e4.toString());
        }
        try {
            jObj = new JSONObject(json);
        } catch (JSONException e5) {
            Log.e("JSON Parser", "Error parsing data " + e5.toString());
        }
        return jObj;
    }
}
