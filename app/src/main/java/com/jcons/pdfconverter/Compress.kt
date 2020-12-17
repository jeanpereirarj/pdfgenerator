package com.jcons.pdfconverter

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.ExifInterface
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream


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


fun compressorFile(imageFile: File, quality: Int){
    decodeSampledBitmapFromFile(imageFile, DEFAULT_WIDTH, DEFAULT_HEIGTH).run {
        overWrite(imageFile, this, imageFile.compressFormat(), quality)
    }
}

private fun decodeSampledBitmapFromFile(imageFile: File, reqWidth: Int, reqHeight: Int): Bitmap {
    return BitmapFactory.Options().run {
        inJustDecodeBounds = true
        BitmapFactory.decodeFile(imageFile.absolutePath, this)

        inSampleSize = calculateInSampleSize(this, reqWidth, reqHeight)

        inJustDecodeBounds = false
        BitmapFactory.decodeFile(imageFile.absolutePath, this)
    }
}

private fun calculateInSampleSize(options: BitmapFactory.Options, reqWidth: Int, reqHeight: Int): Int {

    val (height: Int, width: Int) = options.run { outHeight to outWidth }
    var inSampleSize = 1

    if (height > reqHeight || width > reqWidth) {

        val halfHeight: Int = height / 2
        val halfWidth: Int = width / 2

        while (halfHeight / inSampleSize >= reqHeight && halfWidth / inSampleSize >= reqWidth) {
            inSampleSize *= 2
        }
    }

    return inSampleSize
}

fun overWrite(imageFile: File, bitmap: Bitmap, format: Bitmap.CompressFormat = imageFile.compressFormat(), quality: Int = 100): File {
    val result = if (format == imageFile.compressFormat()) {
        imageFile
    } else {
        File("${imageFile.absolutePath.substringBeforeLast(".")}.${format.extension()}")
    }
    imageFile.delete()
    saveBitmap(bitmap, result, format, quality)
    return result
}

private fun determineImageRotation(imageFile: File, bitmap: Bitmap): Bitmap {
    val exif = ExifInterface(imageFile.absolutePath)
    val orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, 0)
    val matrix = Matrix()
    when (orientation) {
        6 -> matrix.postRotate(90f)
        3 -> matrix.postRotate(180f)
        8 -> matrix.postRotate(270f)
    }
    return Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
}

fun File.compressFormat() = when (extension.toLowerCase()) {
    "png" -> Bitmap.CompressFormat.PNG
    "webp" -> Bitmap.CompressFormat.WEBP
    else -> Bitmap.CompressFormat.JPEG
}

fun Bitmap.CompressFormat.extension() = when (this) {
    Bitmap.CompressFormat.PNG -> "png"
    Bitmap.CompressFormat.WEBP -> "webp"
    else -> "jpg"
}

fun saveBitmap(bitmap: Bitmap, destination: File, format: Bitmap.CompressFormat = destination.compressFormat(), quality: Int = 100) {
    destination.parentFile?.mkdirs()
    var fileOutputStream: FileOutputStream? = null
    try {
        fileOutputStream = FileOutputStream(destination.absolutePath)
        bitmap.compress(format, quality, fileOutputStream)
    } finally {
        fileOutputStream?.run {
            flush()
            close()
        }
    }
}