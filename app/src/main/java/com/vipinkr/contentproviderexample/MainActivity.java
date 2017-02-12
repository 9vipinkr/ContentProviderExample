package com.vipinkr.contentproviderexample;

import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;
import java.util.jar.Manifest;

import static android.Manifest.permission.READ_CONTACTS;

public class MainActivity extends AppCompatActivity {
    List<String> contacts;
    ListView contactlist;
    int hasReadContactsPermission;
    private static final int REQUEST_CODE_READ_CONTACTS = 1;
    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        contactlist = (ListView) findViewById(R.id.contacts);

        //check whether permission is granted
        hasReadContactsPermission = ContextCompat.checkSelfPermission(MainActivity.this, READ_CONTACTS);

        if (hasReadContactsPermission != PackageManager.PERMISSION_GRANTED) {
            //request permission
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{READ_CONTACTS}, REQUEST_CODE_READ_CONTACTS);
        }
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
             /*   Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show(); */
                //check whether permission is granted
                if (ContextCompat.checkSelfPermission(MainActivity.this, READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
                    String[] projection = {ContactsContract.Contacts.DISPLAY_NAME_PRIMARY};
                    ContentResolver contentResolver = getContentResolver();
                    Cursor cursor = contentResolver.query(ContactsContract.Contacts.CONTENT_URI,
                            projection,
                            null,
                            null,
                            ContactsContract.Contacts.DISPLAY_NAME_PRIMARY);

                    if (cursor != null) {
                        contacts = new ArrayList<String>();
                        while (cursor.moveToNext()) {
                            contacts.add(cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME_PRIMARY)));
                        }
                        cursor.close();
                        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(MainActivity.this, R.layout.contact_detail, R.id.name, contacts);
                        contactlist.setAdapter(arrayAdapter);
                    }
                } else {
                    Snackbar.make(view, "To view contacts", Snackbar.LENGTH_INDEFINITE)
                            .setAction("Grant Access", new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    // true if dont show again is not checked while requesting permission
                                    if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, READ_CONTACTS)) {
                                        //request permission
                                        ActivityCompat.requestPermissions(MainActivity.this, new String[]{READ_CONTACTS}, REQUEST_CODE_READ_CONTACTS);
                                    }
                                    //redirect user to app settings page so that user can grant permission
                                    else {

                                        Intent intent = new Intent();
                                        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                        Uri uri = Uri.fromParts("package", MainActivity.this.getPackageName(), null);
                                        intent.setData(uri);
                                        MainActivity.this.startActivity(intent);

                                    }


                                }
                            }).show();
                }

            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_READ_CONTACTS:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.d(TAG, "onRequestPermissionsResult: permission granted");
                } else {
                    Log.d(TAG, "onRequestPermissionsResult: permission denied");
                }
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
