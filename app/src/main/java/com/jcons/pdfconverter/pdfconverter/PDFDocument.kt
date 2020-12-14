package com.jcons.pdfconverter.pdfconverter


class PDFDocument : Base() {

    private val mHeader: Header = Header()
    private val mBody: Body = Body()
    private val mCRT: CrossReferenceTable = CrossReferenceTable()
    private val mTrailer: Trailer = Trailer()

    fun newIndirectObject(): IndirectObject {
        return mBody.newIndirectObject
    }

    fun includeIndirectObject(iobj: IndirectObject?) {
        mBody.includeIndirectObject(iobj!!)
    }

    fun setId(id: String?) {
        mTrailer.setId(id)
    }

    override fun toPDFString(): String {
        val sb = StringBuilder()
        sb.append(mHeader.toPDFString())
        sb.append(mBody.toPDFString())
        mCRT.setObjectNumberStart(mBody.objectNumberStart)
        var x = 0
        while (x < mBody.objectsCount) {
            val iobj = mBody.getObjectByNumberID(++x)
            if (iobj != null) {
                mCRT.addObjectXRefInfo(iobj.byteOffset, iobj.generation, iobj.inUse)
            }
        }
        mTrailer.setObjectsCount(mBody.objectsCount)
        mTrailer.setCrossReferenceTableByteOffset(sb.length)
        return sb.toString() + mCRT.toPDFString() + mTrailer.toPDFString()
    }

    override fun clear() {
        mHeader.clear()
        mBody.clear()
        mCRT.clear()
        mTrailer.clear()
    }

    init {
        mBody.setByteOffsetStart(mHeader.pDFStringSize)
        mBody.objectNumberStart = 0
        setId(Indentifiers.generateId())
    }
}
