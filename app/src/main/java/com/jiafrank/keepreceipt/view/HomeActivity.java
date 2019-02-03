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

    private ActionBar toolbar;
    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        toolbar = getSupportActionBar();
        bottomNavigationView = findViewById(R.id.bottomNav);

        // load the store fragment by default
        loadFragment(new AllReceiptsFragment());

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

                switch (menuItem.getItemId()) {
                    case R.id.navigation_all_receipts:
                        Log.e(LOGTAG,"sdf");
                        loadFragment(new AllReceiptsFragment());
                    case R.id.navigation_search:
                        loadFragment(new SearchFragment());

                }
                return true;

            }
        });
    }

    private void loadFragment(Fragment fragment) {
        // load fragment
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.homeFragmentContainer, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

}
