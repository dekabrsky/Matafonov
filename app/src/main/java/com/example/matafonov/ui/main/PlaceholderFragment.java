package com.example.matafonov.ui.main;

import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.matafonov.R;

import org.json.JSONException;

import java.io.IOException;

import com.bumptech.glide.Glide;
import com.example.matafonov.models.GifRecord;
import com.example.matafonov.utils.ApiUtil;

public class PlaceholderFragment extends Fragment {

    private static final String ARG_SECTION_NUMBER = "section_number";

    private PageViewModel pageViewModel;
    ImageView mImageView, mImageLoad;
    TextView mDesc;
    ApiUtil api;
    Button next, previous;

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
        mImageLoad = root.findViewById(R.id.imageLoad);
        Glide.with(this)
                .load(Uri.parse("file:///android_asset/load.gif"))
                .into(mImageLoad);
        mDesc = root.findViewById(R.id.imageDesc);
        next = root.findViewById(R.id.button2);
        previous = root.findViewById(R.id.button);
        int s = pageViewModel.getIndex();
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if (s!=1){
                        GifRecord gif = api.getNextGif();
                        fillGifCard(gif);
                    } else new Thread(new LoadTask(api)).start();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
        previous.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    GifRecord gif = api.getPreviousGif();
                    fillGifCard(gif);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
        pageViewModel.getText().observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                new Thread(new InitTask(s)).start();
                textView.setText(s);
            }
        });
        return root;
    }

    private void fillGifCard(GifRecord gif) {
        Glide
                .with(PlaceholderFragment.this.getActivity())
                .load(gif.getUrl())
                .into(mImageView);
        mDesc.setText(gif.getDescription());
        if (api.hasNext())
            next.setVisibility(View.VISIBLE);
        else next.setVisibility(View.GONE);
        if (api.hasPrevious())
            previous.setVisibility(View.VISIBLE);
        else previous.setVisibility(View.GONE);
    }

    public class InitTask extends Thread{
        String s;

        public InitTask(String s){
            this.s = s;
        }

        public void run(){
            GifRecord gif = null;
            try {
                api = new ApiUtil(s);
                gif = !s.equals("1")?  api.getNextGif() : api.getNextRandomGif();
            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }
            assert gif != null;

            GifRecord finalGif = gif;
            PlaceholderFragment.this.getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    fillGifCard(finalGif);
                }
            });
        }
    }

    public class LoadTask extends Thread{
        ApiUtil api;

        public LoadTask(ApiUtil api){
            this.api = api;
        }

        public void run(){
            try {
                GifRecord gif = api.getNextRandomGif();
                PlaceholderFragment.this.getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        fillGifCard(gif);
                    }
                });
            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }

        }
    }
}