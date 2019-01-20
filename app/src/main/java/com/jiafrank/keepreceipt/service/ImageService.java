package com.jiafrank.keepreceipt.service;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import androidx.annotation.NonNull;
import it.sephiroth.android.library.exif2.ExifInterface;

import static com.jiafrank.keepreceipt.Constants.FILE_DATE_FORMAT_PATTERN;
import static com.jiafrank.keepreceipt.Constants.REQ_IMG_SIZE;

public class ImageService {

    /**
     * Create a new image file for which to save the picture
     */
    public static File getNewImageFile(Context context) throws IOException {

        // Image file name is same as ID - categorized by date created
        String imageFileName = new SimpleDateFormat(FILE_DATE_FORMAT_PATTERN).format(new Date());

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

    public static void compressAndSaveImage(File file) {
        try {

            ExifInterface originalExif = new ExifInterface();
            originalExif.readExif(file.getAbsolutePath(), ExifInterface.Options.OPTION_ALL);

            // Get amount to scale
            BitmapFactory.Options scalingOptions = new BitmapFactory.Options();
            scalingOptions.inSampleSize = calculateInSampleSize(scalingOptions);

            // Get scaled version of bitmap
            InputStream inputStream = new FileInputStream(file);
            Bitmap selectedBitmap = BitmapFactory.decodeStream(inputStream, null, scalingOptions);
            inputStream.close();

            // Overwrite the file (create the file if it somehow does not exist)
            file.createNewFile();
            FileOutputStream outputStream = new FileOutputStream(file);
            selectedBitmap.compress(Bitmap.CompressFormat.JPEG, 50 , outputStream);


            originalExif.writeExif(file.getAbsolutePath());

        } catch (Exception e) {
            Log.e("ImageService", "Image compression failed", e);
        }
    }

    /**
     * From Android docs
     */
    private static int calculateInSampleSize (BitmapFactory.Options options) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > REQ_IMG_SIZE || width > REQ_IMG_SIZE) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) >= REQ_IMG_SIZE
                    && (halfWidth / inSampleSize) >= REQ_IMG_SIZE) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }

//
//    public static void runTextRecognition(Bitmap inputImage) {
//        FirebaseVisionImage image = FirebaseVisionImage.fromBitmap(inputImage);
//        FirebaseVisionTextRecognizer detector = FirebaseVision.getInstance()
//                .getOnDeviceTextRecognizer();
//        Task<FirebaseVisionText> result =
//                detector.processImage(image)
//                        .addOnSuccessListener(new OnSuccessListener<FirebaseVisionText>() {
//                            @Override
//                            public void onSuccess(FirebaseVisionText firebaseVisionText) {
//                                for (FirebaseVisionText.TextBlock textBlock:
//                                        firebaseVisionText.getTextBlocks()) {
//                                    for(FirebaseVisionText.Line line: textBlock.getLines()) {
//                                        Log.e("ocr success", line.getText());
//                                    }
//                                }
//                            }
//                        })
//                        .addOnFailureListener(
//                                new OnFailureListener() {
//                                    @Override
//                                    public void onFailure(@NonNull Exception e) {
//                                        Log.e("ocr", "failed", e);
//                                        // Task failed with an exception
//                                        // ...
//                                    }
//                                });
//    }

    /**
     * The following is to get the rotation of the device on returned image bitmap
     *
     * So:
     * Get bitmap from image activity
     * scale to a non-scary size
     * rotate to upright
     * save the file + launch add activity
     * pass in text recog
     */
//    private static final SparseIntArray ORIENTATIONS = new SparseIntArray();
//    static {
//        ORIENTATIONS.append(Surface.ROTATION_0, 90);
//        ORIENTATIONS.append(Surface.ROTATION_90, 0);
//        ORIENTATIONS.append(Surface.ROTATION_180, 270);
//        ORIENTATIONS.append(Surface.ROTATION_270, 180);
//    }
//
//    /**
//     * Get the angle by which an image must be rotated given the device's current
//     * orientation.
//     */
//    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
//    private int getRotationCompensation(String cameraId, Activity activity, Context context)
//            throws CameraAccessException {
//        // Get the device's current rotation relative to its "native" orientation.
//        // Then, from the ORIENTATIONS table, look up the angle the image must be
//        // rotated to compensate for the device's rotation.
//        int deviceRotation = activity.getWindowManager().getDefaultDisplay().getRotation();
//        int rotationCompensation = ORIENTATIONS.get(deviceRotation);
//
//        // On most devices, the sensor orientation is 90 degrees, but for some
//        // devices it is 270 degrees. For devices with a sensor orientation of
//        // 270, rotate the image an additional 180 ((270 + 270) % 360) degrees.
//        CameraManager cameraManager = (CameraManager) context.getSystemService(CAMERA_SERVICE);
//        int sensorOrientation = cameraManager
//                .getCameraCharacteristics(cameraId)
//                .get(CameraCharacteristics.SENSOR_ORIENTATION);
//        rotationCompensation = (rotationCompensation + sensorOrientation + 270) % 360;
//
//        // Return the corresponding FirebaseVisionImageMetadata rotation value.
//        int result;
//        switch (rotationCompensation) {
//            case 0:
//                result = FirebaseVisionImageMetadata.ROTATION_0;
//                break;
//            case 90:
//                result = FirebaseVisionImageMetadata.ROTATION_90;
//                break;
//            case 180:
//                result = FirebaseVisionImageMetadata.ROTATION_180;
//                break;
//            case 270:
//                result = FirebaseVisionImageMetadata.ROTATION_270;
//                break;
//            default:
//                result = FirebaseVisionImageMetadata.ROTATION_0;
//                Log.e(TAG, "Bad rotation value: " + rotationCompensation);
//        }
//        return result;
//    }

}
