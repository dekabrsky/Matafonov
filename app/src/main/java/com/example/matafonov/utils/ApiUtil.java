package com.example.matafonov.utils;

import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import com.example.matafonov.models.GifRecord;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;

import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Request.Builder;
import okhttp3.Response;

public class ApiUtil {
    String s;
    JSONArray records;
    ArrayList<GifRecord> random_records = new ArrayList<>();
    int number;

    private Map<String, String> links = Map.of(
            "1", "https://developerslife.ru/random?json=true",
            "2", "https://developerslife.ru/latest/0?json=true",
            "3", "https://developerslife.ru/top/0?json=true");
    private OkHttpClient client = new OkHttpClient();

    public ApiUtil(String s) throws IOException {
        this.s = s;
        if (!s.equals("1")) {
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
        }
        number = 0;
    }

    public GifRecord getNextGif() throws JSONException {
        String gifDesc = records.getJSONObject(number).getString("description");
        String gifUrl = records.getJSONObject(number).getString("gifURL");
        number++;
        return new GifRecord(gifDesc, gifUrl);
    }

    public GifRecord getPreviousGif() throws JSONException {
        if (number != 1)
            number--;
        if (s.equals("1")){
            return random_records.get(number - 1);
        } else {
            String gifDesc = records.getJSONObject(number - 1).getString("description");
            String gifUrl = records.getJSONObject(number - 1).getString("gifURL");
            GifRecord gr = new GifRecord(gifDesc, gifUrl);
            return gr;
        }
    }

    public GifRecord getNextRandomGif() throws IOException, JSONException {
        number++;
        if (number < random_records.size()){
            return random_records.get(number - 1);
        } else {
            String link = links.get(s);
            Request request = new Request.Builder().url(link).build();
            Call call = client.newCall(request);
            Response response = call.execute();
            String json = response.body().string();
            Log.d("JSON - Random", json);
            String gifDesc = new JSONObject(json).getString("description");
            String gifUrl;
            try {
                gifUrl = new JSONObject(json).getString("gifURL");
            } catch (JSONException e){
                gifUrl = "https://i.gifer.com/3z9a.gif";
                gifDesc = "А тут гифки нет: " + gifDesc;
            }
            GifRecord gr = new GifRecord(gifDesc, gifUrl);
            random_records.add(gr);
            return gr;
        }
    }

    public GifRecord getCurrentGif() throws JSONException {
        try {
            if (s.equals("1"))
                return random_records.get(number);
            else {
                String gifDesc = records.getJSONObject(number).getString("description");
                String gifUrl = records.getJSONObject(number).getString("gifURL");
                Log.d("Current GIF", gifDesc + gifUrl);
                return new GifRecord(gifDesc, gifUrl);
            }
        } catch (Exception e){
            return new GifRecord("Такой записи нет", "https://i.gifer.com/3z9a.gif");
        }
    }

    public boolean hasNext(){
        if (!s.equals("1"))
            return records != null && number < records.length();
        else return true;
    }

    public boolean hasPrevious(){
        return number > 1;
    }

    public int getNumber(){
        return number;
    }
}
