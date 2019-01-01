package com.jiafrank.receipttracker.service;

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
    public File getNewImageFile(Context context) throws IOException {

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
     * Retrieve bitmap image from a file name (same as receipt ID)
     *
     * @param imageFileName the file name for the receipt image, same as ID
     * @param height specify if you want a specific size for the image - both width & height must be non-zero
     * @param width specify if you want a specific size for the image - both width & height must be non-zero
     */
    public Bitmap getImageFile(String imageFileName, Context context, int width, int height) {

        String storageDirectory = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES).getAbsolutePath().concat("/");
        String filePath = storageDirectory.concat(imageFileName);

        BitmapFactory.Options bmOptions = new BitmapFactory.Options();

        // Scale if these are specified
        if (width != 0 && height != 0) {
            int photoW = bmOptions.outWidth;
            int photoH = bmOptions.outHeight;

            // Determine how much to scale down the image
            int scaleFactor = Math.min(photoW / width, photoH / height);

            // Decode the image file into a Bitmap sized to fill the View
            bmOptions.inJustDecodeBounds = false;
            bmOptions.inSampleSize = scaleFactor;
        } else {
            bmOptions.inJustDecodeBounds = true;
        }

        return BitmapFactory.decodeFile(filePath, bmOptions);
    }
}
