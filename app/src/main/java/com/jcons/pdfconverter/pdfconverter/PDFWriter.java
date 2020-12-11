package com.jcons.pdfconverter.pdfconverter;

import android.graphics.Bitmap;

public class PDFWriter{

    private PDFDocument mDocument;
    private IndirectObject mCatalog;
    private Pages mPages;
    private Page mCurrentPage;

    public PDFWriter() {
        newDocument(PaperSize.A4_WIDTH, PaperSize.A4_HEIGHT);
    }

    private void newDocument(int pageWidth, int pageHeight) {
        mDocument = new PDFDocument();
        mCatalog = mDocument.newIndirectObject();
        mDocument.includeIndirectObject(mCatalog);
        mPages = new Pages(mDocument, pageWidth, pageHeight);
        mDocument.includeIndirectObject(mPages.getIndirectObject());
        renderCatalog();
        newPage();
    }

    private void renderCatalog() {
        mCatalog.setDictionaryContent("  /Type /Catalog\n  /Pages " + mPages.getIndirectObject().getIndirectReference() + "\n");
    }

    public void newPage() {
        mCurrentPage = mPages.newPage();
        mDocument.includeIndirectObject(mCurrentPage.getIndirectObject());
        mPages.render();
    }

    public void addImage(int fromLeft, int fromBottom, Bitmap bitmap) {
        addImage(fromLeft, fromBottom, bitmap, Transformation.DEGREES_0_ROTATION);
    }

    public void addImage(int fromLeft, int fromBottom, Bitmap bitmap, String transformation) {
        final XObjectImage xImage = new XObjectImage(mDocument, bitmap);
        mCurrentPage.addImage(fromLeft, fromBottom, xImage.getWidth(), xImage.getHeight(), xImage, transformation);
    }

    public String asString() {
        mPages.render();
        return mDocument.toPDFString();
    }

}
