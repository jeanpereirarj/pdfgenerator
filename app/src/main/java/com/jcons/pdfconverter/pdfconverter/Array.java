package com.jcons.pdfconverter.pdfconverter;

public class Array extends EnclosedContent {

    public Array() {
        super("[ ", "]");
    }

    public void addItem(String s) {
        addContent(s);
        addSpace();
    }

    public void addItemsFromStringArray(String[] content) {
        for (String s : content) {
            addItem(s);
        }
    }
}