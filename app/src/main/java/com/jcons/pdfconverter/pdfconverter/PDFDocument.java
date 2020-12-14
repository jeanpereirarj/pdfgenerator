package com.jcons.pdfconverter.pdfconverter;


public class PDFDocument extends Base {

    private Header mHeader;
    private Body mBody;
    private CrossReferenceTable mCRT;
    private Trailer mTrailer;

    public PDFDocument() {
        mHeader = new Header();
        mBody = new Body();
        mBody.setByteOffsetStart(mHeader.getPDFStringSize());
        mBody.setObjectNumberStart(0);
        mCRT = new CrossReferenceTable();
        mTrailer = new Trailer();
        setId(Indentifiers.generateId());
    }

    public IndirectObject newIndirectObject() {
        return mBody.getNewIndirectObject();
    }

    public void includeIndirectObject(IndirectObject iobj) {
        mBody.includeIndirectObject(iobj);
    }

    public void setId(String id) {
        mTrailer.setId(id);
    }

    @Override
    public String toPDFString() {
        StringBuilder sb = new StringBuilder();
        sb.append(mHeader.toPDFString());
        sb.append(mBody.toPDFString());
        mCRT.setObjectNumberStart(mBody.getObjectNumberStart());
        int x = 0;
        while (x < mBody.getObjectsCount()) {
            IndirectObject iobj = mBody.getObjectByNumberID(++x);
            if (iobj != null) {
                mCRT.addObjectXRefInfo(iobj.getByteOffset(), iobj.getGeneration(), iobj.getInUse());
            }
        }
        mTrailer.setObjectsCount(mBody.getObjectsCount());
        mTrailer.setCrossReferenceTableByteOffset(sb.length());
        return sb.toString() + mCRT.toPDFString() + mTrailer.toPDFString();
    }

    @Override
    public void clear() {
        mHeader.clear();
        mBody.clear();
        mCRT.clear();
        mTrailer.clear();
    }
}
