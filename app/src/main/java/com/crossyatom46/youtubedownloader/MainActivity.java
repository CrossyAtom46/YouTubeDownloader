package com.crossyatom46.youtubedownloader;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.crossyatom46.youtubedl_android.BuildConfig;
import com.crossyatom46.youtubedl_android.YoutubeDL;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private Button btnStreamingExample;
    private Button btnDownloadingExample;
    private Button btnCommandExample;
    private Button btnUpdate;
    private ProgressBar progressBar;

    private boolean updating = false;
    private CompositeDisposable compositeDisposable = new CompositeDisposable();
    private static final String TAG = "MainActivity";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initViews();
        initListeners();
    }

    @Override
    protected void onDestroy() {
        compositeDisposable.dispose();
        super.onDestroy();
    }

    private void initListeners() {
        btnStreamingExample.setOnClickListener(this);
        btnDownloadingExample.setOnClickListener(this);
        btnCommandExample.setOnClickListener(this);
        btnUpdate.setOnClickListener(this);
    }

    private void initViews() {
         btnStreamingExample = findViewById(R.id.btn_streaming_example);
         btnDownloadingExample = findViewById(R.id.btn_downloading_example);
        btnCommandExample = findViewById(R.id.btn_command_experimental);
        btnUpdate = findViewById(R.id.btn_update);
        progressBar = findViewById(R.id.progress_bar);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_streaming_example: {
                Intent i = new Intent(MainActivity.this, MP4DownloadActivity.class);
                startActivity(i);
                break;
            }
            case R.id.btn_downloading_example: {
                Intent i = new Intent(MainActivity.this, MP3DownloadActivity.class);
                startActivity(i);
                break;
            }
            case R.id.btn_command_experimental: {
                Intent i = new Intent(MainActivity.this, ExperimentalSettingsActivity.class);
                startActivity(i);
                break;
            }
            case R.id.btn_update: {
                updateYoutubeDL();
                break;
            }
        }
    }

    private void updateYoutubeDL() {
        if (updating) {
            Toast.makeText(MainActivity.this, "update is already in progress", Toast.LENGTH_LONG).show();
            return;
        }

        updating = true;
        progressBar.setVisibility(View.VISIBLE);
        Disposable disposable = Observable.fromCallable(() -> YoutubeDL.getInstance().updateYoutubeDL(getApplication()))
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(status -> {
                    progressBar.setVisibility(View.GONE);
                    switch (status) {
                        case DONE:
                            Toast.makeText(MainActivity.this, "update successful", Toast.LENGTH_LONG).show();
                            break;
                        case ALREADY_UP_TO_DATE:
                            Toast.makeText(MainActivity.this, "already up to date", Toast.LENGTH_LONG).show();
                            break;
                        default:
                            Toast.makeText(MainActivity.this, status.toString(), Toast.LENGTH_LONG).show();
                            break;
                    }
                    updating = false;
                }, e -> {
                    if(BuildConfig.DEBUG) Log.e(TAG, "failed to update", e);
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(MainActivity.this, "update failed", Toast.LENGTH_LONG).show();
                    updating = false;
                });
        compositeDisposable.add(disposable);
    }
}
