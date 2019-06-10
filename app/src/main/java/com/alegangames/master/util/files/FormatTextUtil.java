package com.alegangames.master.util.files;


import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;

public class FormatTextUtil {

    public static String getFormatDate(long date) {
        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd yyyy", Locale.getDefault());
        return sdf.format(date);
    }

    public static String getFileSize(long size) {
        if (size <= 0) return "0";
        String[] units = new String[]{"B", "KB", "MB", "GB", "TB"};
        int digitGroups = (int) (Math.log10(size) / Math.log10(1024));
        return new DecimalFormat("#,##0.#").format(size / Math.pow(1024, digitGroups)) + " " + units[digitGroups];
    }
}
