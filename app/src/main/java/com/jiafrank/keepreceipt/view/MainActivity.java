package com.jiafrank.keepreceipt.view;

import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import androidx.core.content.FileProvider;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.SearchView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.jiafrank.keepreceipt.data.Receipt;
import com.jiafrank.keepreceipt.service.ImageService;
import com.jiafrank.keepreceipt.R;
import com.jiafrank.keepreceipt.data.RealmConfig;
import com.jiafrank.keepreceipt.view.adapter.ReceiptListAdapter;

import java.io.File;
import java.io.IOException;

import io.realm.Realm;
import io.realm.Sort;

public class MainActivity extends AppCompatActivity {


    // TODO extract all the strings used here as static variables
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

    /**
     * Options menu stuff
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.home_screen_menu, menu);

        // Set up search
        MenuItem searchItem = menu.findItem(R.id.action_search);
        final SearchView searchView = (SearchView) searchItem.getActionView();
        searchView.setQueryHint("Enter a vendor, date, or amount");
        searchView.setMaxWidth(Integer.MAX_VALUE);
        searchItem.setOnActionExpandListener(new MenuItem.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {

                Log.e(LOGTAG, "Search Expand");
                return true;
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {

                Log.e(LOGTAG, "Search Collapse");
                return true;
            }
        });


        return super.onCreateOptionsMenu(menu);
    }

    /**
     * Set up all the UI elements
     */
    private void setUpUI() {

        // Set up title
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Receipts");

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
        ReceiptListAdapter receiptListAdapter = new ReceiptListAdapter(realm.where(Receipt.class).findAll().sort("transactionTime", Sort.DESCENDING));
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
