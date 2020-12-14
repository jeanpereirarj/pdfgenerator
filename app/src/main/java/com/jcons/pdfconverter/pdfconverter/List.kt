package com.jcons.pdfconverter.pdfconverter

import java.util.*


abstract class List : Base() {
    protected var mList = ArrayList<String>()
    protected fun renderList(): String {
        val sb = StringBuilder()
        for (s in mList) {
            sb.append(s)
        }
        return sb.toString()
    }

    override fun clear() {
        mList.clear()
    }
}
