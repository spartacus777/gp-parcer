package gp.parcer.gp_parcer;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Binder;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import gp.parcer.events.ParseEvent;
import gp.parcer.events.ParseStartedEvent;
import gp.parcer.events.ParseStoppedEvent;

public class ForegroundService extends Service {

    public static volatile boolean isRunning = false;

    private static final int NOTIFICATION_CODE = 328;

    public static final String ACTION_START_MINING = "lsd_start_mining";
    public static final String ACTION_STOP_MINING = "lsd_stop_mining";

    private final IBinder mBinder = new LocalBinder();

    private List<Parser> parsers;

    private int parcerCount = 6;


    private Parser.ParcerCallback callback = new Parser.ParcerCallback() {
        @Override
        public void onProgressChanged(final long size) {
            EventBus.getDefault().post(new ParseEvent(size));
        }
    };

    public class LocalBinder extends Binder {
        public ForegroundService getService() {
            return ForegroundService.this;
        }
    }

    private static Intent createIntent() {
        return new Intent(App.getContext(), ForegroundService.class);
    }

    public static Intent createIntent(String action) {
        return createIntent().setAction(action);
    }


    private void startParceJob() {
        ModelHolder.init();

        parsers = new ArrayList<>(parcerCount);

        Parser parser1 = new Parser(Constants.URL1, callback);
        parser1.parse();
        parsers.add(parser1);

        Parser parser2 = new Parser(Constants.URL2, callback);
        parser2.parse();
        parsers.add(parser2);

        Parser parser3 = new Parser(Constants.URL3, callback);
        parser3.parse();
        parsers.add(parser3);

        Parser parser4 = new Parser(Constants.URL4, callback);
        parser4.parse();
        parsers.add(parser4);

        Parser parser5 = new Parser(Constants.URL5, callback);
        parser5.parse();
        parsers.add(parser5);

        Parser parser6 = new Parser(Constants.URL6, callback);
        parser6.parse();
        parsers.add(parser6);
    }

    private void stopParceJob(){
        for (Parser parser : parsers) {
            parser.stop();
        }
    }

    private void sendNotification(String text) {
        Notification notify = createNotification(text, !isRunning);
        NotificationManager nm = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        nm.notify(NOTIFICATION_CODE, notify);
    }

    public boolean isRunning() {
        return isRunning;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        if (intent != null && intent.getAction() != null && intent.getAction().equals(ACTION_START_MINING)) {
            startMiner();
        } else {
            stopMiner();
        }

        return START_STICKY;
    }

    private void startMiner() {
        isRunning = true;
        startParceJob();

        startForeground(NOTIFICATION_CODE, createNotification("Parser is running", !isRunning));

        EventBus.getDefault().post(new ParseStartedEvent());
    }

    private void stopMiner() {
        isRunning = false;
        stopParceJob();

        stopForeground(true);
        stopSelf();

        EventBus.getDefault().post(new ParseStoppedEvent());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        isRunning = false;
    }

    private Notification createNotification(String contentText, boolean isStopped) {

        Intent activityIntent = new Intent(this, MainActivity.class);
        PendingIntent piShowActivity = PendingIntent.getActivity(this, 0, activityIntent, 0);
        final Bitmap icon = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);
//        final Bitmap largeIcon = Bitmap.createScaledBitmap(icon, 128, 128, false);

        final Notification notification;
        if (isStopped) {
            notification = new NotificationCompat.Builder(this)
                    .setContentTitle("Parser stopped.")
                    .setTicker("This is a ticker")
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setStyle(new NotificationCompat.BigTextStyle().bigText(contentText))
                    .setContentText(contentText)
                    .setLargeIcon(icon)
                    .setContentIntent(piShowActivity) // back to
                    .setOngoing(true)
                    .setAutoCancel(true) // just return to activity and dismiss
                    .build();
        } else {
            Intent serviceIntent = new Intent(this, ForegroundService.class);
            serviceIntent.setAction(ACTION_STOP_MINING);
            PendingIntent piStopMining = PendingIntent.getService(this, 0, serviceIntent, 0);
            notification = new NotificationCompat.Builder(this)
                    .setContentTitle("Parser is running")
                    .setTicker("This is a ticker")
                    .setStyle(new NotificationCompat.BigTextStyle().bigText(contentText))
                    .setContentText(contentText)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setLargeIcon(icon)
                    .setContentIntent(piShowActivity) // back to activity
                    .setOngoing(true)
                    .addAction(android.R.drawable.ic_media_pause, "Stop mining", piStopMining)
                    .build();
        }

        return notification;
    }


}