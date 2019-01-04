package com.jiafrank.keepreceipt.view;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import io.realm.Realm;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.jiafrank.keepreceipt.R;
import com.jiafrank.keepreceipt.data.Receipt;
import com.jiafrank.keepreceipt.service.ImageService;
import com.jiafrank.keepreceipt.service.TextFormatService;
import com.jiafrank.keepreceipt.service.UIService;

import java.util.Date;

import static com.jiafrank.keepreceipt.Constants.ID_STRING_INTENT_NAME;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_receipt);

        String possiblyNullId = getIntent().getStringExtra(ID_STRING_INTENT_NAME);

        // Check if all the correct parameters were passed
        if (null != possiblyNullId) {
            try (Realm realm = Realm.getDefaultInstance()) {
                receipt = realm.where(Receipt.class).equalTo("receiptId", possiblyNullId).findFirst();
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


                return true;
            }
        });

        return super.onCreateOptionsMenu(menu);
    }

    private void setUpUI() {

        actionBar.setTitle("Receipt");
        receiptImageView.setImageBitmap(imageService.getImageFile(receipt.getReceiptId(), this, 0, 0));
        vendorNameView.setText(receipt.getVendor());
        amountView.setText(TextFormatService.getFormattedCurrencyString(receipt.getAmount()));
        transactionDateView.setText(TextFormatService.getFormattedStringFromDate(receipt.getTransactionTime(), true));

    }

}
