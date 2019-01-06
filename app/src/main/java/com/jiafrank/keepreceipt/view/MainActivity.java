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

import io.realm.Case;
import io.realm.Realm;
import io.realm.RealmQuery;
import io.realm.RealmResults;
import io.realm.Sort;

import static com.jiafrank.keepreceipt.Constants.ACTIVITY_ACTION_CREATE;
import static com.jiafrank.keepreceipt.Constants.ACTIVITY_ACTION_INTENT_NAME;
import static com.jiafrank.keepreceipt.Constants.ADD_NEW_RECEIPT;
import static com.jiafrank.keepreceipt.Constants.ID_STRING_INTENT_NAME;
import static com.jiafrank.keepreceipt.Constants.REQUEST_IMAGE_CAPTURE;

public class MainActivity extends AppCompatActivity {

    private static final String LOGTAG = "MainActivity";

    // Gets passed to AddOrEditReceiptActivity
    private String newPhotoId;

    // UI Elements
    private ActionBar actionBar;
    private RecyclerView recyclerView;
    private FloatingActionButton addItemButton;

    // State variables
    private Realm realm;
    private RealmResults<Receipt> receiptsToShow; // Could change depending on query
    private ReceiptListAdapter receiptListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize views
        actionBar = getSupportActionBar();
        recyclerView = findViewById(R.id.recyclerView);
        addItemButton = findViewById(R.id.addItemButton);

        // Recyclerview performance fixes
        recyclerView.setHasFixedSize(true);

        initializeRealm();

        // Initialize the receipt variables
        receiptsToShow = getResultsForQuery("");

        setUpUI();

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
        searchView.setQueryHint(getString(R.string.main_activity_search_hint));
        searchView.setMaxWidth(Integer.MAX_VALUE);
        searchItem.setOnActionExpandListener(new MenuItem.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {
                // No need to do anything here
                return true;
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                searchView.setQuery("", true);
                return true;
            }
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                receiptsToShow = getResultsForQuery(newText);
                if (receiptListAdapter != null) {
                    receiptListAdapter.setReceipts(receiptsToShow);
                }
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
        actionBar.setTitle(getString(R.string.main_activity_actionbar_title));

        // Set up action button
        addItemButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dispatchTakePictureIntent();
            }
        });

        // Set up recycler
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        receiptListAdapter = new ReceiptListAdapter(receiptsToShow);
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
                photoFile = ImageService.getNewImageFile(this);
                newPhotoId = photoFile.getName();
            } catch (IOException ex) {
                Log.e(LOGTAG, "Could not make a new photo file");
                Snackbar.make(findViewById(R.id.mainActivityRootView), getString(R.string.error_snackbar), Snackbar.LENGTH_SHORT);
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
            Intent addReceiptIntent = new Intent(MainActivity.this, AddOrEditReceiptActivity.class);
            addReceiptIntent.putExtra(ID_STRING_INTENT_NAME, newPhotoId);
            addReceiptIntent.putExtra(ACTIVITY_ACTION_INTENT_NAME, ACTIVITY_ACTION_CREATE);
            startActivityForResult(addReceiptIntent, ADD_NEW_RECEIPT);

            Log.d(LOGTAG, "Take image successful");

        } else if (requestCode == ADD_NEW_RECEIPT && resultCode == RESULT_OK) {

            Log.d(LOGTAG, "Add New Receipt Successful");

        } else if (requestCode == ADD_NEW_RECEIPT) {

            Log.i(LOGTAG, "Add New Receipt Failed");
            Snackbar.make(findViewById(R.id.mainActivityRootView), getString(R.string.error_snackbar), Snackbar.LENGTH_SHORT);

        } else if (requestCode == REQUEST_IMAGE_CAPTURE) {

            Log.d(LOGTAG, "Take image cancelled");

        } else {
            Log.e(LOGTAG, "Unsupported onActivityResult case");
        }
    }

    /**
     * Set the default realm
     */
    private void initializeRealm() {
        Realm.init(this);
        Realm.setDefaultConfiguration(RealmConfig.getDefaultDatabaseConfig());
        realm = Realm.getDefaultInstance();
    }

    private RealmResults<Receipt> getResultsForQuery(String query) {

        // TODO date sorting

        RealmQuery<Receipt> receiptRealmQuery = realm.where(Receipt.class).sort(getString(R.string.REALM_receipt_time), Sort.DESCENDING);
        if (query != null & !query.isEmpty()) {
            receiptRealmQuery =  receiptRealmQuery
                    .contains(getString(R.string.REALM_receipt_vendor), query, Case.INSENSITIVE);

            Double possibleAmount = null;
            try {
                possibleAmount = Double.valueOf(query.replace("$", ""));
            } catch (NumberFormatException e) {
                // Entered string is not a number
            }
            if (possibleAmount != null) {
                receiptRealmQuery = receiptRealmQuery.or().equalTo(getString(R.string.REALM_receipt_amount), possibleAmount);
            }

            return receiptRealmQuery.findAll();
        }
        return receiptRealmQuery.findAll();
    }

}
