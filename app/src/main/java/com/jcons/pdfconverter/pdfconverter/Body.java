package com.jcons.pdfconverter.pdfconverter;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;

public class Body extends List {

    private int mByteOffsetStart;
    private int mObjectNumberStart;
    private int mGeneratedObjectsCount;
    private ArrayList<IndirectObject> mObjectsList;

    public Body() {
        super();
        clear();
    }

    public int getObjectNumberStart() {
        return mObjectNumberStart;
    }

    public void setObjectNumberStart(int value) {
        mObjectNumberStart = value;
    }

    public void setByteOffsetStart(int value) {
        mByteOffsetStart = value;
    }

    public int getObjectsCount() {
        return mObjectsList.size();
    }

    private int getNextAvailableObjectNumber() {
        return ++mGeneratedObjectsCount + mObjectNumberStart;
    }

    public IndirectObject getNewIndirectObject() {
        return getNewIndirectObject(getNextAvailableObjectNumber(), 0, true);
    }

    public IndirectObject getNewIndirectObject(int number, int generation, boolean inUse) {
        IndirectObject iobj = new IndirectObject();
        iobj.setNumberID(number);
        iobj.setGeneration(generation);
        iobj.setInUse(inUse);
        return iobj;
    }

    public IndirectObject getObjectByNumberID(int number) {
        for (IndirectObject iobj : mObjectsList)
            if (iobj.getNumberID() == number)
                return iobj;

        return null;
    }

    public void includeIndirectObject(IndirectObject iobj) {
        mObjectsList.add(iobj);
    }

    private String render() {
        int offset = mByteOffsetStart;
        for (int x = 1; x <= mObjectsList.size(); x++) {
            IndirectObject iobj = getObjectByNumberID(x);
            String s = "";

            if (iobj != null){
                s = iobj.toPDFString() + "\n";
                iobj.setByteOffset(offset);
            }

            mList.add(s);
            offset += s.length();
        }
        return renderList();
    }

    @Override
    public String toPDFString() {
        return render();
    }

    public int writeTo(OutputStream stream) throws IOException {
        int offset = mByteOffsetStart;
        for (int x = 1; x <= mObjectsList.size(); x++) {
            IndirectObject iobj = getObjectByNumberID(x);
            if (iobj != null) {
                iobj.setByteOffset(offset);
                offset += iobj.writeTo(stream) + 1;
                stream.write('\n');
            }
        }
        return offset - mByteOffsetStart;
    }

    @Override
    public void clear() {
        super.clear();
        mByteOffsetStart = 0;
        mObjectNumberStart = 0;
        mGeneratedObjectsCount = 0;
        mObjectsList = new ArrayList<IndirectObject>();
    }
}
