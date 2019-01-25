package com.jiafrank.keepreceipt;

public class Constants {

    // Key to store the receipt ID between activities
    public static String ID_STRING_INTENT_NAME = "ID_STRING_INTENT_NAME";
    // Key to store the action for edit/add receipt
    public static String ACTIVITY_ACTION_INTENT_NAME = "ACTIVITY_ACTION_INTENT_NAME";
    // Key to store selected category for pick category
    public static String SELECTED_CATEGORY_INTENT_NAME = "SELECTED_CATEGORY_INTENT_NAME";
    // Action values for edit/add receipt
    public static final int ACTIVITY_ACTION_EDIT = 1;
    public static final int ACTIVITY_ACTION_CREATE = 2;
    // Action values for startActivityWithResult
    public static final int REQUEST_IMAGE = 1;
    public static final int ADD_NEW_RECEIPT = 2;
    public static final int EDIT_RECEIPT = 3;
    public static final int PICK_CATEGORY = 4;
    // Image processing values
    public static final int REQ_IMG_SIZE = 500;
    public static final String FILE_DATE_FORMAT_PATTERN = "yyyyMMdd_HHmmss_SSSS";
    // Add Receipt
    public static final String ADD_RECEIPT_CHOICE_IMPORT = "Import from Library";
    public static final String ADD_RECEIPT_CHOICE_TAKENEW = "Take New Image";
    public static final String[] ADD_RECEIPT_CHOICES = new String[]{ADD_RECEIPT_CHOICE_IMPORT, ADD_RECEIPT_CHOICE_TAKENEW};

}
