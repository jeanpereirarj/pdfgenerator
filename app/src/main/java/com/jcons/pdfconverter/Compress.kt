package com.jcons.pdfconverter

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import java.io.ByteArrayOutputStream


object Compress {

    fun compressBitmap(bmp: Bitmap, quality: Int): Bitmap {

        var scaledBitmap: Bitmap? = null
        var actualHeight = bmp.height
        var actualWidth = bmp.width

        var imgRatio = actualWidth.toFloat() / actualHeight.toFloat()
        val maxRatio = DEFAULT_WIDTH / DEFAULT_HEIGTH

        if (actualHeight > DEFAULT_HEIGTH || actualWidth > DEFAULT_WIDTH) {

            when {
                imgRatio < maxRatio -> {
                    imgRatio = (DEFAULT_HEIGTH.toFloat() / actualHeight)
                    actualWidth = (imgRatio * actualWidth).toInt()
                    actualHeight = DEFAULT_HEIGTH
                }
                imgRatio > maxRatio -> {
                    imgRatio = (DEFAULT_WIDTH.toFloat() / actualWidth)
                    actualHeight = (imgRatio * actualHeight).toInt()
                    actualWidth = DEFAULT_WIDTH
                }
                else -> {
                    actualHeight = DEFAULT_HEIGTH
                    actualWidth = DEFAULT_WIDTH
                }
            }
        }

        try {
            scaledBitmap = Bitmap.createScaledBitmap(bmp, actualWidth, actualHeight, true)
        } catch (exception: OutOfMemoryError) {
            exception.printStackTrace()
        }

        val out = ByteArrayOutputStream()
        scaledBitmap!!.compress(Bitmap.CompressFormat.JPEG, quality, out)

        return BitmapFactory.decodeByteArray(out.toByteArray(), 0, out.toByteArray().size)
    }
}

private const val DEFAULT_WIDTH: Int = 612
private const val DEFAULT_HEIGTH: Int = 816