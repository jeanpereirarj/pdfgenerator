package com.jcons.pdfconverter.pdfconverter;


import java.util.ArrayList;

public abstract class List extends Base {

    protected ArrayList<String> mList  = new ArrayList<String>();

    protected String renderList() {
        StringBuilder sb = new StringBuilder();
        for (String s : mList) {
            sb.append(s);
        }
        return sb.toString();
    }

    @Override
    public void clear() {
        mList.clear();
    }
}
