package com.jcons.pdfconverter.pdfconverter

import java.io.IOException
import java.io.OutputStream


class IndirectObject : Base {
    private var mContent: EnclosedContent? = null
    private var mDictionaryContent: Dictionary? = null
    private var mStreamContent: Stream? = null
    private var mID: IndirectIdentifier? = null
    var byteOffset = 0
    var inUse = false

    var numberID: Int
        get() = mID!!.number
        set(value) {
            mID!!.number = value
        }

    var generation: Int
        get() = mID!!.generation
        set(value) {
            mID!!.generation = value
        }

    val indirectReference: String
        get() = mID!!.toPDFString() + " R"

    fun setContent(value: String?) {
        mContent?.content = value
    }

    fun addDictionaryContent(value: String?) {
        mDictionaryContent!!.addContent(value)
    }

    fun setDictionaryContent(value: String?) {
        mDictionaryContent?.content = value
    }

    fun addStreamContent(value: String?) {
        mStreamContent!!.addContent(value)
    }

    var streamContent: String?
        get() = mStreamContent?.content
        set(value) {
            mStreamContent?.content = value
        }

    protected fun render(): String {
        val sb = StringBuilder()
        sb.append(mID!!.toPDFString())
        sb.append(" ")
        // j-a-s-d: this can be performed in inherited classes DictionaryObject and StreamObject
        if (mDictionaryContent!!.hasContent()) {
            mContent?.content = mDictionaryContent!!.toPDFString()
            if (mStreamContent!!.hasContent()) mContent!!.addContent(mStreamContent!!.toPDFString())
        }
        sb.append(mContent!!.toPDFString())
        return sb.toString()
    }

    override fun clear() {
        mID = IndirectIdentifier()
        byteOffset = 0
        inUse = false
        mContent = EnclosedContent("obj\n", "endobj\n")
        mDictionaryContent = Dictionary()
        mStreamContent = Stream()
    }

    override fun toPDFString(): String {
        return render()
    }

    @Throws(IOException::class)
    fun writeTo(stream: OutputStream): Int {
        // j-a-s-d: this can be performed in inherited classes DictionaryObject and StreamObject
        if (mDictionaryContent!!.hasContent()) {
            mContent?.content = mDictionaryContent!!.toPDFString()
            if (mStreamContent!!.hasContent()) mContent!!.addContent(mStreamContent!!.toPDFString())
        }
        var offset = mID!!.writeTo(stream)
        stream.write(' '.toInt())
        offset++
        return offset + mContent!!.writeTo(stream)
    }

    init {
        clear()
    }
}
