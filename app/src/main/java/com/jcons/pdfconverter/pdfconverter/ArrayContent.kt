package com.jcons.pdfconverter.pdfconverter

class ArrayContent : EnclosedContent("[ ", "]") {

    fun addItem(s: String?) {
        addContent(s)
        addSpace()
    }

    fun addItemsFromStringArray(content: Array<String?>) {
        for (s in content) {
            addItem(s)
        }
    }
}