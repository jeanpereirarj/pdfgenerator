package com.jcons.pdfconverter.pdfconverter

import java.io.IOException
import java.io.OutputStream


open class EnclosedContent(private val mBegin: String, private val mEnd: String) : Base {

    var mContent: StringBuilder? = null

    fun hasContent(): Boolean {
        return mContent!!.length > 0
    }

    var content: String?
        get() = mContent.toString()
        set(value) {
            clear()
            mContent!!.append(value)
        }

    fun addContent(value: String?) {
        mContent!!.append(value)
    }

    fun addNewLine() {
        mContent!!.append("\n")
    }

    fun addSpace() {
        mContent!!.append(" ")
    }

    override fun clear() {
        mContent = StringBuilder()
    }

    override fun toPDFString(): String {
        return mBegin + mContent.toString() + mEnd
    }

    init {
        clear()
    }
}
