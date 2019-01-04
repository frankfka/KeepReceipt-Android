package com.jiafrank.keepreceipt.view;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;

import com.google.android.material.textfield.TextInputEditText;
import com.jiafrank.keepreceipt.R;
import com.jiafrank.keepreceipt.data.Receipt;
import com.jiafrank.keepreceipt.service.ImageService;
import com.jiafrank.keepreceipt.service.TextFormatService;

import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import io.realm.Realm;

public class AddOrEditReceiptActivity extends AppCompatActivity {

    public static String ID_STRING_INTENT_NAME = "ID_STRING_INTENT_NAME";
    public static String ACTIVITY_ACTION_INTENT_NAME = "ACTIVITY_ACTION_INTENT_NAME";
    public static int ACTIVITY_ACTION_EDIT = 2;
    public static int ACTIVITY_ACTION_CREATE = 1;
    private static String LOGTAG = "AddOrEditReceiptActivity";

    // Receipt ID is just the file name
    private String receiptId = null;
    private int activityAction;

    // UI elements
    private TextInputEditText vendorNameInput;
    private TextInputEditText priceInput;
    private TextInputEditText dateInput;
    private Button submitButton;
    private ImageView receiptImageView;
    private ActionBar actionBar;

    // Inputs
    private double statedPrice;
    private String statedVendorName = "";
    private Calendar statedCalendar = Calendar.getInstance(Locale.getDefault());

    // Services
    private ImageService imageService = new ImageService();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_or_edit_receipt);

        String possiblyNullId = getIntent().getStringExtra(ID_STRING_INTENT_NAME);
        int possiblyInvalidAction = getIntent().getIntExtra(ACTIVITY_ACTION_INTENT_NAME, 0);

        // Check if all the correct parameters were passed
        if (null != possiblyNullId && 0 != possiblyInvalidAction) {
            receiptId = possiblyNullId;
            activityAction = possiblyInvalidAction;
        } else {
            // Go back to previous activity & indicate that something failed
            Log.e(LOGTAG, "Sufficient extras were not provided");
            finishActivity(false);
        }

        setUpUI();

    }

    /**
     * Options Menu for Delete
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.add_edit_receipt_menu, menu);

        // Set up delete reference - onclick will be handled in setUpUI()
        MenuItem deleteButton = menu.findItem(R.id.action_edit_receipt_delete);

        if (activityAction == ACTIVITY_ACTION_CREATE) {

            // Go back to previous activity on delete click (after dialog is shown)
            deleteButton.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    showAlertDialog("Cancel Add Receipt", "Are you sure you want to cancel?",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    finishActivity(false);
                                }
                            });
                    return true;
                }
            });

        } else if (activityAction == ACTIVITY_ACTION_EDIT) {

        }


        return super.onCreateOptionsMenu(menu);
    }

    private void setUpUI() {

        actionBar = getSupportActionBar();
        vendorNameInput = findViewById(R.id.receiptVendorNameInput);
        priceInput = findViewById(R.id.receiptPriceInput);
        dateInput = findViewById(R.id.receiptDateInput);
        submitButton = findViewById(R.id.submit_button);
        receiptImageView = findViewById(R.id.largeReceiptImageView);

        // Show the receipt image regardless, scale it down to save memory
        receiptImageView.setImageBitmap(imageService.getImageFile(receiptId, this, 500, 500));
        // Initialize a date picker listener
        final DatePickerDialog.OnDateSetListener onDateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                statedCalendar.set(year, month, dayOfMonth);
                dateInput.setText(TextFormatService.getFormattedStringFromDate(statedCalendar.getTime(), true));
            }
        };
        // Initialize listener on the date input
        dateInput.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                new DatePickerDialog(AddOrEditReceiptActivity.this, onDateSetListener,
                        statedCalendar.get(Calendar.YEAR), statedCalendar.get(Calendar.MONTH), statedCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        // Now perform action specific activities
        if (activityAction == ACTIVITY_ACTION_CREATE) {

            //TODO extract as string resource
            actionBar.setTitle("Add New Receipt");
            // Persist new receipt on button click
            submitButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    statedPrice = Double.valueOf(priceInput.getText().toString());
                    statedVendorName = vendorNameInput.getText().toString();

                    // Persist
                    try (Realm realm = Realm.getDefaultInstance()) {

                        realm.beginTransaction();
                        Receipt receiptToAdd = realm.createObject(Receipt.class, receiptId);
                        receiptToAdd.setTransactionTime(new Date());
                        receiptToAdd.setAmount(statedPrice);
                        receiptToAdd.setVendor(statedVendorName);
                        receiptToAdd.setTransactionTime(statedCalendar.getTime());
                        realm.commitTransaction();

                        // Go back to previous activity
                        finishActivity(true);
                    }

                    // Go back to previous activity & indicate that something failed
                    finishActivity(false);
                }
            });

        } else if (activityAction == ACTIVITY_ACTION_EDIT) {

            //Change actionbar title
            //        // prefill all fields
            //        // revise current entry
            //        // Delete button actually deletes from realm

        } else {
            Log.e(LOGTAG, "Activity action identifier was not valid");
            finishActivity(false);
        }


//
    }

    private void showAlertDialog(String title, String message, DialogInterface.OnClickListener yesListener) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setTitle(title);
        alertDialogBuilder.setMessage(message);
        alertDialogBuilder.setPositiveButton("Yes", yesListener);
        alertDialogBuilder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        alertDialogBuilder.create().show();
    }


    /**
     * Go back to the previous activity indicating success or not
     *
     * @param succeeded whether a new receipt was added to realm
     */
    private void finishActivity(boolean succeeded) {
        if (succeeded) {
            setResult(RESULT_OK);
        } else {
            setResult(RESULT_CANCELED);
        }
        finish();
    }

}
