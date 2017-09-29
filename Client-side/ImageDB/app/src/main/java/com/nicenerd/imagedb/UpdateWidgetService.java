package com.nicenerd.imagedb;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URL;
import java.util.HashMap;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;
import android.widget.RemoteViews;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import activity.GalleryActivity;
import app.AppConfig;
import helper.SQLiteHandler;
import layout.ImageDBWidget;


public class UpdateWidgetService extends Service {

    private final int SERVICE_UPDATE_DELAY = 3000;
    private final String LOG_TAG = "TopImageServiceLOG";

    private static final int NOTIFICATION_ID = 322;

    private Thread updateThread;
    private boolean isUpdate;
    private String currTopImagePath = "";
    private SQLiteHandler db;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        Log.d(LOG_TAG, "TopImageService onCreate");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        //  Stop update thread
        isUpdate = false;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(LOG_TAG, "TopImageService onStartCommand");

        //  Get SQLite DB context
        db = new SQLiteHandler(getBaseContext());

        //  Start Service Update Thread
        isUpdate = true;
        updateThread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (isUpdate) {
                    startJob();
                    try {
                        Thread.sleep(SERVICE_UPDATE_DELAY);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

            }
        });
        updateThread.start();
        return START_STICKY;
    }

    private void startJob() {

        //  Get remote view
        RemoteViews view = new RemoteViews(getPackageName(), R.layout.image_dbwidget);
        ComponentName theWidget = new ComponentName(this, ImageDBWidget.class);
        AppWidgetManager manager = AppWidgetManager.getInstance(this);

        //  Update top image
        Bitmap topImage = GetTopImage();
        if (topImage != null) {
            //  Send new image to widget
            view.setImageViewBitmap(R.id.imgView, topImage);
            manager.updateAppWidget(theWidget, view);
        }

        //  Update widget view
        manager.updateAppWidget(theWidget, view);
    }

    //    public void sendNotification(String contentText) {
    //        // Prepare intent which is triggered if the
    //        // notification is selected
    //        Intent intent = new Intent(this, GalleryActivity.class);
    //        //        //  Top init context
    //        Bundle initBundle = new Bundle();
    //        initBundle.putString(AppConfig.GALLERY_MODE, AppConfig.GALLERY_MODE_TOP);
    //        intent.putExtras(initBundle);
    //        PendingIntent pIntent =  PendingIntent.getActivity(this, (int) System.currentTimeMillis(), intent, 0);
    //
    //        // Build notification
    //        // Actions are just fake
    //        Notification noti = new Notification.Builder(this)
    //                .setContentTitle("ImageDB")
    //                .setContentText(contentText).setSmallIcon(android.R.drawable.alert_light_frame)
    //                .setContentIntent(pIntent)
    //                .build();
    //        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
    //        // hide the notification after its selected
    //        noti.flags |= Notification.FLAG_AUTO_CANCEL;
    //
    //        notificationManager.notify(NOTIFICATION_ID, noti);
    //    }

    //=======================================================================================

    public Bitmap GetTopImage() {
        Bitmap newImage = null;

        //  Top.php (first place)
        String url = AppConfig.getTop_Url() + "?place=1";

        //  Get Data From JSON
        final HashMap<String, String> imgInfo = new HashMap<String, String>();
        Bitmap topImageCache = null;
        try {
            JSONArray data = new JSONArray(getJSONUrl(url));

            for (int i = 0; i < data.length(); i++) {
                JSONObject c = data.getJSONObject(i);
                imgInfo.put("ImageID", c.getString("id"));
                imgInfo.put("ImageName", c.getString("image_name"));
                imgInfo.put("ImagePath", c.getString("image_path"));
                imgInfo.put("AuthorUniqueUserId", c.getString("author_unique_user_id"));
                imgInfo.put("CreatedAt", c.getString("created_at"));
                imgInfo.put("rating", c.getString("rating"));
            }
        } catch (JSONException e) {
            try {
                //  Try Get Top Image From Cache
                topImageCache = db.getImageCache(AppConfig.TOP_IMAGE);
            } catch (Exception exp) {
                exp.printStackTrace();
            }
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        String topImagePath = imgInfo.get("ImagePath");

        if (currTopImagePath.equals(topImagePath)) {
            return null;
        } else if (topImageCache == null) {
            //  Download new image
            newImage = loadBitmap(topImagePath);

            //  Notify
            //            if(!currTopImagePath.equals("")) {
            //                sendNotification("New top image!");
            //            }

            //  Save New Top Image Url
            currTopImagePath = topImagePath;

            //  Update Top Image Cache
            db.addImageCache(AppConfig.TOP_IMAGE, newImage);
        } else if (topImageCache != null) {
            newImage = topImageCache;
        }
        return newImage;
    }

    /*** Get JSON Code from URL ***/
    public String getJSONUrl(String url) {
        StringBuilder str = new StringBuilder();
        HttpClient client = new DefaultHttpClient();
        HttpGet httpGet = new HttpGet(url);
        try {
            HttpResponse response = client.execute(httpGet);
            StatusLine statusLine = response.getStatusLine();
            int statusCode = statusLine.getStatusCode();
            if (statusCode == 200) { // Download OK
                HttpEntity entity = response.getEntity();
                InputStream content = entity.getContent();
                BufferedReader reader = new BufferedReader(new InputStreamReader(content));
                String line;
                while ((line = reader.readLine()) != null) {
                    str.append(line);
                }
            } else {
                Log.e("Log", "Failed to download file..");
            }
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return str.toString();
    }

    /***** Get Image Resource from URL (Start) *****/
    private final String TAG = "ERROR";
    private final int IO_BUFFER_SIZE = 4 * 1024;

    public Bitmap loadBitmap(String url) {
        Bitmap bitmap = null;
        InputStream in = null;
        BufferedOutputStream out = null;

        //  Try load image from cache
        bitmap = db.getImageCache(url);
        //  If cant load image from cache - try load image from web
        if (bitmap == null) {
            //  Load image from web
            try {
                String imgNotLocalURLFIX = url.replace(AppConfig.DEBUG_HOST_NAME, AppConfig.URL_IMAGEDB);  //DEBUG TIME 4 localhost server
                in = new BufferedInputStream(new URL(imgNotLocalURLFIX).openStream(), IO_BUFFER_SIZE);

                final ByteArrayOutputStream dataStream = new ByteArrayOutputStream();
                out = new BufferedOutputStream(dataStream, IO_BUFFER_SIZE);
                copy(in, out);
                out.flush();

                final byte[] data = dataStream.toByteArray();
                BitmapFactory.Options options = new BitmapFactory.Options();
                //options.inSampleSize = 1;

                bitmap = BitmapFactory.decodeByteArray(data, 0, data.length, options);
                //  Add image to cache
                db.addImageCache(url, bitmap);
            } catch (IOException e) {
                Log.e(TAG, "Could not load Bitmap from: " + url);
            } finally {
                closeStream(in);
                closeStream(out);
            }
        }
        return bitmap;
    }

    private void closeStream(Closeable stream) {
        if (stream != null) {
            try {
                stream.close();
            } catch (IOException e) {
                android.util.Log.e(TAG, "Could not close stream", e);
            }
        }
    }

    private void copy(InputStream in, OutputStream out) throws IOException {
        byte[] b = new byte[IO_BUFFER_SIZE];
        int read;
        while ((read = in.read(b)) != -1) {
            out.write(b, 0, read);
        }
    }
    /***** Get Image Resource from URL (End) *****/
}