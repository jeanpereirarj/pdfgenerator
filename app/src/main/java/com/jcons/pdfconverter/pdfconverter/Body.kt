package com.jcons.pdfconverter.pdfconverter

import java.io.IOException
import java.io.OutputStream
import java.util.*


class Body : List() {
    private var mByteOffsetStart = 0
    var objectNumberStart = 0
    private var mGeneratedObjectsCount = 0
    private var mObjectsList: ArrayList<IndirectObject>? = null

    fun setByteOffsetStart(value: Int) {
        mByteOffsetStart = value
    }

    val objectsCount: Int
        get() = mObjectsList!!.size

    private val nextAvailableObjectNumber: Int
        private get() = ++mGeneratedObjectsCount + objectNumberStart

    val newIndirectObject: IndirectObject
        get() = getNewIndirectObject(nextAvailableObjectNumber, 0, true)

    fun getNewIndirectObject(number: Int, generation: Int, inUse: Boolean): IndirectObject {
        val iobj = IndirectObject()
        iobj.numberID = number
        iobj.generation = generation
        iobj.inUse = inUse
        return iobj
    }

    fun getObjectByNumberID(number: Int): IndirectObject? {
        for (iobj in mObjectsList!!) if (iobj.numberID == number) return iobj
        return null
    }

    fun includeIndirectObject(iobj: IndirectObject) {
        mObjectsList!!.add(iobj)
    }

    private fun render(): String {
        var offset = mByteOffsetStart
        for (x in 1..mObjectsList!!.size) {
            val iobj = getObjectByNumberID(x)
            var s = ""
            if (iobj != null) {
                s = """
                    ${iobj.toPDFString()}
                    
                    """.trimIndent()
                iobj.byteOffset = offset
            }
            mList.add(s)
            offset += s.length
        }
        return renderList()
    }

    override fun toPDFString(): String {
        return render()
    }

    @Throws(IOException::class)
    fun writeTo(stream: OutputStream): Int {
        var offset = mByteOffsetStart
        for (x in 1..mObjectsList!!.size) {
            val iobj = getObjectByNumberID(x)
            if (iobj != null) {
                iobj.byteOffset = offset
                offset += iobj.writeTo(stream) + 1
                stream.write('\n'.toInt())
            }
        }
        return offset - mByteOffsetStart
    }

    override fun clear() {
        super.clear()
        mByteOffsetStart = 0
        objectNumberStart = 0
        mGeneratedObjectsCount = 0
        mObjectsList = ArrayList()
    }

    init {
        clear()
    }
}
