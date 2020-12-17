package com.jcons.pdfconverter.pdfconverter

import android.graphics.Bitmap
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.util.zip.Deflater
import java.util.zip.DeflaterOutputStream

class XObjectImage(private val mDocument: PDFDocument, bitmap: Bitmap) {
    private var mIndirectObject: IndirectObject? = null
    private var mDataSize = 0
    var width = -1
        private set
    var height = -1
        private set
    var name = ""
    var id = ""
    private var mProcessedImage: String? = ""
    fun appendToDocument() {
        mIndirectObject = mDocument.newIndirectObject()
        mDocument.includeIndirectObject(mIndirectObject)
        mIndirectObject!!.addDictionaryContent(
            """ /Type /XObject
 /Subtype /Image
 /Filter [/ASCII85Decode /FlateDecode]
 /Width ${width}
 /Height ${height}
 /BitsPerComponent ${Integer.toString(BITSPER_COMPONENT)}
 /Interpolate ${java.lang.Boolean.toString(INTERPOLATION)}
 /ColorSpace $DEVICE_RGB
 /Length ${mProcessedImage!!.length}
"""
        )
        mIndirectObject!!.addStreamContent(mProcessedImage)
    }

    private fun configureBitmap(bitmap: Bitmap): Bitmap? {
        val img = bitmap.copy(Bitmap.Config.ARGB_8888, false)
        if (img != null) {
            width = img.width
            height = img.height
            mDataSize = width * height * 3
        }
        return img
    }

    private fun getBitmapData(bitmap: Bitmap?): ByteArray? {
        var data: ByteArray? = null
        if (bitmap != null) {
            data = ByteArray(mDataSize)
            var intColor: Int
            var offset = 0
            for (y in 0 until height) {
                for (x in 0 until width) {
                    intColor = bitmap.getPixel(x, y)
                    data[offset++] = (intColor shr 16 and 0xFF).toByte()
                    data[offset++] = (intColor shr 8 and 0xFF).toByte()
                    data[offset++] = (intColor shr 0 and 0xFF).toByte()
                }
            }
        }
        return data
    }

    private fun deflateImageData(baos: ByteArrayOutputStream, data: ByteArray?): Boolean {
        if (data != null) {
            val deflater = Deflater(COMPRESSION_LEVEL)
            val dos = DeflaterOutputStream(baos, deflater)
            try {
                dos.write(data)
                dos.close()
                deflater.end()
                return true
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
        return false
    }

    private fun encodeImageData(baos: ByteArrayOutputStream): String {
        val sob = ByteArrayOutputStream()
        val enc85 = ASCII85Encoder(sob)
        try {
            var i = 0
            for (b in baos.toByteArray()) {
                enc85.write(b.toInt())
                if (i++ == 255) {
                    sob.write('\n'.toInt())
                    i = 0
                }
            }
            return sob.toString(Charsets.ISO_8859_1.name())
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return ""
    }

    private fun processImage(bitmap: Bitmap?): String? {
        val baos = ByteArrayOutputStream()
        return if (deflateImageData(baos, getBitmapData(bitmap))) {
            encodeImageData(baos)
        } else null
    }

    fun asXObjectReference(): String {
        return name + " " + mIndirectObject!!.indirectReference
    }

    companion object {
        const val BITSPERCOMPONENT_8 = 8
        const val DEVICE_RGB = "/DeviceRGB"
        const val INTERPOLATION = false
        const val BITSPER_COMPONENT = BITSPERCOMPONENT_8
        const val COLOR_SPACE = DEVICE_RGB
        const val COMPRESSION_LEVEL = Deflater.NO_COMPRESSION
        var mImageCount = 0
    }

    init {
        mProcessedImage = processImage(configureBitmap(bitmap))
        id = Indentifiers.generateId(mProcessedImage!!)
        name = "/img" + ++mImageCount
    }
}