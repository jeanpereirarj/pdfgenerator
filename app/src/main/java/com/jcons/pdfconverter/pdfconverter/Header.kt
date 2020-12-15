package com.jcons.pdfconverter.pdfconverter


class Header : Base {
    private var mVersion: String? = null
    private var mRenderedHeader: String? = null

    private fun setVersion(major: Int, minor: Int) {
        mVersion = "$major.$minor"
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

    init {
        clear()
    }
}
