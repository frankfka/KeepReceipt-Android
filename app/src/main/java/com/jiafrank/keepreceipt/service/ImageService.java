package com.jiafrank.keepreceipt.service;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ImageService {

    // Used to save files
    public static String DATE_FORMAT_PATTERN = "yyyyMMdd_HHmmss";

    /**
     * Create a new image file for which to save the picture
     */
    public static File getNewImageFile(Context context) throws IOException {

        // Image file name is same as ID - categorized by date created
        String imageFileName = new SimpleDateFormat(DATE_FORMAT_PATTERN).format(new Date());

        // Get INTERNAL storage directory - scope limited to app
        File storageDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES);

        return File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir);      /* directory */
    }

    /**
     * Get File from image ID
     */
    public static File getImageFile(String imageFileName, Context context) {
        String storageDirectory = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES).getAbsolutePath().concat("/");
        return new File(storageDirectory.concat(imageFileName));
    }
}
