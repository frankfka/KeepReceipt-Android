package com.jiafrank.keepreceipt.view;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.jiafrank.keepreceipt.R;

public class HomeActivity extends AppCompatActivity {

    private static String LOGTAG = "HomeActivity";

    // UI References
    private ActionBar toolbar;
    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // Assign UI references
        toolbar = getSupportActionBar();
        bottomNavigationView = findViewById(R.id.bottomNav);

        // This loads the default fragment
        loadFragment(new AllReceiptsFragment());

        // Set navigation listeners for the bottom navigation
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.navigation_all_receipts:
                        loadFragment(new AllReceiptsFragment());
                        break;
                    case R.id.navigation_search:
                        loadFragment(new SearchFragment());
                        break;
                }
                return true;

            }
        });
    }

    // Load a specific fragment
    private void loadFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.homeFragmentContainer, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

}
