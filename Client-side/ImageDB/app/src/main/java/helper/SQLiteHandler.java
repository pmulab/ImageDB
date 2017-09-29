package helper;

/**
 * This class takes care of storing the user data in SQLite database. Whenever we needs to get the logged in user information, we fetch from SQLite instead of making request to server.
 */

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;

import app.AppConfig;


public class SQLiteHandler extends SQLiteOpenHelper {

    private static final String TAG = SQLiteHandler.class.getSimpleName();

    //  All Static variables
    //  Database Version
    private static final int DATABASE_VERSION = 1;

    //  Database Name
    private static final String DATABASE_NAME = "android_api";


    //  Login table name
    private static final String TABLE_USER = "user";
    //  Login Table Columns names
    private static final String KEY_ID = "id";
    private static final String KEY_NAME = "name";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_UID = "uid";
    private static final String KEY_CREATED_AT = "created_at";


    //  Settings table name
    private static final String TABLE_SETTINGS = "settings";
    //  Settings Table Columns names
    private static final String KEY_SERVER_URL = "server_url";


    //  Cache table name
    private static final String TABLE_CACHE_GALLERY = "gallery_cache";
    private static final String TABLE_CACHE_TOP = "top_cache";
    //  Cache table columns names
    private static final String TABLE_CACHE_KEY_IMAGE_ID = "ImageID";
    private static final String TABLE_CACHE_KEY_IMAGE_NAME = "ImageName";
    private static final String TABLE_CACHE_KEY_IMAGE_PATH = "ImagePath";
    private static final String TABLE_CACHE_KEY_AUTHOR_UNIQUE_USER_ID = "AuthorUniqueUserId";
    private static final String TABLE_CACHE_KEY_CREATED_AT = "CreatedAt";
    private static final String TABLE_CACHE_KEY_RATING = "rating";


    //  Image cache table name
    private static final String TABLE_IMAGE_CACHE = "image_cache";
    //  Image cache table column name
    private static final String TABLE_IMAGE_CACHE_KEY_IMAGE_PATH = "image_path";
    private static final String TABLE_IMAGE_CACHE_KEY_BITMAP = "image_bitmap";


    //  DB Context
    public SQLiteHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    //  Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {
        //login table
        String CREATE_LOGIN_TABLE = "CREATE TABLE " + TABLE_USER + "("
                + KEY_ID + " INTEGER PRIMARY KEY," + KEY_NAME + " TEXT,"
                + KEY_EMAIL + " TEXT UNIQUE," + KEY_UID + " TEXT,"
                + KEY_CREATED_AT + " TEXT" + ")";
        db.execSQL(CREATE_LOGIN_TABLE);

        //settings table
        String CREATE_SETTINGS_TABLE = "CREATE TABLE " + TABLE_SETTINGS + "("
                + KEY_SERVER_URL + " TEXT" + ")";
        db.execSQL(CREATE_SETTINGS_TABLE);

        //gallery cache table
        String CREATE_CACHE_GALLERY_TABLE = "CREATE TABLE " + TABLE_CACHE_GALLERY + "("
                + TABLE_CACHE_KEY_IMAGE_ID + " TEXT PRIMARY KEY,"   //+ TABLE_CACHE_KEY_IMAGE_ID + " INTEGER PRIMARY KEY,"
                + TABLE_CACHE_KEY_IMAGE_NAME + " TEXT,"
                + TABLE_CACHE_KEY_IMAGE_PATH + " TEXT UNIQUE,"
                + TABLE_CACHE_KEY_AUTHOR_UNIQUE_USER_ID + " TEXT,"
                + TABLE_CACHE_KEY_CREATED_AT + " TEXT,"             //+ TABLE_CACHE_KEY_CREATED_AT + " DATE,"
                + TABLE_CACHE_KEY_RATING + " TEXT" + ")";           //+ TABLE_CACHE_KEY_RATING + " INTEGER" + ")";
        db.execSQL(CREATE_CACHE_GALLERY_TABLE);

        //top cache table
        String CREATE_CACHE_TOP_TABLE = "CREATE TABLE " + TABLE_CACHE_TOP + "("
                + TABLE_CACHE_KEY_IMAGE_ID + " TEXT PRIMARY KEY,"    //+ TABLE_CACHE_KEY_IMAGE_ID + " INTEGER PRIMARY KEY,"
                + TABLE_CACHE_KEY_IMAGE_NAME + " TEXT,"
                + TABLE_CACHE_KEY_IMAGE_PATH + " TEXT UNIQUE,"
                + TABLE_CACHE_KEY_AUTHOR_UNIQUE_USER_ID + " TEXT,"
                + TABLE_CACHE_KEY_CREATED_AT + " TEXT,"
                + TABLE_CACHE_KEY_RATING + " TEXT" + ")";        //+ TABLE_CACHE_KEY_RATING + " INTEGER" + ")";
        db.execSQL(CREATE_CACHE_TOP_TABLE);

        //image cache table
        String CREATE_IMAGE_CACHE_TABLE = "CREATE TABLE " + TABLE_IMAGE_CACHE + "("
                + TABLE_IMAGE_CACHE_KEY_IMAGE_PATH + " TEXT PRIMARY KEY,"
                + TABLE_IMAGE_CACHE_KEY_BITMAP + " BLOB" + ")";
        db.execSQL(CREATE_IMAGE_CACHE_TABLE);


        Log.d(TAG, "Database tables created");
    }

    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USER);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SETTINGS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CACHE_GALLERY);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CACHE_TOP);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_IMAGE_CACHE);

        // Create tables again
        onCreate(db);
    }

    /**
     * Storing user details in database
     */
    public void addUser(String name, String email, String uid, String created_at) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_NAME, name); // Name
        values.put(KEY_EMAIL, email); // Email
        values.put(KEY_UID, uid); // Email
        values.put(KEY_CREATED_AT, created_at); // Created At

        // Inserting Row
        long id = db.insert(TABLE_USER, null, values);
        db.close(); // Closing database connection

        Log.d(TAG, "New user inserted into sqlite: " + id);
    }

    /**
     * Getting user data from database
     */
    public HashMap<String, String> getUserDetails() {
        HashMap<String, String> user = new HashMap<String, String>();
        String selectQuery = "SELECT  * FROM " + TABLE_USER;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        // Move to first row
        cursor.moveToFirst();
        if (cursor.getCount() > 0) {
            user.put("name", cursor.getString(1));
            user.put("email", cursor.getString(2));
            user.put("uid", cursor.getString(3));
            user.put("created_at", cursor.getString(4));
        }
        cursor.close();
        db.close();
        Log.d(TAG, "Fetching user from Sqlite: " + user.toString());
        return user;
    }

    /**
     * Re crate database Delete all tables and create them again
     */
    public void deleteUsers() {
        SQLiteDatabase db = this.getWritableDatabase();
        // Delete All Rows
        db.delete(TABLE_USER, null, null);
        db.close();

        Log.d(TAG, "Deleted all user info from sqlite");
    }

    /**
     * Get Server Url from DB
     */
    public String getServerUrl() {
        String serverUrl = "near null";
        String selectQuery = "SELECT  * FROM " + TABLE_SETTINGS;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        // Move to first row
        cursor.moveToFirst();
        if (cursor.getCount() > 0) {
            serverUrl = cursor.getString(0);
        }
        cursor.close();
        db.close();
        Log.d(TAG, "Fetching user from Sqlite: " + serverUrl.toString());
        return serverUrl;
    }

    /**
     * Storing Server Url in database
     */
    public void addServerUrlAddress(String serverUrlAddress) {
        SQLiteDatabase db = this.getWritableDatabase();

        // (1) clear old value
        String selectQuery = "SELECT  * FROM " + TABLE_SETTINGS;
        Cursor cursor = db.rawQuery(selectQuery, null);
        cursor.moveToFirst();
        if (cursor.getCount() > 0) {
            db.delete(TABLE_SETTINGS, null, null);
        }
        cursor.close();

        // (2) Write new value
        ContentValues values = new ContentValues();
        values.put(KEY_SERVER_URL, serverUrlAddress); // server url
        // Inserting Row
        long id = db.insert(TABLE_SETTINGS, null, values);
        db.close(); // Closing database connection

        Log.d(TAG, "New server url inserted into sqlite: " + serverUrlAddress);
    }

    /**
     * Storing Gallery Cache Data in database
     */
    public void addGalleryCache(ArrayList<HashMap<String, String>> galleryCacheData, String MODE) {
        SQLiteDatabase db = this.getWritableDatabase();

        // (1) clear old value
        String selectQuery = "near null";
        String targetTable = "near null";
        if (MODE.equals(AppConfig.GALLERY_MODE_GALLERY)) {
            selectQuery = "SELECT  * FROM " + TABLE_CACHE_GALLERY;
            targetTable = TABLE_CACHE_GALLERY;
        } else if (MODE.equals(AppConfig.GALLERY_MODE_TOP)) {
            selectQuery = "SELECT  * FROM " + TABLE_CACHE_TOP;
            targetTable = TABLE_CACHE_TOP;
        }
        Cursor cursor = db.rawQuery(selectQuery, null);
        cursor.moveToFirst();
        if (cursor.getCount() > 0) {
            db.delete(targetTable, null, null);
        }
        cursor.close();

        // (2) Write new value
        for (int i = 0; i < galleryCacheData.size(); i++) {
            HashMap<String, String> galleryCacheRowData = galleryCacheData.get(i);

            ContentValues value = new ContentValues();
            value.put(TABLE_CACHE_KEY_IMAGE_ID, galleryCacheRowData.get("ImageID"));
            value.put(TABLE_CACHE_KEY_IMAGE_NAME, galleryCacheRowData.get("ImageName"));
            value.put(TABLE_CACHE_KEY_IMAGE_PATH, galleryCacheRowData.get("ImagePath"));
            value.put(TABLE_CACHE_KEY_AUTHOR_UNIQUE_USER_ID, galleryCacheRowData.get("AuthorUniqueUserId"));
            value.put(TABLE_CACHE_KEY_CREATED_AT, galleryCacheRowData.get("CreatedAt"));
            value.put(TABLE_CACHE_KEY_RATING, galleryCacheRowData.get("rating"));

            // Inserting Row
            long id = db.insert(targetTable, null, value);
        }
        db.close(); // Closing database connection

        Log.d(TAG, "New Gallery Cache inserted into sqlite. Table : " + targetTable.toString());
    }


    /**
     * Get Gallery Cache Data from DB
     */
    public ArrayList<HashMap<String, String>> getGalleryCache(String MODE) {
        ArrayList<HashMap<String, String>> galleryCacheData = new ArrayList<HashMap<String, String>>();
        String selectQuery = "near null";
        if (MODE.equals(AppConfig.GALLERY_MODE_GALLERY)) {
            selectQuery = "SELECT  * FROM " + TABLE_CACHE_GALLERY;
        } else if (MODE.equals(AppConfig.GALLERY_MODE_TOP)) {
            selectQuery = "SELECT  * FROM " + TABLE_CACHE_TOP;
        }

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        // Move to first row
        if (cursor.moveToFirst()) {
            do {
                int st = cursor.getCount();
                if (cursor.getCount() > 0) {
                    HashMap<String, String> val = new HashMap<String, String>();
                    val.put("ImageID", cursor.getString(0));
                    val.put("ImageName", cursor.getString(1));
                    val.put("ImagePath", cursor.getString(2));
                    val.put("AuthorUniqueUserId", cursor.getString(3));
                    val.put("CreatedAt", cursor.getString(4));
                    val.put("rating", cursor.getString(5));

                    //add row
                    galleryCacheData.add(val);
                }
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();


        Log.d(TAG, "Fetching  galleryCacheData from Sqlite. Table : " + MODE);
        return galleryCacheData;
    }

    /**
     * Add Image Cache to database
     */
    public void addImageCache(String imagePath, Bitmap imageBitmap) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(TABLE_IMAGE_CACHE_KEY_IMAGE_PATH, imagePath);
        //  image bitmap to byte array
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        imageBitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] imageByteArray = stream.toByteArray();
        values.put(TABLE_IMAGE_CACHE_KEY_BITMAP, imageByteArray);

        //  Inserting Row
        long id = db.insert(TABLE_IMAGE_CACHE, null, values);
        db.close(); // Closing database connection

        Log.d(TAG, "New ImageCache inserted into sqlite: " + imagePath);
    }


    /**
     * Get Image Cache from database
     */
    public Bitmap getImageCache(String imagePath) {
        Bitmap bm = null;
        String selectQuery = "SELECT * FROM " + TABLE_IMAGE_CACHE + " WHERE " + TABLE_IMAGE_CACHE_KEY_IMAGE_PATH + " = '" + imagePath + "'";

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            byte[] imgByte = cursor.getBlob(1);
            cursor.close();
            bm = BitmapFactory.decodeByteArray(imgByte, 0, imgByte.length);
        }
        if (cursor != null && !cursor.isClosed()) {
            cursor.close();
        }
        return bm;
    }
}