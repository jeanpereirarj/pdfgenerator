package com.jcons.pdfconverter.pdfconverter;


import java.util.ArrayList;

public class Page {

    private PDFDocument mDocument;
    private IndirectObject mIndirectObject;
    private ArrayList<IndirectObject> mPageFonts;
    private ArrayList<XObjectImage> mXObjects;
    private IndirectObject mPageContents;

    public Page(PDFDocument document) {
        mDocument = document;
        mIndirectObject = mDocument.newIndirectObject();
        mPageFonts = new ArrayList<IndirectObject>();
        mXObjects = new ArrayList<XObjectImage>();
        mPageContents = mDocument.newIndirectObject();
        mDocument.includeIndirectObject(mPageContents);
    }

    public IndirectObject getIndirectObject() {
        return mIndirectObject;
    }

    private String getFontReferences() {
        StringBuilder result = new StringBuilder();
        if (!mPageFonts.isEmpty()) {
            result.append("    /Font <<\n");
            int x = 0;
            for (IndirectObject lFont : mPageFonts) {
                result.append("      /F" + Integer.toString(++x) + " " + lFont.getIndirectReference() + "\n");
            }
            result.append("    >>\n");
        }
        return result.toString();
    }

    private String getXObjectReferences() {
        StringBuilder result = new StringBuilder();
        if (!mXObjects.isEmpty()) {
            result.append("    /XObject <<\n");
            for (XObjectImage xObj : mXObjects) {
                result.append("      " + xObj.asXObjectReference() + "\n");
            }
            result.append("    >>\n");
        }
        return result.toString();
    }

    public void render(String pagesIndirectReference) {
        mIndirectObject.setDictionaryContent(
                "  /Type /Page\n  /Parent " + pagesIndirectReference + "\n" +
                        "  /Resources <<\n" + getFontReferences() + getXObjectReferences() + "  >>\n" +
                        "  /Contents " + mPageContents.getIndirectReference() + "\n"
        );
    }

    private void addContent(String content) {
        mPageContents.addStreamContent(content);
        String streamContent = mPageContents.getStreamContent();
        mPageContents.setDictionaryContent("  /Length " + Integer.toString(streamContent.length()) + "\n");
        mPageContents.setStreamContent(streamContent);
    }

    private String ensureXObjectImage(XObjectImage xObject) {
        for (XObjectImage x : mXObjects) {
            if (x.getId().equals(xObject.getId())) {
                return x.getName();
            }
        }
        mXObjects.add(xObject);
        xObject.appendToDocument();
        return xObject.getName();
    }

    public void addImage(int fromLeft, int fromBottom, int width, int height, XObjectImage xImage, String transformation) {
        final String name = ensureXObjectImage(xImage);
        final String translate = "1 0 0 1 " + fromLeft + " " + fromBottom;
        final String scale = "" + width + " 0 0 " + height + " 0 0";
        final String rotate = transformation + " 0 0";
        String breakline =  " cm\n";

        addContent(
                "q\n" +
                        translate + breakline +
                        rotate + breakline +
                        scale + breakline +
                        name + " Do\n" +
                        "Q\n"
        );
    }
}
