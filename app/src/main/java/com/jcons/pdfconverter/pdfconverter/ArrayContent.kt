package com.jcons.pdfconverter.pdfconverter

class ArrayContent : EnclosedContent("[ ", "]") {

    fun addItem(s: String?) {
        addContent(s)
        addSpace()
    }

}