package activity;

/**
 * Here we are just fetching the logged user information from SQLite and displaying it on the screen.
 * The logout button will logout the user by clearing the session and deleting the user from SQLite table.
 */

import app.AppConfig;
import helper.SQLiteHandler;
import helper.SessionManager;

import java.util.HashMap;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.nicenerd.imagedb.R;

public class MainActivity extends AppCompatActivity {

    private TextView txtName;
    private TextView txtEmail;
    private Button btnUpload;
    private Button btnGallery;
    private Button btnTop;

    private SQLiteHandler db;
    private SessionManager session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        txtName = (TextView) findViewById(R.id.name);
        txtEmail = (TextView) findViewById(R.id.email);
        btnUpload = (Button) findViewById(R.id.btnUpload);
        btnGallery = (Button) findViewById(R.id.btnGallery);
        btnTop = (Button) findViewById(R.id.btnTop);

        // SqLite database handler
        db = new SQLiteHandler(getApplicationContext());

        // session manager
        session = new SessionManager(getApplicationContext());

        if (!session.isLoggedIn()) {
            logoutUser();
        }

        // Fetching user details from sqlite
        HashMap<String, String> user = db.getUserDetails();

        String name = user.get("name");
        String email = user.get("email");

        // Displaying the user details on the screen
        txtName.setText(name);
        txtEmail.setText(email);

        //  Upload Image button click event
        btnUpload.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                uploadImage();
            }
        });

        // Logout button click event
        btnGallery.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                openGallery();
            }
        });

        // Logout button click event
        btnTop.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                openTop();
            }
        });
    }

    /**
     * Logging out the user. Will set isLoggedIn flag to false in shared preferences Clears the user data from sqlite users table
     */
    private void logoutUser() {
        session.setLogin(false);

        db.deleteUsers();

        // Launching the login activity
        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    private void uploadImage() {
        // Launching the ImageUploader activity
        Intent intent = new Intent(this, ImageUploaderActivity.class);
        startActivity(intent);
        //finish();
    }

    /**
     * open GALLERY
     */
    private void openGallery() {
        // Launching Gallery activity
        Intent intent = new Intent(this, GalleryActivity.class);
        Bundle initBundle = new Bundle();
        initBundle.putString(AppConfig.GALLERY_MODE, AppConfig.GALLERY_MODE_GALLERY);
        intent.putExtras(initBundle);
        startActivity(intent);
        //finish();
    }

    /**
     * open TOP
     */
    private void openTop() {
        // Launching Gallery activity
        Intent intent = new Intent(this, GalleryActivity.class);
        Bundle initBundle = new Bundle();
        initBundle.putString(AppConfig.GALLERY_MODE, AppConfig.GALLERY_MODE_TOP);
        intent.putExtras(initBundle);
        startActivity(intent);
        //finish();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    public void onSettingsMenuClick(MenuItem item) {
        //  Open Settings activity
        Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
        startActivity(intent);
        finish();
    }
}