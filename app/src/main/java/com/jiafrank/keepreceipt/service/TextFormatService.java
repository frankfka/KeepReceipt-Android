package com.jiafrank.keepreceipt.service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class TextFormatService {

    public static String getFormattedStringFromDate(Date date, boolean fullMonth) {
        if (fullMonth) {
            return new SimpleDateFormat("MMMM dd, yyyy", Locale.getDefault()).format(date);
        } else {
            return new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(date);
        }
    }

    public static String getFormattedCurrencyString(double amount) {
        return "$ ".concat(String.format("%.2f", amount));
    }

}
