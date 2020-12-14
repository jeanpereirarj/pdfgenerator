package com.jcons.pdfconverter.pdfconverter;


import java.io.IOException;
import java.io.OutputStream;

public class IndirectObject extends Base {

    private EnclosedContent mContent;
    private Dictionary mDictionaryContent;
    private Stream mStreamContent;
    private IndirectIdentifier mID;
    private int mByteOffset;
    private boolean mInUse;

    public IndirectObject() {
        clear();
    }

    public void setNumberID(int value) {
        mID.setNumber(value);
    }

    public int getNumberID() {
        return mID.getNumber();
    }

    public void setGeneration(int value) {
        mID.setGeneration(value);
    }

    public int getGeneration() {
        return mID.getGeneration();
    }

    public String getIndirectReference() {
        return mID.toPDFString() + " R";
    }

    public void setByteOffset(int value) {
        mByteOffset = value;
    }

    public int getByteOffset() {
        return mByteOffset;
    }

    public void setInUse(boolean value) {
        mInUse = value;
    }

    public boolean getInUse() {
        return mInUse;
    }


    public void setContent(String value) {
        mContent.setContent(value);
    }

    public void addDictionaryContent(String value) {
        mDictionaryContent.addContent(value);
    }

    public void setDictionaryContent(String value) {
        mDictionaryContent.setContent(value);
    }

    public void addStreamContent(String value) {
        mStreamContent.addContent(value);
    }

    public void setStreamContent(String value) {
        mStreamContent.setContent(value);
    }

    public String getStreamContent() {
        return mStreamContent.getContent();
    }

    protected String render() {
        StringBuilder sb = new StringBuilder();
        sb.append(mID.toPDFString());
        sb.append(" ");
        // j-a-s-d: this can be performed in inherited classes DictionaryObject and StreamObject
        if (mDictionaryContent.hasContent()) {
            mContent.setContent(mDictionaryContent.toPDFString());
            if (mStreamContent.hasContent())
                mContent.addContent(mStreamContent.toPDFString());
        }
        sb.append(mContent.toPDFString());
        return sb.toString();
    }

    @Override
    public void clear() {
        mID = new IndirectIdentifier();
        mByteOffset = 0;
        mInUse = false;
        mContent = new EnclosedContent("obj\n", "endobj\n");
        mDictionaryContent = new Dictionary();
        mStreamContent = new Stream();
    }

    @Override
    public String toPDFString() {
        return render();
    }

    public int writeTo(OutputStream stream) throws IOException {
        // j-a-s-d: this can be performed in inherited classes DictionaryObject and StreamObject
        if (mDictionaryContent.hasContent()) {
            mContent.setContent(mDictionaryContent.toPDFString());
            if (mStreamContent.hasContent())
                mContent.addContent(mStreamContent.toPDFString());
        }

        int offset = mID.writeTo(stream);

        stream.write(' ');
        offset++;

        return offset + mContent.writeTo(stream);
    }
}
