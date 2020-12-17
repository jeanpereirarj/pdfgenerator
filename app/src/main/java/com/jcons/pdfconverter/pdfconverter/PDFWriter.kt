package com.jcons.pdfconverter.pdfconverter

import android.graphics.Bitmap


class PDFWriter {
    private var mDocument: PDFDocument = PDFDocument()
    private var mCatalog: IndirectObject? = null
    private var mPages: Pages? = null
    private var mCurrentPage: Page? = null

    private fun renderCatalog() {
        mCatalog!!.setDictionaryContent(
            """  /Type /Catalog
            /Pages ${mPages?.indirectObject?.indirectReference}
            """
        )
    }

    fun newPage() {
        mCurrentPage = mPages!!.newPage()
        mDocument!!.includeIndirectObject(mCurrentPage!!.indirectObject)
        mPages!!.render()
    }

    @JvmOverloads
    fun addImage(
        fromLeft: Int,
        fromBottom: Int,
        bitmap: Bitmap,
        transformation: String = DEGREES_0_ROTATION
    ) {
        val xImage = XObjectImage(mDocument, bitmap)
        mCurrentPage!!.addImage(
            fromLeft,
            fromBottom,
            xImage.width,
            xImage.height,
            xImage,
            transformation
        )
    }

    fun asString(): String {
        mPages!!.render()
        return mDocument!!.toPDFString()
    }

    companion object {
        const val DEGREES_0_ROTATION = "1 0 0 1"
    }

    init {
        mCatalog = mDocument!!.newIndirectObject()
        mDocument!!.includeIndirectObject(mCatalog)
        mPages = Pages(mDocument!!)
        mDocument!!.includeIndirectObject(mPages?.indirectObject)
        renderCatalog()
        newPage()
    }
}
