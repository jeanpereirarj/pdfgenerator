package com.jcons.pdfconverter.pdfconverter;


import java.io.IOException;
import java.io.OutputStream;

import kotlin.text.Charsets;

public class IndirectIdentifier extends Base {

    private int mNumber;
    private int mGeneration;

    public IndirectIdentifier() {
        clear();
    }

    public void setNumber(int number) {
        this.mNumber = number;
    }

    public int getNumber() {
        return mNumber;
    }

    public void setGeneration(int generation) {
        this.mGeneration = generation;
    }

    public int getGeneration() {
        return mGeneration;
    }

    @Override
    public void clear() {
        mNumber = 0;
        mGeneration = 0;
    }

    @Override
    public String toPDFString() {
        return mNumber + " " + mGeneration;
    }

    public int writeTo(OutputStream stream) throws IOException {
        byte[] bytes = toPDFString().getBytes(Charsets.US_ASCII);
        stream.write(bytes);
        return bytes.length;
    }
}

