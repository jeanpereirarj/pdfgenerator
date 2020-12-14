package com.jcons.pdfconverter.pdfconverter;


public class Trailer extends Base {

    private int mXRefByteOffset;
    private int mObjectsCount;
    private String mId;
    private Dictionary mTrailerDictionary;

    public Trailer() {
        clear();
    }

    public void setId(String value) {
        mId = value;
    }

    public void setCrossReferenceTableByteOffset(int value) {
        mXRefByteOffset = value;
    }

    public void setObjectsCount(int value) {
        mObjectsCount = value;
    }

    private void renderDictionary() {
        mTrailerDictionary.setContent("  /Size " + Integer.toString(mObjectsCount));
        mTrailerDictionary.addNewLine();
        mTrailerDictionary.addContent("  /Root 1 0 R");
        mTrailerDictionary.addNewLine();
        mTrailerDictionary.addContent("  /ID [<" + mId + "> <" + mId + ">]");
        mTrailerDictionary.addNewLine();
    }

    private String render() {
        renderDictionary();
        StringBuilder sb = new StringBuilder();
        sb.append("trailer");
        sb.append("\n");
        sb.append(mTrailerDictionary.toPDFString());
        sb.append("startxref");
        sb.append("\n");
        sb.append(mXRefByteOffset);
        sb.append("\n");
        sb.append("%%EOF");
        sb.append("\n");
        return sb.toString();
    }

    @Override
    public String toPDFString() {
        return render();
    }

    @Override
    public void clear() {
        mXRefByteOffset = 0;
        mTrailerDictionary = new Dictionary();
    }

}
