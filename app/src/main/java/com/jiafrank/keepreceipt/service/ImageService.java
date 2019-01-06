package com.jiafrank.keepreceipt.service;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.Log;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.text.FirebaseVisionText;
import com.google.firebase.ml.vision.text.FirebaseVisionTextRecognizer;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import androidx.annotation.NonNull;

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

    public static void runTextRecognition(Bitmap inputImage) {
        FirebaseVisionImage image = FirebaseVisionImage.fromBitmap(inputImage);
        FirebaseVisionTextRecognizer detector = FirebaseVision.getInstance()
                .getOnDeviceTextRecognizer();
        Task<FirebaseVisionText> result =
                detector.processImage(image)
                        .addOnSuccessListener(new OnSuccessListener<FirebaseVisionText>() {
                            @Override
                            public void onSuccess(FirebaseVisionText firebaseVisionText) {
                                for (FirebaseVisionText.TextBlock textBlock:
                                        firebaseVisionText.getTextBlocks()) {
                                    for(FirebaseVisionText.Line line: textBlock.getLines()) {
                                        Log.e("ocr success", line.getText());
                                    }
                                }
                            }
                        })
                        .addOnFailureListener(
                                new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.e("ocr", "failed", e);
                                        // Task failed with an exception
                                        // ...
                                    }
                                });
    }

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
