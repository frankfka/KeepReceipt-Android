package com.jiafrank.receipttracker;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class AddReceiptActivity extends AppCompatActivity {

    private String receiptId = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_receipt);

        String possiblyNullId = getIntent().getStringExtra("temp");
        if (null != possiblyNullId) {
            receiptId = possiblyNullId;
        } else {
            // TODO Probably go back to the previous activity
        }

        // Set up textfields and all that
    }



}
