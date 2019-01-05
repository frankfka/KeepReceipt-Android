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

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.jiafrank.keepreceipt.R;
import com.jiafrank.keepreceipt.data.Receipt;
import com.jiafrank.keepreceipt.service.ImageService;
import com.jiafrank.keepreceipt.service.TextFormatService;
import com.jiafrank.keepreceipt.service.UIService;

import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import io.realm.Realm;

import static com.jiafrank.keepreceipt.Constants.ACTIVITY_ACTION_CREATE;
import static com.jiafrank.keepreceipt.Constants.ACTIVITY_ACTION_EDIT;
import static com.jiafrank.keepreceipt.Constants.ACTIVITY_ACTION_INTENT_NAME;
import static com.jiafrank.keepreceipt.Constants.ID_STRING_INTENT_NAME;
import static com.jiafrank.keepreceipt.service.UIService.DISMISS_ALERT_DIALOG_LISTENER;

public class AddOrEditReceiptActivity extends AppCompatActivity {

    private static String LOGTAG = "AddOrEditReceiptActivity";

    // Receipt ID is just the file name
    private String receiptId = null;
    private Receipt receiptIfEditing = null;
    private int activityAction;

    // UI elements
    private TextInputEditText vendorNameInput;
    private TextInputLayout vendorNameInputLayout;
    private TextInputEditText priceInput;
    private TextInputLayout priceInputLayout;
    private TextInputEditText dateInput;
    private Button submitButton;
    private ImageView receiptImageView;
    private ActionBar actionBar;

    // Inputs
    private double statedPrice;
    private String statedVendorName = "";
    private Calendar statedCalendar = Calendar.getInstance(Locale.getDefault());

    // TODO VALIDATION ON FIELDS

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
            UIService.finishActivity(this,false);
        }

        // Initialize state variables if we're editing
        if (activityAction == ACTIVITY_ACTION_EDIT) {

            try (Realm realm = Realm.getDefaultInstance()) {
                receiptIfEditing = realm.where(Receipt.class).equalTo("receiptId", possiblyNullId).findFirst();
            }
            if (null == receiptIfEditing) {
                Log.e(LOGTAG, "Could not find a receipt object with the ID ".concat(receiptId));
                UIService.finishActivity(this, false);
            }

            statedCalendar.setTime(receiptIfEditing.getTransactionTime());
            statedPrice = receiptIfEditing.getAmount();
            statedVendorName = receiptIfEditing.getVendor();

        }

        // Initialize all the views
        actionBar = getSupportActionBar();
        vendorNameInput = findViewById(R.id.receiptVendorNameInput);
        vendorNameInputLayout = findViewById(R.id.receiptVendorNameInputLayout);
        priceInput = findViewById(R.id.receiptPriceInput);
        priceInputLayout = findViewById(R.id.receiptPriceInputLayout);
        dateInput = findViewById(R.id.receiptDateInput);
        submitButton = findViewById(R.id.submit_button);
        receiptImageView = findViewById(R.id.largeReceiptImageView);
        setUpUI();

    }

    /**
     * Options Menu for Delete
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Only show delete button if adding new receipt

        if (activityAction == ACTIVITY_ACTION_CREATE) {

            getMenuInflater().inflate(R.menu.add_edit_receipt_menu, menu);

            // Set up delete reference - onclick will be handled in setUpUI()
            MenuItem deleteButton = menu.findItem(R.id.action_edit_receipt_delete);

            // Define alert dialog listeners
            final AlertDialog.OnClickListener positiveDialogListener = new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    UIService.finishActivity(AddOrEditReceiptActivity.this,false);
                }
            };

            deleteButton.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {

                    // TODO extract as string resources
                    UIService.getAlertDialog(AddOrEditReceiptActivity.this, "Cancel Add Receipt",
                            "Are you sure you want to cancel?",
                            "Yes", positiveDialogListener,
                            "No", DISMISS_ALERT_DIALOG_LISTENER).show();

                    return true;
                }
            });

        }

        return super.onCreateOptionsMenu(menu);
    }

    private void setUpUI() {

        // Show the receipt image regardless
        Glide.with(this)
                .load(ImageService.getImageFile(receiptId, this))
                .apply(RequestOptions.centerCropTransform())
                .into(receiptImageView);
        // Show stated date ( will be current date for create action )
        dateInput.setText(TextFormatService.getFormattedStringFromDate(statedCalendar.getTime(), true));
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

                    if(validateInputsOrShowError()) {
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
                            UIService.finishActivity(AddOrEditReceiptActivity.this,true);
                        }
                    }

                }
            });

        } else if (activityAction == ACTIVITY_ACTION_EDIT) {

            actionBar.setTitle("Edit Receipt");
            priceInput.setText(String.format("%.2f", statedPrice));
            vendorNameInput.setText(statedVendorName);

            submitButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if(validateInputsOrShowError()) {
                        statedPrice = Double.valueOf(priceInput.getText().toString());
                        statedVendorName = vendorNameInput.getText().toString();

                        // Persist
                        try (Realm realm = Realm.getDefaultInstance()) {

                            realm.beginTransaction();
                            receiptIfEditing.setAmount(statedPrice);
                            receiptIfEditing.setVendor(statedVendorName);
                            receiptIfEditing.setTransactionTime(statedCalendar.getTime());
                            realm.commitTransaction();

                            // Go back to previous activity
                            UIService.finishActivity(AddOrEditReceiptActivity.this,true);
                        }
                    }

                }
            });

        } else {
            Log.e(LOGTAG, "Activity action identifier was not valid");
            UIService.finishActivity(this,false);
        }

    }

    /**
     * Returns true if everything is OK
     */
    private boolean validateInputsOrShowError() {
        boolean error = false;
        if (priceInput.getText().toString().isEmpty()) {
            // TODO extract to string resource
            priceInputLayout.setErrorEnabled(true);
            priceInputLayout.setError("Please enter a price");
            error = true;
        } else {
            priceInputLayout.setError(null);
            priceInputLayout.setErrorEnabled(false);
        }
        if (vendorNameInput.getText().toString().isEmpty()) {
            vendorNameInputLayout.setErrorEnabled(true);
            vendorNameInputLayout.setError("Please enter a vendor");
            error = true;
        } else {
            vendorNameInputLayout.setErrorEnabled(false);
            vendorNameInputLayout.setError(null);
        }
        return !error;
    }

}
