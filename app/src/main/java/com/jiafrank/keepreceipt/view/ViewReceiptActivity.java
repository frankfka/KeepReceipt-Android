package com.jiafrank.keepreceipt.view;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.jiafrank.keepreceipt.R;

public class ViewReceiptActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_receipt);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.view_receipt_menu, menu);

        // Set up search
        MenuItem delete = menu.findItem(R.id.action_edit_receipt_delete);
        MenuItem edit = menu.findItem(R.id.action_edit_receipt);

        return super.onCreateOptionsMenu(menu);
    }

}
