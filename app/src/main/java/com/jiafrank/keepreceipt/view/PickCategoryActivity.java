package com.jiafrank.keepreceipt.view;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.jiafrank.keepreceipt.R;
import com.jiafrank.keepreceipt.data.Category;
import com.jiafrank.keepreceipt.view.adapter.CategoryListAdapter;
import com.yarolegovich.lovelydialog.LovelyTextInputDialog;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import io.realm.Case;
import io.realm.Realm;
import io.realm.RealmResults;

import static com.jiafrank.keepreceipt.Constants.PICK_CATEGORY;
import static com.jiafrank.keepreceipt.Constants.SELECTED_CATEGORY_INTENT_NAME;

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
    private Category selectedCategory;
    private Category pickedCategory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pick_category);

        // Get current selected category name (passed from intent)
        String possiblySelectedCategoryName = getIntent().getStringExtra(SELECTED_CATEGORY_INTENT_NAME);

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
        // If passed in, this object has to exist
        if (null != possiblySelectedCategoryName) {
            selectedCategory = categories.where().equalTo(getString(R.string.REALM_category_name), possiblySelectedCategoryName).findAll().first();
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
                pickedCategory = categoryListAdapter.getSelectedCategory();
                Intent returnIntent = new Intent();
                returnIntent.putExtra(SELECTED_CATEGORY_INTENT_NAME, null == pickedCategory ? null : pickedCategory.getName());
                setResult(PICK_CATEGORY, returnIntent);
                finish();
                return true;
            }

        });

        return super.onCreateOptionsMenu(menu);
    }

    private void setUpUI() {

        // Set up title
        actionBar.setTitle(getString(R.string.select_category_title));

        // Set up recycler
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        categoryListAdapter = new CategoryListAdapter(categories, false, selectedCategory);
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

            // This shows a dialog to enter & add a new category
            @Override
            public void onClick(View v) {
                new LovelyTextInputDialog(PickCategoryActivity.this)
                        .setTitle(getString(R.string.add_category_dialog_title))
                        .setMessage(getString(R.string.add_category_dialog_message))
                        .setTopColorRes(R.color.colorAccent)
                        .setIcon(R.drawable.ic_baseline_add)
                        .setInputFilter(getString(R.string.add_category_error_msg), new LovelyTextInputDialog.TextFilter() {
                            @Override
                            public boolean check(String text) {
                                return !text.isEmpty() && categories.where().equalTo("name", text, Case.INSENSITIVE).findAll().size() == 0;
                            }
                        })
                        .setConfirmButton(android.R.string.ok, new LovelyTextInputDialog.OnTextInputConfirmListener() {
                            @Override
                            public void onTextInputConfirmed(String text) {

                                // Create a new category then add it
                                try (Realm realm = Realm.getDefaultInstance()) {
                                    realm.beginTransaction();
                                    realm.createObject(Category.class, text);
                                    realm.commitTransaction();
                                }
                                // Tell adapter to reload data
                                categoryListAdapter.notifyDataSetChanged();

                            }
                        })
                        .setNegativeButton(android.R.string.cancel, null)
                        .setConfirmButtonColor(R.color.colorAccent)
                        .setNegativeButtonColor(R.color.colorAccent)
                        .show();
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
