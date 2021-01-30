package com.example.matafonov.utils;

import android.util.Log;
import android.widget.Toast;

import com.example.matafonov.models.GifRecord;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Map;

import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class ApiUtil {
    String s;
    JSONArray records;
    int number;

    private Map<String, String> links = Map.of(
            "1", "https://developerslife.ru/latest/0?json=true",
            "2", "https://developerslife.ru/top/0?json=true");
    private OkHttpClient client = new OkHttpClient();

    public ApiUtil(String s) throws IOException {
        this.s = s;
        String link = links.get(s);
        Request request = new Request.Builder().url(link).build();
        Call call = client.newCall(request);
        Response response = call.execute();
        String json = response.body().string();
        Log.d("JSON", json);
        try {
            this.records = new JSONObject(json).getJSONArray("result");
            Log.d("JSON Records", records.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        number = 0;
    }

    public GifRecord getNextGif() throws JSONException {
        String gifDesc = records.getJSONObject(number).getString("description");
        String gifUrl = records.getJSONObject(number).getString("gifURL");
        number++;
        GifRecord gr = new GifRecord(gifDesc, gifUrl);
        return gr;
    }

    public GifRecord getPreviousGif() throws JSONException {
        if (number != 0)
            number--;
        String gifDesc = records.getJSONObject(number).getString("description");
        String gifUrl = records.getJSONObject(number).getString("gifURL");
        GifRecord gr = new GifRecord(gifDesc, gifUrl);
        return gr;
    }
}