package com.jcons.pdfconverter.pdfconverter


class Trailer : Base {
    private var mXRefByteOffset = 0
    private var mObjectsCount = 0
    private var mId: String? = null
    private var mTrailerDictionary: Dictionary? = null
    fun setId(value: String?) {
        mId = value
    }

    fun setCrossReferenceTableByteOffset(value: Int) {
        mXRefByteOffset = value
    }

    fun setObjectsCount(value: Int) {
        mObjectsCount = value
    }

    private fun renderDictionary() {
        mTrailerDictionary!!.content = "  /Size " + Integer.toString(mObjectsCount)
        mTrailerDictionary!!.addNewLine()
        mTrailerDictionary!!.addContent("  /Root 1 0 R")
        mTrailerDictionary!!.addNewLine()
        mTrailerDictionary!!.addContent("  /ID [<$mId> <$mId>]")
        mTrailerDictionary!!.addNewLine()
    }

    override fun toPDFString(): String {
        renderDictionary()
        val sb = StringBuilder()
        sb.append("trailer")
        sb.append("\n")
        sb.append(mTrailerDictionary!!.toPDFString())
        sb.append("startxref")
        sb.append("\n")
        sb.append(mXRefByteOffset)
        sb.append("\n")
        sb.append("%%EOF")
        sb.append("\n")
        return sb.toString()
    }

    override fun clear() {
        mXRefByteOffset = 0
        mTrailerDictionary = Dictionary()
    }

    init {
        clear()
    }
}
