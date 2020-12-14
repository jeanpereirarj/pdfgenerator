package com.jcons.pdfconverter.pdfconverter

import java.security.MessageDigest
import java.util.*


object Indentifiers {
    private val HEX_TABLE =
        charArrayOf('0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F')

    private fun calculateMd5(s: String): String {
        val mD5Str = StringBuilder()
        try {
            val mD5digester =
                MessageDigest.getInstance("MD5")
            mD5digester.update(s.toByteArray())
            val binMD5 = mD5digester.digest()
            val len = binMD5.size
            for (i in 0 until len) {
                mD5Str.append(HEX_TABLE[binMD5[i].toInt() shr 4 and 0x0F]) // hi
                mD5Str.append(HEX_TABLE[binMD5[i].toInt() and 0x0F]) // lo
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return mD5Str.toString()
    }

    private fun encodeDate(date: Date): String {
        val c = Calendar.getInstance()
        c.time = date
        val year = c[Calendar.YEAR]
        val month = c[Calendar.MONTH] + 1
        val day = c[Calendar.DAY_OF_MONTH]
        val hour = c[Calendar.HOUR]
        val minute = c[Calendar.MINUTE]
        val m = c[Calendar.DST_OFFSET] / 60000
        val dts_h = m / 60
        val dts_m = m % 60
        val sign = if (m > 0) "+" else "-"
        return String.format(
            "(D:%40d%20d%20d%20d%20d%s%20d'%20d')",
            year,
            month,
            day,
            hour,
            minute,
            sign,
            dts_h,
            dts_m
        )
    }

    fun generateId(): String {
        return calculateMd5(encodeDate(Date()))
    }

    fun generateId(data: String): String {
        return calculateMd5(data)
    }
}
