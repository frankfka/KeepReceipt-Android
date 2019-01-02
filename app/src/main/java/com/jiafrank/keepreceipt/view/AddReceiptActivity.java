package com.jiafrank.keepreceipt.view;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.jiafrank.keepreceipt.R;
import com.jiafrank.keepreceipt.data.Receipt;

import java.util.Date;

import io.realm.Realm;

public class AddReceiptActivity extends AppCompatActivity {

    public static String ID_STRING_INTENT_NAME = "ID_STRING_INTENT_NAME";
    private static String LOGTAG = "AddReceiptActivity";

    // Receipt ID is just the file name
    private String receiptId = null;

    // UI elements
    private EditText editText;
    private Button submitButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_receipt);

        String possiblyNullId = getIntent().getStringExtra(ID_STRING_INTENT_NAME);
        if (null != possiblyNullId) {
            receiptId = possiblyNullId;
        } else {
            // Go back to previous activity & indicate that something failed
            Log.e(LOGTAG, "String extra to indicate ID was not passed");
            finishActivity(false);
        }

        setUpUI();

    }

    private void setUpUI() {
        editText = findViewById(R.id.editText);
        submitButton = findViewById(R.id.submitButton);
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Persist
                try (Realm realm = Realm.getDefaultInstance()) {

                    String vendorName = editText.getText().toString();

                    realm.beginTransaction();
                    Receipt receiptToAdd = realm.createObject(Receipt.class, receiptId);
                    receiptToAdd.setTime(new Date());
                    receiptToAdd.setAmount(0);
                    receiptToAdd.setVendor(vendorName);
                    realm.commitTransaction();

                    // Go back to previous activity
                    finishActivity(true);
                }

                // Go back to previous activity & indicate that something failed
                finishActivity(false);
            }
        });
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
