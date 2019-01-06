package com.jiafrank.keepreceipt.view;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import io.realm.Realm;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.jiafrank.keepreceipt.Constants;
import com.jiafrank.keepreceipt.FullscreenImageActivity;
import com.jiafrank.keepreceipt.R;
import com.jiafrank.keepreceipt.data.Receipt;
import com.jiafrank.keepreceipt.service.ImageService;
import com.jiafrank.keepreceipt.service.TextFormatService;
import com.jiafrank.keepreceipt.service.UIService;

import java.util.Date;

import static com.jiafrank.keepreceipt.Constants.ACTIVITY_ACTION_CREATE;
import static com.jiafrank.keepreceipt.Constants.ACTIVITY_ACTION_EDIT;
import static com.jiafrank.keepreceipt.Constants.ACTIVITY_ACTION_INTENT_NAME;
import static com.jiafrank.keepreceipt.Constants.ADD_NEW_RECEIPT;
import static com.jiafrank.keepreceipt.Constants.EDIT_RECEIPT;
import static com.jiafrank.keepreceipt.Constants.ID_STRING_INTENT_NAME;
import static com.jiafrank.keepreceipt.service.UIService.DISMISS_ALERT_DIALOG_LISTENER;

public class ViewReceiptActivity extends AppCompatActivity {

    private static String LOGTAG = "ViewReceiptActivity";

    // State variables
    private Receipt receipt = null;

    // Services
    private ImageService imageService = new ImageService();

    // UI Elements
    private TextView vendorNameView;
    private TextView amountView;
    private TextView transactionDateView;
    private ImageView receiptImageView;
    private ActionBar actionBar;
    private ImageButton expandImageButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_receipt);

        String possiblyNullId = getIntent().getStringExtra(ID_STRING_INTENT_NAME);

        // Check if all the correct parameters were passed
        if (null != possiblyNullId) {
            try (Realm realm = Realm.getDefaultInstance()) {
                receipt = realm.where(Receipt.class).equalTo(getString(R.string.REALM_receipt_id), possiblyNullId).findFirst();
            }
            if (null == receipt) {
                Log.e(LOGTAG, "Could not find a receipt object with the ID ".concat(possiblyNullId));
                UIService.finishActivity(this, false);
            }
        } else {
            // Go back to previous activity & indicate that something failed
            Log.e(LOGTAG, "Sufficient extras were not provided");
            UIService.finishActivity(this, false);
        }

        // Initialize all the views
        actionBar = getSupportActionBar();
        vendorNameView = findViewById(R.id.statedVendorNameLabel);
        amountView = findViewById(R.id.statedAmountLabel);
        transactionDateView = findViewById(R.id.statedDateLabel);
        receiptImageView = findViewById(R.id.largeReceiptImageView);
        expandImageButton = findViewById(R.id.expandImageButton);
        setUpUI();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.view_receipt_menu, menu);

        // Set up search
        MenuItem delete = menu.findItem(R.id.action_edit_receipt_delete);
        MenuItem edit = menu.findItem(R.id.action_edit_receipt);

        edit.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                // Launch into new activity
                Intent editReceiptIntent = new Intent(ViewReceiptActivity.this, AddOrEditReceiptActivity.class);
                editReceiptIntent.putExtra(ID_STRING_INTENT_NAME, receipt.getReceiptId());
                editReceiptIntent.putExtra(ACTIVITY_ACTION_INTENT_NAME, ACTIVITY_ACTION_EDIT);
                startActivityForResult(editReceiptIntent, EDIT_RECEIPT);

                return true;
            }
        });

        delete.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {

                final AlertDialog.OnClickListener positiveDialogListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        try (Realm realm = Realm.getDefaultInstance()) {
                            realm.beginTransaction();
                            receipt.deleteFromRealm();
                            realm.commitTransaction();
                        }

                        // Delete image file
                        try {
                            ImageService.getImageFile(receipt.getReceiptId(), ViewReceiptActivity.this).delete();
                        } catch (Exception e) {
                            Log.e(LOGTAG, "Could not delete the image file", e);
                        }

                        UIService.finishActivity(ViewReceiptActivity.this, true);
                    }
                };

                UIService.getAlertDialog(ViewReceiptActivity.this, getString(R.string.delete_receipt_title),
                        getString(R.string.delete_receipt_text),
                        getString(R.string.positive_dialog_text), positiveDialogListener,
                        getString(R.string.negative_dialog_text), DISMISS_ALERT_DIALOG_LISTENER).show();

                return true;
            }
        });

        return super.onCreateOptionsMenu(menu);
    }

    private void setUpUI() {

        actionBar.setTitle(getString(R.string.view_receipt_activity_actionbar_title));

        /**
         * Testing google vision
         */
//        ImageService.runTextRecognition(BitmapFactory.decodeFile(ImageService.getImageFile(receipt.getReceiptId(), this).getAbsolutePath()));

        Glide.with(this)
                .load(ImageService.getImageFile(receipt.getReceiptId(), this))
                .apply(RequestOptions.centerCropTransform())
                .into(receiptImageView);
        vendorNameView.setText(receipt.getVendor());
        amountView.setText(TextFormatService.getFormattedCurrencyString(receipt.getAmount()));
        transactionDateView.setText(TextFormatService.getFormattedStringFromDate(receipt.getTransactionTime(), true));
        expandImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent fullScreenImageIntent = new Intent(ViewReceiptActivity.this, FullscreenImageActivity.class);
                fullScreenImageIntent.putExtra(Constants.ID_STRING_INTENT_NAME, receipt.getReceiptId());
                startActivity(fullScreenImageIntent);
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        setUpUI();
    }

}
