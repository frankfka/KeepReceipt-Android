package com.jiafrank.receipttracker;

import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.jiafrank.receipttracker.data.Category;
import com.jiafrank.receipttracker.data.RealmConfig;

import io.realm.Realm;

public class MainActivity extends AppCompatActivity {

    private Realm realm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Realm.init(this);
        Realm.setDefaultConfiguration(RealmConfig.getDefaultDatabaseConfig());
        realm = Realm.getDefaultInstance();

        addAllCategoryIfNotInitialized();

        FloatingActionButton addItemButton = findViewById(R.id.addItemButton);
        addItemButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.e("", "Clicked");
            }
        });

    }

    @Override
    protected void onDestroy() {
        realm.close();
        super.onDestroy();
    }

    private void addAllCategoryIfNotInitialized() {
        if (realm.where(Category.class).findAll().isEmpty()) {
            realm.beginTransaction();
            Category allCategory = realm.createObject(Category.class); // Create managed objects directly
            allCategory.setName("All");
            realm.commitTransaction();
        }
    }


}
