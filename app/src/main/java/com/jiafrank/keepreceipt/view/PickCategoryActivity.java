package com.jiafrank.keepreceipt.view;

// TODO
// Load category list
// Selection functionality
// edit mode
// Add mode

import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.jiafrank.keepreceipt.R;
import com.jiafrank.keepreceipt.data.Category;
import com.jiafrank.keepreceipt.data.Receipt;
import com.jiafrank.keepreceipt.service.ImageService;
import com.jiafrank.keepreceipt.service.UIService;
import com.jiafrank.keepreceipt.view.adapter.CategoryListAdapter;
import com.jiafrank.keepreceipt.view.adapter.ReceiptListAdapter;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import io.realm.Realm;
import io.realm.RealmResults;

import static com.jiafrank.keepreceipt.Constants.ACTIVITY_ACTION_CREATE;
import static com.jiafrank.keepreceipt.service.UIService.DISMISS_ALERT_DIALOG_LISTENER;

public class PickCategoryActivity extends AppCompatActivity {

    private static String LOGTAG = "PickCategoryActivity";

    // UI Elements
    private RecyclerView recyclerView;
    private TextView noCategoriesHintTextView;
    private ActionBar actionBar

    // State variables
    private RealmResults<Category> categories;
    private CategoryListAdapter categoryListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pick_category);

        // Initialize views
        recyclerView = findViewById(R.id.recyclerView);
        noCategoriesHintTextView = findViewById(R.id.noCategoriesHintTextView);

        // Recyclerview performance fixes
        recyclerView.setHasFixedSize(true);

        // Initialize the receipt variables
        try(Realm realm = Realm.getDefaultInstance()) {
            categories = realm.where(Category.class).findAll();
        }

        setUpUI();
    }

    /**
     * Options Menu for Delete
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.pick_category_menu, menu);

        // UI Elements
        MenuItem doneButton = menu.findItem(R.id.actionPickCategoryDone);
        MenuItem cancelButton = menu.findItem(R.id.actionPickCategoryCancel);

        // Done button will tell incoming view that something was selected
        doneButton.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {

                Log.e(LOGTAG, "done button pressed");

                return true;
            }
        });

        // Cancel button will tell incoming view to cancel
        cancelButton.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                Log.e(LOGTAG, "cancel button pressed");

                return true;
            }
        });

        return super.onCreateOptionsMenu(menu);
    }

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

        // Just show help text if no receipts are available
        if (receiptsToShow.isEmpty()) {
            showReceiptListOrHint(false);
        } else {
            showReceiptListOrHint(true);
        }
    }

}
