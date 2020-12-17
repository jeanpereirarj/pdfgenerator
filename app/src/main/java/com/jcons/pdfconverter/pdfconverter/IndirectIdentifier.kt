package com.jcons.pdfconverter.pdfconverter

import java.io.IOException
import java.io.OutputStream


class IndirectIdentifier : Base {
    var number = 0
    var generation = 0

    override fun clear() {
        number = 0
        generation = 0
    }

    override fun toPDFString(): String {
        return "$number $generation"
    }

    init {
        clear()
    }
}

