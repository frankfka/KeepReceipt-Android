package com.jiafrank.keepreceipt.view;

// TODO
// Load category list
// Selection functionality
// edit mode
// Add mode

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.jiafrank.keepreceipt.R;
import com.jiafrank.keepreceipt.data.Category;
import com.jiafrank.keepreceipt.view.adapter.CategoryListAdapter;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import io.realm.Realm;
import io.realm.RealmResults;

public class PickCategoryActivity extends AppCompatActivity {

    private static String LOGTAG = "PickCategoryActivity";

    // UI Elements
    private RecyclerView recyclerView;
    private TextView noCategoriesHintTextView;
    private ActionBar actionBar;
    private MenuItem doneSelectionButton;
    private ImageView editButton;
    private ImageView addButton;
    private ImageView doneChangesButton;

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
        editButton = findViewById(R.id.editButton);
        doneChangesButton = findViewById(R.id.doneChangesButton);
        addButton = findViewById(R.id.addButton);
        actionBar = getSupportActionBar();

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
        doneSelectionButton = menu.findItem(R.id.actionPickCategoryDone);

        // Done button will tell incoming view that something was selected
        doneSelectionButton.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {

                Log.e(LOGTAG, "done button pressed");

                return true;
            }
        });

        return super.onCreateOptionsMenu(menu);
    }

    private void setUpUI() {
        // Set up title
        actionBar.setTitle(getString(R.string.main_activity_actionbar_title));

        // Set up recycler
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        // TODO put in selectedCategory
        categoryListAdapter = new CategoryListAdapter(categories, false, null);
        recyclerView.setAdapter(categoryListAdapter);

        // Just show help text if no receipts are available
        if (categories.isEmpty()) {
            showCategoryListOrHint(false);
        } else {
            showCategoryListOrHint(true);
        }

        /*
          Bottom toolbar logic
         */
        // First set defaults
        doneChangesButton.setVisibility(View.GONE);
        addButton.setVisibility(View.GONE);

        // Listener to edit button
        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Launch into edit mode
                doneChangesButton.setVisibility(View.VISIBLE);
                addButton.setVisibility(View.VISIBLE);
                editButton.setVisibility(View.GONE);
                // Disable the save selection button while editing
                if (null != doneSelectionButton) {
                    doneSelectionButton.setEnabled(false);
                    doneSelectionButton.setIcon(R.drawable.ic_baseline_check_grey);
                }

                // Update the list items
                categoryListAdapter.setEditing(true);
            }
        });

        // Listener to done changes button
        doneChangesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doneChangesButton.setVisibility(View.GONE);
                addButton.setVisibility(View.GONE);
                editButton.setVisibility(View.VISIBLE);
                if (null != doneSelectionButton) {
                    doneSelectionButton.setEnabled(true);
                    doneSelectionButton.setIcon(R.drawable.ic_baseline_check);
                }

                // Update the list items
                categoryListAdapter.setEditing(false);
            }
        });

        // Listener to add categories
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Show dialog to add
            }
        });

    }

    private void showCategoryListOrHint(boolean recyclerVisible) {
        if (recyclerVisible) {
            recyclerView.setVisibility(View.VISIBLE);
            noCategoriesHintTextView.setVisibility(View.GONE);
        } else {
            noCategoriesHintTextView.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        }
    }

}
