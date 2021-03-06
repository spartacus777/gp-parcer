package gp.parcer.gp_parcer;

import android.Manifest;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Environment;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import gp.parcer.events.ParseEvent;
import gp.parcer.events.ParseStartedEvent;
import gp.parcer.events.ParseStoppedEvent;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_WRITE_EXTERNAL_STORAGE_LOGS = 324;

    private TextView tvResult, tvStatus;
    private ProgressBar pb;
    private ImageView ivStartStop;

    private TextView tvDownloadAsFile, tvLogAsLogs;

    private Drawable play, pause;

    private boolean onWorkDone = false;

    private boolean mBound = false;
    private ForegroundService mService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        play = getResources().getDrawable(R.drawable.baseline_play_arrow_white_36);
        pause = getResources().getDrawable(R.drawable.baseline_pause_white_36);

        ivStartStop = (ImageView) findViewById(R.id.ivStartStop);
        tvResult = (TextView) findViewById(R.id.tvResult);
        tvDownloadAsFile = (TextView) findViewById(R.id.tvDownloadAsFile);
        tvLogAsLogs = (TextView) findViewById(R.id.tvLogAsLogs);
        tvStatus = (TextView) findViewById(R.id.tvStatus);
        pb = (ProgressBar) findViewById(R.id.pb);
        ivStartStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onStartStopClicked();
            }
        });
        tvDownloadAsFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onDownloadLogsClicked();
            }
        });
        tvLogAsLogs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                printAllModels();
            }
        });

        tvResult.setText("Items: " + ModelHolder.getSize());
    }

    private void initStartBtn() {
        if (mService != null) {
            if (mService.isRunning()) {
                ivStartStop.setImageDrawable(pause);
                pb.setVisibility(View.VISIBLE);
                tvStatus.setText("Is running..");

                tvLogAsLogs.setVisibility(View.GONE);
                tvDownloadAsFile.setVisibility(View.GONE);
            } else {
                ivStartStop.setImageDrawable(play);
                pb.setVisibility(View.GONE);
                tvStatus.setText("Not running");

                tvLogAsLogs.setVisibility(View.VISIBLE);
                tvDownloadAsFile.setVisibility(View.VISIBLE);
            }
        }
    }

    private void onStartStopClicked() {
        if (mService != null) {
            if (mService.isRunning()) {
                Intent service = ForegroundService.createIntent(ForegroundService.ACTION_STOP_MINING);
                App.getContext().startService(service);

            } else {
                Intent service = ForegroundService.createIntent(ForegroundService.ACTION_START_MINING);
                App.getContext().startService(service);
            }
        }

        initStartBtn();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(ParseEvent event) {
        tvResult.setText("Items: " + event.elements);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(ParseStartedEvent event) {
        initStartBtn();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(ParseStoppedEvent event) {
        initStartBtn();
    }

    @Override
    protected void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
        // Bind to LocalService
        Intent intent = new Intent(this, ForegroundService.class);
        bindService(intent, mConnection, Context.BIND_AUTO_CREATE);

        if (mService != null) {
            initStartBtn();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
        unbindService(mConnection);
        mBound = false;
    }

    private ServiceConnection mConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className,
                                       IBinder service) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance
            ForegroundService.LocalBinder binder = (ForegroundService.LocalBinder) service;
            mService = binder.getService();
            mBound = true;

            initStartBtn();
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            mBound = false;
        }
    };


    private boolean hasPermission() {
        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_WRITE_EXTERNAL_STORAGE_LOGS);
            return false;
        } else {
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode){
            case REQUEST_WRITE_EXTERNAL_STORAGE_LOGS:
                onDownloadLogsClicked();
                break;
        }
    }

    private void onDownloadLogsClicked() {
        if (!hasPermission()) {
            return;
        }

        String filename = "logs.txt";
        try {
            File root = new File(Environment.getExternalStorageDirectory(), "Notes");
            if (!root.exists()) {
                root.mkdirs();
            }
            File gpxfile = new File(root, filename);
            FileWriter writer = new FileWriter(gpxfile);

            for (Model model : ModelHolder.getAllModels()) {
                writer.append(model.toString()).append("\n");
            }

            writer.flush();
            writer.close();

            shareFile(gpxfile);

        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Failed to create file", Toast.LENGTH_LONG).show();
        }
    }

    private void shareFile(File gpxfile){
        Intent intentShareFile = new Intent(Intent.ACTION_SEND);
        if (gpxfile.exists()) {
            intentShareFile.setType("application/txt");
            intentShareFile.putExtra(Intent.EXTRA_STREAM, Uri.parse("file://" + gpxfile.getAbsolutePath()));

            intentShareFile.putExtra(Intent.EXTRA_SUBJECT,
                    "Sharing File...");
            intentShareFile.putExtra(Intent.EXTRA_TEXT, "Sharing File...");

            startActivity(Intent.createChooser(intentShareFile, "Share File"));
        }
    }

    private void printAllModels() {
        for (Model model : ModelHolder.getAllModels()) {
            Log.d(Constants.TAG, model.toString());
        }

        Log.d(Constants.TAG, "---------------------------------------------");
    }
}
