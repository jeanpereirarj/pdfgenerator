package com.jcons.pdfconverter

import android.graphics.Bitmap
import android.util.Log
import java.io.FileOutputStream
import java.io.OutputStream
import java.nio.ByteBuffer


class AndroidBmpUtil {

    private val bfType = byteArrayOf('B'.toByte(), 'M'.toByte())
    private var bfSize = 0
    private val bfReserved1 = 0
    private val bfReserved2 = 0
    private val bfOffBits =
        BITMAPFILEHEADER_SIZE + BITMAPINFOHEADER_SIZE

    private val biSize = BITMAPINFOHEADER_SIZE
    private var biWidth = 0
    private var biHeight = 0
    private val biPlanes = 1
    private val biBitCount = 24
    private val biCompression = 0
    private var biSizeImage = 0x030000
    private val biXPelsPerMeter = 0x0
    private val biYPelsPerMeter = 0x0
    private val biClrUsed = 0
    private val biClrImportant = 0
    private var pixels: IntArray? = null
    private var buffer: ByteBuffer? = null
    private var outputStream: OutputStream? = null

    fun saveBitmap(
        parFilename: String?,
        bitmap: Bitmap
    ) {
        try {
            outputStream = FileOutputStream(parFilename)
            save(bitmap)
            (outputStream as FileOutputStream).close()
        } catch (e: Exception) {
            Log.e("Exception", e.message)
        }
    }

//    fun saveBitmap(
//        bitmap: Bitmap,
//        outputStream: OutputStream?
//    ) {
//        this.outputStream = outputStream
//        save(bitmap)
//    }

    private fun save(
        bitmap: Bitmap
    ) {
        try {

            convertImage(bitmap)
            writeBitmapFileHeader()
            writeBitmapInfoHeader()
            writeBitmap()

            outputStream!!.write(buffer!!.array())

        } catch (e: Exception) {
            Log.e("Exception", e.message)
        }
    }

    private fun convertImage(
        bitmap: Bitmap
    ): Boolean {
        val pad: Int
        val width = bitmap.width
        val height = bitmap.height
        pixels = IntArray(width * height)
        bitmap.getPixels(
            pixels,
            0,
            width,
            0,
            0,
            width,
            height
        )
        pad = (4 - width * 3 % 4) * height
        biSizeImage = width * height * 3 + pad
        bfSize = biSizeImage + BITMAPFILEHEADER_SIZE +
                BITMAPINFOHEADER_SIZE
        buffer = ByteBuffer.allocate(bfSize)
        biWidth = width
        biHeight = height
        return true
    }

    private fun writeBitmap() {
        val size: Int
        var value: Int
        var j: Int
        var i: Int
        var rowCount: Int
        var rowIndex: Int
        var lastRowIndex: Int
        var pad: Int
        var padCount: Int
        val rgb = ByteArray(3)
        size = biWidth * biHeight - 1
        pad = 4 - biWidth * 3 % 4
        if (pad == 4)
            pad = 0
        rowCount = 1
        padCount = 0
        rowIndex = size - biWidth
        lastRowIndex = rowIndex
        try {
            j = 0
            while (j < size) {
                value = pixels!!.get(rowIndex)
                rgb[0] = (value and 0xFF).toByte()
                rgb[1] = (value shr 8 and 0xFF).toByte()
                rgb[2] = (value shr 16 and 0xFF).toByte()
                buffer!!.put(rgb)
                if (rowCount == biWidth) {
                    padCount += pad
                    i = 1
                    while (i <= pad) {
                        buffer!!.put(0x00.toByte())
                        i++
                    }
                    rowCount = 1
                    rowIndex = lastRowIndex - biWidth
                    lastRowIndex = rowIndex
                } else rowCount++
                rowIndex++
                j++
            }
            // Update the size of the file
            bfSize += padCount - pad
            biSizeImage += padCount - pad
        } catch (e: Exception) {
            Log.e("Exception", e.message)
        }
    }

    private fun writeBitmapFileHeader() {
        try {
            buffer!!.put(bfType)
            buffer!!.put(intToDWord(bfSize))
            buffer!!.put(intToWord(bfReserved1))
            buffer!!.put(intToWord(bfReserved2))
            buffer!!.put(intToDWord(bfOffBits))
        } catch (e: Exception) {
            Log.e("Exception", e.message)
        }
    }

    private fun writeBitmapInfoHeader() {
        try {
            buffer!!.put(intToDWord(biSize))
            buffer!!.put(intToDWord(biWidth))
            buffer!!.put(intToDWord(biHeight))
            buffer!!.put(intToWord(biPlanes))
            buffer!!.put(intToWord(biBitCount))
            buffer!!.put(intToDWord(biCompression))
            buffer!!.put(intToDWord(biSizeImage))
            buffer!!.put(intToDWord(biXPelsPerMeter))
            buffer!!.put(intToDWord(biYPelsPerMeter))
            buffer!!.put(intToDWord(biClrUsed))
            buffer!!.put(intToDWord(biClrImportant))
        } catch (e: Exception) {
            Log.e("Exception", e.message)
        }
    }

    private fun intToWord(
        parValue: Int
    ): ByteArray {
        val retValue = ByteArray(2)
        retValue[0] = (parValue and 0x00FF).toByte()
        retValue[1] = (parValue shr 8 and 0x00FF).toByte()
        return retValue
    }

    private fun intToDWord(
        parValue: Int
    ): ByteArray {
        val retValue = ByteArray(4)
        retValue[0] = (parValue and 0x00FF).toByte()
        retValue[1] = (parValue shr 8 and 0x000000FF).toByte()
        retValue[2] = (parValue shr 16 and 0x000000FF).toByte()
        retValue[3] = (parValue shr 24 and 0x000000FF).toByte()
        return retValue
    }

    companion object {
        // Private constants
        private const val BITMAPFILEHEADER_SIZE = 14
        private const val BITMAPINFOHEADER_SIZE = 40
    }
}