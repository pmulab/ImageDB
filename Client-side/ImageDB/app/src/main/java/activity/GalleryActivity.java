package activity;
import com.nicenerd.imagedb.R;
import app.AppConfig;
import helper.SQLiteHandler;


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
import java.util.ArrayList;
import java.util.HashMap;

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

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.widget.Toast;


public class GalleryActivity extends AppCompatActivity {

    private TextView listViewName;
    SQLiteHandler db;

    @SuppressLint("NewApi")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);

        //  Parse Init Data
        Bundle initBundle = getIntent().getExtras();
        String MODE = "near null";
        if (initBundle != null) {
            MODE = initBundle.getString(AppConfig.GALLERY_MODE);
        }

        //  ui
        listViewName = (TextView) findViewById(R.id.textView1);
        //  Get SQLite DB context
        db = new SQLiteHandler(getApplicationContext());

        // Permission StrictMode
        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        // listView1
        final ListView lstView1 = (ListView) findViewById(R.id.listView1);

        String url = "";

        if (MODE.equals(AppConfig.GALLERY_MODE_GALLERY)) {
            //  Gallery.php
            url = AppConfig.getGallery_Url() + "?userID=" + db.getUserDetails().get("uid"); //get user UID
            //ui
            listViewName.setText(R.string.gallery);
        } else if (MODE.equals(AppConfig.GALLERY_MODE_TOP)) {
            //  Top.php
            url = AppConfig.getTop_Url();
            //ui
            listViewName.setText(R.string.btn_top);
        }

        //  Get Data From JSON
        final ArrayList<HashMap<String, String>> MyArrList = new ArrayList<HashMap<String, String>>();
        try {
            JSONArray data = new JSONArray(getJSONUrl(url));

            HashMap<String, String> map;

            for (int i = 0; i < data.length(); i++) {
                JSONObject c = data.getJSONObject(i);
                map = new HashMap<String, String>();
                map.put("ImageID", c.getString("id"));
                map.put("ImageName", c.getString("image_name"));
                map.put("ImagePath", c.getString("image_path"));
                map.put("AuthorUniqueUserId", c.getString("author_unique_user_id"));
                map.put("CreatedAt", c.getString("created_at"));
                map.put("rating", c.getString("rating"));
                MyArrList.add(map);
            }

            //  Update Cache
            db.addGalleryCache(MyArrList, MODE);
        } catch (JSONException e) {
            try {
                //  Get Data From Cache
                // MODE --- AppConfig.GALLERY_MODE_GALLERY /or/ AppConfig.GALLERY_MODE_TOP
                ArrayList<HashMap<String, String>> CacheDataArrList = db.getGalleryCache(MODE);

                //  Load data from cache
                if (CacheDataArrList != null && MyArrList.size() == 0) {
                    MyArrList.clear();
                    MyArrList.addAll(CacheDataArrList);
                }

                //  If cant load from server/cache
                if (CacheDataArrList == null) {
                    if (MyArrList == null) {
                        return;
                    }
                }
            } catch (Exception exp) {
                exp.printStackTrace();
            }
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        //  Init List View Adapter
        lstView1.setAdapter(new ImageAdapter(this, MyArrList));

        final AlertDialog.Builder imageDialog = new AlertDialog.Builder(this);
        final LayoutInflater inflater = (LayoutInflater) this.getSystemService(LAYOUT_INFLATER_SERVICE);

        // OnClick
        lstView1.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                View layout = inflater.inflate(R.layout.custom_fullimage_dialog, (ViewGroup) findViewById(R.id.layout_root));
                ImageView image = (ImageView) layout.findViewById(R.id.fullimage);

                try {
                    String imgPath = MyArrList.get(position).get("ImagePath");
                    image.setImageBitmap(loadBitmap(imgPath));
                } catch (Exception e) {
                    // When Error
                    image.setImageResource(android.R.drawable.ic_menu_report_image);
                }

                ///////////imageDialog.setIcon(android.R.drawable.btn_star_big_on);
                final String postID = MyArrList.get(position).get("ImageID");
                imageDialog.setTitle("View : " + MyArrList.get(position).get("ImageName"));
                imageDialog.setView(layout);
                imageDialog.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                imageDialog.setNeutralButton(R.string.open_post, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        openPostInWeb(postID);
                        dialog.dismiss();
                    }
                });

                imageDialog.create();
                imageDialog.show();
            }
        });
    }

    void openPostInWeb(String postID) {
        String postURL = AppConfig.getPost_Url() + "?postid=" + postID;

        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(postURL));
        startActivity(browserIntent);
    }

    public class ImageAdapter extends BaseAdapter {
        private Context context;
        private ArrayList<HashMap<String, String>> MyArr = new ArrayList<HashMap<String, String>>();

        private HashMap<String, Bitmap> LoadedImages = new HashMap<String, Bitmap>();

        public ImageAdapter(Context c, ArrayList<HashMap<String, String>> list) {
            // TODO Auto-generated method stub
            context = c;
            MyArr = list;
        }

        public int getCount() {
            // TODO Auto-generated method stub
            return MyArr.size();
        }

        public Object getItem(int position) {
            // TODO Auto-generated method stub
            return position;
        }

        public long getItemId(int position) {
            // TODO Auto-generated method stub
            return position;
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            // TODO Auto-generated method stub

            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            if (convertView == null) {
                convertView = inflater.inflate(R.layout.activity_gallery_column, null);
            }

            // ColImage
            ImageView imageView = (ImageView) convertView.findViewById(R.id.ColImgPath);
            String imgPath = MyArr.get(position).get("ImagePath");
            imageView.getLayoutParams().height = 200;
            imageView.getLayoutParams().width = 200;
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            if (!LoadedImages.containsKey(imgPath)) {
                try {
                    Bitmap bm = loadBitmap(imgPath);
                    imageView.setImageBitmap(bm);
                    LoadedImages.put(imgPath, bm);
                } catch (Exception e) {
                    // When Error
                    imageView.setImageResource(android.R.drawable.ic_menu_delete);
                }
            } else {
                imageView.setImageBitmap(LoadedImages.get(imgPath));
            }


            // ColPosition
            TextView txtPosition = (TextView) convertView.findViewById(R.id.ColImgID);
            txtPosition.setPadding(10, 0, 0, 0);
            txtPosition.setText("ID : " + MyArr.get(position).get("ImageID"));


            // ColPicname
            TextView txtPicName = (TextView) convertView.findViewById(R.id.ColImgDesc);
            txtPicName.setPadding(50, 0, 0, 0);
            txtPicName.setText("Name : " + MyArr.get(position).get("ImageName"));

            return convertView;
        }
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    public void onSettingsMenuClick(MenuItem item) {
        //  Open Settings activity
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
        finish();
    }
}