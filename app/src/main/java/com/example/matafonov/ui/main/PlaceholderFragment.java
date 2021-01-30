package com.example.matafonov.ui.main;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.matafonov.MainActivity;
import com.example.matafonov.R;

import java.util.Map;
import okhttp3.*;
import okhttp3.Request.Builder;

import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;

import com.bumptech.glide.Glide;

public class PlaceholderFragment extends Fragment {

    private static final String ARG_SECTION_NUMBER = "section_number";
    private static final Map<String, String> LINKS = Map.of(
            "1", "https://developerslife.ru/latest/0?json=true",
            "2", "https://developerslife.ru/top/0?json=true");

    private PageViewModel pageViewModel;
    private OkHttpClient client = new OkHttpClient();
    ImageView mImageView;

    public static PlaceholderFragment newInstance(int index) {
        PlaceholderFragment fragment = new PlaceholderFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(ARG_SECTION_NUMBER, index);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        pageViewModel = new ViewModelProvider(this).get(PageViewModel.class);
        int index = 1;
        if (getArguments() != null) {
            index = getArguments().getInt(ARG_SECTION_NUMBER);
        }
        pageViewModel.setIndex(index);
    }

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_main, container, false);
        final TextView textView = root.findViewById(R.id.section_label);
        mImageView = root.findViewById(R.id.imageView);
        /*Glide
                .with(this)
                .load("http://static.devli.ru/public/images/gifs/202009/3c2dbbe9-da67-4df3-8790-0fa3d995ceeb.gif")
                .into(mImageView);*/
        pageViewModel.getText().observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                new Thread(new Task(s)).start();
                textView.setText(s);
            }
        });
        return root;
    }

    public class Task extends Thread{
        String s;

        public Task(String s){
            this.s = s;
        }

        public void run(){
            String gifUrl = null;
            try {
                gifUrl = getGifUrl(s);
            } catch (IOException e) {
                e.printStackTrace();
            }
            Log.d("GIF Url #2", gifUrl);
            String finalGifUrl = gifUrl;
            PlaceholderFragment.this.getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                Glide
                        .with(PlaceholderFragment.this.getActivity())
                        .load(finalGifUrl)
                        .into(mImageView);
                }
            });
        }

        private String getGifUrl(@Nullable String s) throws IOException {
            String link = LINKS.get(s);
            StringBuilder gifUrl = new StringBuilder();
            Request request = new Builder().url(link).build();
            Call call = client.newCall(request);
            Response response = call.execute();
            String json = response.body().string();
            Log.d("JSON", json);
            try {
                gifUrl.append(new JSONObject(json).getJSONArray("result").getJSONObject(0).getString("gifURL"));
                Log.d("GIFUrl #1", gifUrl.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return gifUrl.toString();
        }
    }
}