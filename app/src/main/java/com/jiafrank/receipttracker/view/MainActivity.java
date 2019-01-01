package com.jiafrank.receipttracker.view;

import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.jiafrank.receipttracker.data.Receipt;
import com.jiafrank.receipttracker.service.ImageService;
import com.jiafrank.receipttracker.R;
import com.jiafrank.receipttracker.data.Category;
import com.jiafrank.receipttracker.data.RealmConfig;
import com.jiafrank.receipttracker.view.adapter.ReceiptListAdapter;

import java.io.File;
import java.io.IOException;

import io.realm.Realm;
import io.realm.Sort;

public class MainActivity extends AppCompatActivity {

    public static final int REQUEST_IMAGE_CAPTURE = 1;
    public static final int ADD_NEW_RECEIPT = 2;
    private static final String LOGTAG = "MainActivity";

    private Realm realm;
    private ImageService imageService = new ImageService();

    // Gets passed to AddReceiptActivity
    private String newPhotoId;

    // UI Elements
    private RecyclerView recyclerView;
    private FloatingActionButton addItemButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initializeRealm();
        setUpUI();

    }

    /**
     * Need to close the realm when the view gets destroyed
     */
    @Override
    protected void onDestroy() {
        realm.close();
        super.onDestroy();
    }

    private void setUpUI() {

        // Set up action button
        addItemButton = findViewById(R.id.addItemButton);
        addItemButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dispatchTakePictureIntent();
            }
        });

        // Set up recycler
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        ReceiptListAdapter receiptListAdapter = new ReceiptListAdapter(realm.where(Receipt.class).findAll().sort("time", Sort.DESCENDING));
        recyclerView.setAdapter(receiptListAdapter);
    }

    /**
     * Image Taking Functionality
     */
    private void dispatchTakePictureIntent() {

        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {

            // Create a photo file to save the new photo
            File photoFile = null;
            try {
                photoFile = imageService.getNewImageFile(this);
                newPhotoId = photoFile.getName();
            } catch (IOException ex) {
                Log.e(LOGTAG, "Could not make a new photo file");
                Snackbar.make(findViewById(R.id.mainActivityRootView), "Something Went Wrong", Snackbar.LENGTH_SHORT);
            }

            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "com.jiafrank.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }

        }

    }

    /**
     * Actions to take when we come back from taking a picture or from adding a new receipt
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {

            // Launch into new activity
            Intent addReceiptIntent = new Intent(MainActivity.this, AddReceiptActivity.class);
            // Maybe just use ADD_NEW_RECEIPT
            addReceiptIntent.putExtra(AddReceiptActivity.ID_STRING_INTENT_NAME, newPhotoId);
            startActivityForResult(addReceiptIntent, ADD_NEW_RECEIPT);

            Log.d(LOGTAG, "Take image successful");

        } else if (requestCode == ADD_NEW_RECEIPT && resultCode == RESULT_OK) {

            Log.d(LOGTAG, "Add New Receipt Successful");

        } else if (requestCode == ADD_NEW_RECEIPT) {

            Log.i(LOGTAG, "Add New Receipt Failed");
            Snackbar.make(findViewById(R.id.mainActivityRootView), "Something Went Wrong", Snackbar.LENGTH_SHORT);

        }
            Log.d(LOGTAG, "User cancelled take image");
    }

    /**
     * Set the default realm
     */
    private void initializeRealm() {
        Realm.init(this);
        Realm.setDefaultConfiguration(RealmConfig.getDefaultDatabaseConfig());
        realm = Realm.getDefaultInstance();
    }

}
