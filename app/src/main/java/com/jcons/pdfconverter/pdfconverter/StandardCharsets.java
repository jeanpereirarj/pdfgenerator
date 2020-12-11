package com.jcons.pdfconverter.pdfconverter;


import java.nio.charset.Charset;

/**
 * Convenient access to the most important built-in charsets.
 * @since 1.7
 */
public final class StandardCharsets {
    private StandardCharsets() {
    }

    /**
     * The ISO-8859-1 charset.
     */
    public static final Charset ISO_8859_1 = Charset.forName("ISO-8859-1");

    /**
     * The US-ASCII charset.
     */
    public static final Charset US_ASCII = Charset.forName("US-ASCII");

    /**
     * The UTF-8 charset.
     */
    public static final Charset UTF_8 = Charset.forName("UTF-8");

    /**
     * The UTF-16 charset.
     */
    public static final Charset UTF_16 = Charset.forName("UTF-16");

    /**
     * The UTF-16BE (big-endian) charset.
     */
    public static final Charset UTF_16BE = Charset.forName("UTF-16BE");

    /**
     * The UTF-16LE (little-endian) charset.
     */
    public static final Charset UTF_16LE = Charset.forName("UTF-16LE");
}
