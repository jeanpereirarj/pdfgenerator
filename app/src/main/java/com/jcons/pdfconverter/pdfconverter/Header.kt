package com.jcons.pdfconverter.pdfconverter

import com.jcons.pdfconverter.pdfconverter.Base
import com.jcons.pdfconverter.pdfconverter.StandardCharsets
import java.io.IOException
import java.io.OutputStream


class Header : Base() {
    private var mVersion: String? = null
    private var mRenderedHeader: String? = null
    fun setVersion(Major: Int, Minor: Int) {
        mVersion = Integer.toString(Major) + "." + Integer.toString(Minor)
        render()
    }

    val pDFStringSize: Int
        get() = mRenderedHeader!!.length

    private fun render() {
        mRenderedHeader = "%PDF-$mVersion\n%\u00a9\u00bb\u00aa\u00b5\n"
    }

    override fun toPDFString(): String? {
        return mRenderedHeader
    }

    override fun clear() {
        setVersion(1, 4)
    }

    @Throws(IOException::class)
    fun writeTo(stream: OutputStream): Int {
        val bytes: ByteArray = mRenderedHeader?.toByteArray(StandardCharsets.US_ASCII)!!
        stream.write(bytes)
        return bytes.size
    }

    init {
        clear()
    }
}
