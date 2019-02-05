package com.jiafrank.keepreceipt.view;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import io.realm.RealmResults;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.jiafrank.keepreceipt.R;
import com.jiafrank.keepreceipt.data.Receipt;
import com.jiafrank.keepreceipt.view.adapter.ReceiptListAdapter;

public class SearchResultsActivity extends AppCompatActivity {

    // UI Views
    private RecyclerView searchResultsRecycler;
    private TextView noResultsText;

    // Query Params

    // Results
    private RealmResults<Receipt> receiptsToShow; // Could change depending on query
    private ReceiptListAdapter receiptListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_results);

        // Initialize UI variables
        searchResultsRecycler = findViewById(R.id.recyclerView);
        noResultsText = findViewById(R.id.noSearchResultsText);
        getSupportActionBar().setTitle(getString(R.string.search_results_activity_title)); // Set Search Title

        // Initialize query params
        getQueryParams();

        // Populate results
        getQueryResults();

        // Update the UI
        setUpUI();

    }

    private void getQueryParams() {
        Bundle inputBundle = getIntent().getExtras();

    }

    private void getQueryResults() {

    }

    private void setUpUI() {
        if (receiptsToShow.isEmpty()) {
            // Show no results
            noResultsText.setVisibility(View.VISIBLE);
            searchResultsRecycler.setVisibility(View.GONE);
        } else {
            receiptListAdapter = new ReceiptListAdapter(receiptsToShow);
            searchResultsRecycler.setAdapter(receiptListAdapter);
            noResultsText.setVisibility(View.GONE);
            searchResultsRecycler.setVisibility(View.VISIBLE);
        }
    }

}
