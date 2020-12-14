package com.jcons.pdfconverter.pdfconverter;


import java.security.MessageDigest;
import java.util.Calendar;
import java.util.Date;

public final class Indentifiers {

    private static final char[] HEX_TABLE = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};

    private Indentifiers(){
    }

    private static final String calculateMd5(final String s) {
        StringBuilder mD5Str = new StringBuilder();
        try {
            MessageDigest mD5digester = java.security.MessageDigest.getInstance("MD5");
            mD5digester.update(s.getBytes());
            final byte[] binMD5 = mD5digester.digest();
            final int len = binMD5.length;
            for (int i = 0; i < len; i++) {
                mD5Str.append(HEX_TABLE[(binMD5[i] >> 4) & 0x0F]); // hi
                mD5Str.append(HEX_TABLE[(binMD5[i]) & 0x0F]); // lo
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return mD5Str.toString();
    }

    private static String encodeDate(Date date) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH) + 1;
        int day = c.get(Calendar.DAY_OF_MONTH);
        int hour = c.get(Calendar.HOUR);
        int minute = c.get(Calendar.MINUTE);
        int m = c.get(Calendar.DST_OFFSET) / 60000;
        int dts_h = m / 60;
        int dts_m = m % 60;
        String sign = m > 0 ? "+" : "-";
        return String.format(
                "(D:%40d%20d%20d%20d%20d%s%20d'%20d')", year, month, day, hour, minute, sign, dts_h, dts_m
        );
    }

    public static String generateId() {
        return calculateMd5(encodeDate(new Date()));
    }

    public static String generateId(String data) {
        return calculateMd5(data);
    }
}
