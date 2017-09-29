package activity;

import com.nicenerd.imagedb.R;

import app.AppConfig;
import helper.SQLiteHandler;
import helper.SessionManager;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class SettingsActivity extends Activity {

    private EditText serverUrlTxtEdit;
    private Button btnLogout;
    private Button btnSaveSettings;

    private SQLiteHandler db;
    private SessionManager session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        serverUrlTxtEdit = (EditText) findViewById(R.id.serverUrlTxtEdit);
        btnLogout = (Button) findViewById(R.id.btnLogout);
        btnSaveSettings = (Button) findViewById(R.id.btnSaveSettings);

        // SqLite database handler
        db = new SQLiteHandler(getApplicationContext());

        // session manager
        session = new SessionManager(getApplicationContext());

        // Fetching server url from sqlite
        String serverUrl = db.getServerUrl();

        // Displaying
        serverUrlTxtEdit.setText(serverUrl);

        // Logout button click event
        if(session.isLoggedIn())
        {
            btnLogout.setVisibility(View.VISIBLE);

            btnLogout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    logoutUser();
                }
            });
        }
        else
        {
            btnLogout.setVisibility(View.GONE);
        }

        //  Save settings button click event
        btnSaveSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateAndSaveSettings();
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
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    private void updateAndSaveSettings() {
        String serverURL = serverUrlTxtEdit.getText().toString();

        //  update
        AppConfig.URL_IMAGEDB = serverURL;

        //  save
        db.addServerUrlAddress(serverURL);

        // Launching the main activity
        Intent intent = new Intent(SettingsActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

}