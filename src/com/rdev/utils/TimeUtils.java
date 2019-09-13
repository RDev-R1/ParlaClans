package com.rdev.utils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public final class TimeUtils {

    public static final int HOUR = 3600000;

    public static int getDateDay() {
        DateFormat dateFormat = new SimpleDateFormat("dd");
        Date date = new Date();
        return Integer.parseInt(dateFormat.format(date));
    }
}
