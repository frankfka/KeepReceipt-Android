package com.jiafrank.keepreceipt.view;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AlertDialog;
import androidx.core.content.FileProvider;
import androidx.appcompat.app.ActionBar;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.SearchView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

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

import static android.app.Activity.RESULT_OK;
import static com.jiafrank.keepreceipt.Constants.ACTIVITY_ACTION_CREATE;
import static com.jiafrank.keepreceipt.Constants.ACTIVITY_ACTION_INTENT_NAME;
import static com.jiafrank.keepreceipt.Constants.ADD_NEW_RECEIPT;
import static com.jiafrank.keepreceipt.Constants.ADD_RECEIPT_CHOICES;
import static com.jiafrank.keepreceipt.Constants.ADD_RECEIPT_CHOICE_IMPORT;
import static com.jiafrank.keepreceipt.Constants.ID_STRING_INTENT_NAME;
import static com.jiafrank.keepreceipt.Constants.REQUEST_IMAGE;

public class AllReceiptsFragment extends Fragment {

    private static final String LOGTAG = "AllReceiptsFragment";

    // Gets passed to AddOrEditReceiptActivity
    private String newPhotoId;
    
    // Parent variables
    private Activity parentActivity;
    private View rootView;

    // UI Elements
    private ActionBar actionBar;
    private RecyclerView recyclerView;
    private FloatingActionButton addItemButton;
    private TextView noReceiptsHintTextView;

    // State variables
    private Realm realm;
    private RealmResults<Receipt> receiptsToShow; // Could change depending on query
    private ReceiptListAdapter receiptListAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        setHasOptionsMenu(true);
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.all_receipts_fragment, container, false);
        parentActivity = getActivity();
        
        // Initialize views
        actionBar = ((HomeActivity) parentActivity).getSupportActionBar();
        recyclerView = rootView.findViewById(R.id.recyclerView);
        addItemButton = rootView.findViewById(R.id.addItemButton);
        noReceiptsHintTextView = rootView.findViewById(R.id.noReceiptsHintTextView);

        // Recyclerview performance fixes
        recyclerView.setHasFixedSize(true);

        initializeRealm();

        // Initialize the receipt variables
        receiptsToShow = getResultsForQuery("");

        setUpUI();

        return rootView;
    }

    /**
     * Options menu stuff
     */
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.home_screen_menu, menu);

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

        super.onCreateOptionsMenu(menu, inflater);
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

                // Show option to either import or take a picture
                AlertDialog.Builder builder = new AlertDialog.Builder(parentActivity);
                builder.setTitle("Add New Receipt")
                        .setItems(ADD_RECEIPT_CHOICES, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {

                            if(ADD_RECEIPT_CHOICES[which].equals(ADD_RECEIPT_CHOICE_IMPORT)) {
                                dispatchImportPictureIntent();
                            } else {
                                dispatchTakePictureIntent();
                            }

                            }
                        });

                builder.show();

            }
        });

        // Set up recycler
        recyclerView.setLayoutManager(new LinearLayoutManager(parentActivity));
        receiptListAdapter = new ReceiptListAdapter(receiptsToShow);
        recyclerView.setAdapter(receiptListAdapter);

        // Just show help text if no receipts are available
        if (receiptsToShow.isEmpty()) {
            showReceiptListOrHint(false);
        } else {
            showReceiptListOrHint(true);
        }

    }

    private void showReceiptListOrHint(boolean recyclerVisible) {
        if (recyclerVisible) {
            recyclerView.setVisibility(View.VISIBLE);
            noReceiptsHintTextView.setVisibility(View.GONE);
        } else {
            recyclerView.setVisibility(View.GONE);
            noReceiptsHintTextView.setVisibility(View.VISIBLE);
        }
    }

    /**
     * Image Taking Functionality
     */
    private void dispatchTakePictureIntent() {

        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(parentActivity.getPackageManager()) != null) {

            // Create a photo file to save the new photo
            File photoFile = null;
            try {
                photoFile = ImageService.getNewImageFile(parentActivity);
                newPhotoId = photoFile.getName();
            } catch (IOException ex) {
                Log.e(LOGTAG, "Could not make a new photo file");
                Snackbar.make(rootView.findViewById(R.id.mainActivityRootView), getString(R.string.error_snackbar), Snackbar.LENGTH_SHORT);
            }

            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(parentActivity,
                        "com.jiafrank.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_IMAGE);
            }
        }
    }

    /**
     * Image Import Functionality
     */
    private void dispatchImportPictureIntent() {
        Intent importPictureIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

        // Ensure that there's a gallery activity to handle the intent
        if (importPictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
            startActivityForResult(importPictureIntent, REQUEST_IMAGE);
        }
    }

    /**
     * Actions to take when we come back from taking a picture or from adding a new receipt
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        // Case where we ask for an image
        if (requestCode == REQUEST_IMAGE && resultCode == RESULT_OK) {

            try {

                Uri photoUri = data.getData();

                // photoURI is non-null if we've imported an image
                if (null != photoUri) {
                    Bitmap selectedImage = MediaStore.Images.Media.getBitmap(parentActivity.getContentResolver(), photoUri);
                    newPhotoId = ImageService.saveBitmapToFile(parentActivity, selectedImage).getName();
                }

                // Launch into new activity
                Intent addReceiptIntent = new Intent(this.parentActivity, AddOrEditReceiptActivity.class);
                addReceiptIntent.putExtra(ID_STRING_INTENT_NAME, newPhotoId);
                addReceiptIntent.putExtra(ACTIVITY_ACTION_INTENT_NAME, ACTIVITY_ACTION_CREATE);
                startActivityForResult(addReceiptIntent, ADD_NEW_RECEIPT);

                Log.d(LOGTAG, "Get image successful");

            } catch (IOException e) {

                Log.e(LOGTAG, "Importing an image was not successful", e);

            }

        }

        // Case where we come back from adding a new receipt
        else if (requestCode == ADD_NEW_RECEIPT && resultCode == RESULT_OK) {

            // Gets rid of help text if we're adding a first receipt
            if (recyclerView.getVisibility() == View.GONE) {
                showReceiptListOrHint(true);
            }
            Log.d(LOGTAG, "Add New Receipt Successful");

        }

        // Error cases
        else if (requestCode == ADD_NEW_RECEIPT) {

            Log.i(LOGTAG, "Add New Receipt Failed");
            Snackbar.make(rootView.findViewById(R.id.mainActivityRootView), getString(R.string.error_snackbar), Snackbar.LENGTH_SHORT);

        } else if (requestCode == REQUEST_IMAGE) {

            Log.d(LOGTAG, "Take image cancelled");

        } else {
            Log.e(LOGTAG, "Unsupported onActivityResult case");
        }

    }

    /**
     * Set the default realm
     */
    private void initializeRealm() {
        Realm.init(parentActivity);
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
