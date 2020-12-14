package com.jcons.pdfconverter.pdfconverter


class CrossReferenceTable : List() {
    private var mObjectNumberStart = 0
    fun setObjectNumberStart(value: Int) {
        mObjectNumberStart = value
    }

    private val objectsXRefInfo: String
        private get() = renderList()

    fun addObjectXRefInfo(byteOffset: Int, generation: Int, inUse: Boolean) {
        val sb = StringBuilder()
        sb.append(String.format("%010d", byteOffset))
        sb.append(" ")
        sb.append(String.format("%05d", generation))
        if (inUse) {
            sb.append(" n ")
        } else {
            sb.append(" f ")
        }
        sb.append("\r\n")
        mList.add(sb.toString())
    }

    private fun render(): String {
        val sb = StringBuilder()
        sb.append("xref")
        sb.append("\r\n")
        sb.append(mObjectNumberStart)
        sb.append(" ")
        sb.append(mList.size)
        sb.append("\r\n")
        sb.append(objectsXRefInfo)
        return sb.toString()
    }

    override fun toPDFString(): String {
        return render()
    }

    override fun clear() {
        super.clear()
        addObjectXRefInfo(0, 65536, false) // free objects linked list head
        mObjectNumberStart = 0
    }

    init {
        clear()
    }
}
