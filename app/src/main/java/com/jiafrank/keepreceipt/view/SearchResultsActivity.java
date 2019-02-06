package com.jiafrank.keepreceipt.view;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import io.realm.Case;
import io.realm.Realm;
import io.realm.RealmQuery;
import io.realm.RealmResults;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.jiafrank.keepreceipt.R;
import com.jiafrank.keepreceipt.data.Category;
import com.jiafrank.keepreceipt.data.Receipt;
import com.jiafrank.keepreceipt.view.adapter.ReceiptListAdapter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import static com.jiafrank.keepreceipt.Constants.SEARCH_CATEGORIES_INTENT_NAME;
import static com.jiafrank.keepreceipt.Constants.SEARCH_DATE_FORMAT;
import static com.jiafrank.keepreceipt.Constants.SEARCH_KEYWORDS_INTENT_NAME;
import static com.jiafrank.keepreceipt.Constants.SEARCH_MAX_DATE_INTENT_NAME;
import static com.jiafrank.keepreceipt.Constants.SEARCH_MAX_PRICE_INTENT_NAME;
import static com.jiafrank.keepreceipt.Constants.SEARCH_MIN_DATE_INTENT_NAME;
import static com.jiafrank.keepreceipt.Constants.SEARCH_MIN_PRICE_INTENT_NAME;

public class SearchResultsActivity extends AppCompatActivity {

    private static String LOGTAG = "SearchResultsActivity";

    // UI Views
    private RecyclerView searchResultsRecycler;
    private TextView noResultsText;

    // Query Params
    private Date maxStatedDate;
    private Date minStatedDate;
    private ArrayList<String> statedCategoryStrings;
    private String statedKeywords;
    private Double statedMaxPrice;
    private Double statedMinPrice;

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

        // Get dates
        String maxDateString = inputBundle.getString(SEARCH_MAX_DATE_INTENT_NAME);
        String minDateString = inputBundle.getString(SEARCH_MIN_DATE_INTENT_NAME);
        try {
            maxStatedDate = maxDateString == null ? null : (new SimpleDateFormat(SEARCH_DATE_FORMAT, Locale.getDefault())).parse(maxDateString);
        } catch (ParseException e) {
            Log.e(LOGTAG, "Error parsing date string: ".concat(maxDateString));
        }
        try {
            minStatedDate = minDateString == null ? null : (new SimpleDateFormat(SEARCH_DATE_FORMAT, Locale.getDefault())).parse(minDateString);
        } catch (ParseException e) {
            Log.e(LOGTAG, "Error parsing date string: ".concat(minDateString));
        }

        // General search
        statedCategoryStrings = inputBundle.getStringArrayList(SEARCH_CATEGORIES_INTENT_NAME);        // if this is empty or null, we'll search everything
        statedKeywords = inputBundle.getString(SEARCH_KEYWORDS_INTENT_NAME);

        // Prices - we pass everything as strings so we know what queries were entered and which weren't
        String maxPriceString = inputBundle.getString(SEARCH_MAX_PRICE_INTENT_NAME);
        statedMaxPrice = maxPriceString == null ? null : Double.valueOf(maxPriceString);
        String minPriceString = inputBundle.getString(SEARCH_MIN_PRICE_INTENT_NAME);
        statedMinPrice = minPriceString == null ? null : Double.valueOf(minPriceString);

    }

    private void getQueryResults() {
        try (Realm realm = Realm.getDefaultInstance()) {
            RealmQuery inputQuery = realm.where(Receipt.class);

            // General
            if (statedKeywords != null) {
                inputQuery = inputQuery.contains(getString(R.string.REALM_receipt_vendor), statedKeywords, Case.INSENSITIVE);
            }
            if (statedCategoryStrings != null && !statedCategoryStrings.isEmpty()) {
                for (int i = 0; i < statedCategoryStrings.size(); i++) {
                    if (i != 0) {
                        inputQuery = inputQuery.or();
                    }
                    inputQuery = inputQuery.equalTo(getString(R.string.REALM_parent_category_name), statedCategoryStrings.get(i));
                }
            }

            Log.e(LOGTAG, inputQuery.getDescription());
            Log.e(LOGTAG, String.valueOf(inputQuery.findAll().size()));
            receiptsToShow = inputQuery.findAll();
        }
    }

    private void setUpUI() {
        if (receiptsToShow.isEmpty()) {
            // Show no results
            noResultsText.setVisibility(View.VISIBLE);
            searchResultsRecycler.setVisibility(View.GONE);
        } else {
            searchResultsRecycler.setLayoutManager(new LinearLayoutManager(this));
            searchResultsRecycler.setHasFixedSize(true);
            receiptListAdapter = new ReceiptListAdapter(receiptsToShow);
            searchResultsRecycler.setAdapter(receiptListAdapter);
            noResultsText.setVisibility(View.GONE);
            searchResultsRecycler.setVisibility(View.VISIBLE);
        }
    }

}
