package com.jcons.pdfconverter.pdfconverter

class Pages(private val mDocument: PDFDocument) {

    private val mPageList: ArrayList<Page> = ArrayList()
    val indirectObject: IndirectObject = mDocument.newIndirectObject()
    private val mKids: ArrayContent = ArrayContent()

    fun newPage(): Page {
        val lPage =
            Page(mDocument)
        mPageList.add(lPage)
        mKids.addItem(lPage.indirectObject.indirectReference)
        return lPage
    }

    fun render() {
        indirectObject.setDictionaryContent(
            """  /Type /Pages
  /Count ${Integer.toString(mPageList.size)}
  /Kids ${mKids.toPDFString()}
"""
        )
        for (lPage in mPageList) {
            lPage.render(indirectObject.indirectReference)
        }
    }

    init {
//        val content = arrayOf<String?>(
//            "0",
//            "0",
//            pageWidth.toString(),
//            pageHeight.toString()
//        )
    }
}
