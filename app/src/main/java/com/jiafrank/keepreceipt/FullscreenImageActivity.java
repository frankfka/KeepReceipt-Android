package com.jiafrank.keepreceipt;

import androidx.appcompat.app.AppCompatActivity;
import io.realm.Realm;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.jiafrank.keepreceipt.data.Receipt;
import com.jiafrank.keepreceipt.service.ImageService;
import com.jiafrank.keepreceipt.service.UIService;

public class FullscreenImageActivity extends AppCompatActivity {

    private static String LOGTAG = "FullscreenImageActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fullscreen_image);

        String receiptId = getIntent().getStringExtra(Constants.ID_STRING_INTENT_NAME);

        if (receiptId == null) {
            Log.e(LOGTAG, "ID Extra was not provided");
            UIService.finishActivity(this, false);
        }

        // TODO use a library such as zoomage to make image zoomable
        // No action bar
        getSupportActionBar().hide();
        Glide.with(this)
                .load(ImageService.getImageFile(receiptId, this))
                .apply(RequestOptions.fitCenterTransform())
                .into((ImageView) findViewById(R.id.fullScreenImageView));
        findViewById(R.id.closeFullscreenImageButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UIService.finishActivity(FullscreenImageActivity.this, false);
            }
        });

    }
}
