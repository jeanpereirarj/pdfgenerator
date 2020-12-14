package com.jcons.pdfconverter.pdfconverter

class Page(document:PDFDocument) {

    private val mDocument:PDFDocument = document
    val indirectObject:IndirectObject = mDocument.newIndirectObject()
    private val mPageFonts:ArrayList<IndirectObject> = ArrayList()
    private val mXObjects:ArrayList<XObjectImage> = java.util.ArrayList()
    private val mPageContents:IndirectObject = mDocument.newIndirectObject()

    private val fontReferences:String
        get() {
            val result = StringBuilder()
            if (!mPageFonts.isEmpty())
            {
                result.append(" /Font <<\n")
                var x = 0
                for (lFont in mPageFonts)
                {
                    result.append(" /F" + Integer.toString(++x) + " " + lFont.indirectReference + "\n")
                }
                result.append(" >>\n")
            }
            return result.toString()
        }

    private val xObjectReferences:String
        get() {
            val result = StringBuilder()
            if (!mXObjects.isEmpty())
            {
                result.append(" /XObject <<\n")
                for (xObj in mXObjects)
                {
                    result.append(" " + xObj.asXObjectReference() + "\n")
                }
                result.append(" >>\n")
            }
            return result.toString()
        }

    init{
        mDocument.includeIndirectObject(mPageContents)
    }

    fun render(pagesIndirectReference:String) {
        indirectObject.setDictionaryContent(
            (" /Type /Page\n /Parent " + pagesIndirectReference + "\n" +
                    " /Resources <<\n" + fontReferences + xObjectReferences + " >>\n" +
                    " /Contents " + mPageContents.indirectReference + "\n")
        )
    }
    private fun addContent(content:String) {
        mPageContents.addStreamContent(content)
        val streamContent = mPageContents.streamContent
        mPageContents.setDictionaryContent(" /Length " + Integer.toString(streamContent?.length!!) + "\n")
        mPageContents.streamContent =streamContent
    }

    private fun ensureXObjectImage(xObject:XObjectImage):String {
        for (x in mXObjects)
        {
            if (x.id.equals(xObject.id))
            {
                return x.name
            }
        }
        mXObjects.add(xObject)
        xObject.appendToDocument()
        return xObject.name
    }
    fun addImage(fromLeft:Int, fromBottom:Int, width:Int, height:Int, xImage:XObjectImage, transformation:String) {
        val name = ensureXObjectImage(xImage)
        val translate = "1 0 0 1 " + fromLeft + " " + fromBottom
        val scale = "" + width + " 0 0 " + height + " 0 0"
        val rotate = transformation + " 0 0"
        val breakline = " cm\n"
        addContent(
            ("q\n" +
                    translate + breakline +
                    rotate + breakline +
                    scale + breakline +
                    name + " Do\n" +
                    "Q\n")
        )
    }
}