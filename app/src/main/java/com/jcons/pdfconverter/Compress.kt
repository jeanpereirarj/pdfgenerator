package com.jcons.pdfconverter

import android.graphics.*
import android.media.ExifInterface
import android.util.Log
import id.zelory.compressor.compressFormat
import id.zelory.compressor.overWrite
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

fun compressFile(imageFile: File, bitmap: Bitmap, format: Bitmap.CompressFormat = Bitmap.CompressFormat.JPEG, quality: Int = 100): File {

    val result = if (format == imageFile.compressFormat()) {
        imageFile
    } else {
        File("${imageFile.absolutePath.substringBeforeLast(".")}.${format.extension()}")
    }

    imageFile.delete()
    compressBitmap(bitmap, result, quality)
    return result
}

fun compressBitmap(bitmap: Bitmap, destination: File, quality: Int = 100) {

    destination.parentFile?.mkdirs()
    var fileOutputStream: FileOutputStream? = null
    try {
        fileOutputStream = FileOutputStream(destination.absolutePath)
        bitmap.compress(Bitmap.CompressFormat.JPEG, quality, fileOutputStream)
    } finally {
        fileOutputStream?.run {
            flush()
            close()
        }
    }
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

fun compressImage(image: Bitmap, quality: Int = 100): Bitmap {

    val baos = ByteArrayOutputStream()

    image.compress(
        Bitmap.CompressFormat.JPEG,
        100,
        baos
    )

    baos.reset()

    image.compress(
        Bitmap.CompressFormat.JPEG,
        quality,
        baos
    )

    var percent = 95

    var image = image
    while (baos.toByteArray().size / 1024 > 3000) {
        baos.reset()
        image = Bitmap.createScaledBitmap(image, (image.width*(percent/100)).toInt(), (image.height*(percent/100)).toInt(), true);
        percent -= 2
    }

    image.recycle()
    return BitmapFactory.decodeByteArray(baos.toByteArray(), 0, baos.toByteArray()?.size!!)

}

fun scaleDown(realImage: Bitmap, maxImageSize: Float, filter: Boolean): Bitmap {
    val ratio = Math.min(maxImageSize / realImage.width, maxImageSize / realImage.height)

    val width = Math.round(ratio * realImage.width)
    val height = Math.round(ratio * realImage.height)

    var bitmap =  Bitmap.createScaledBitmap(
        realImage, width,
        height, filter
    )

    if (bitmap != realImage) {
        realImage.recycle();
    }else{
        Log.e("ImageService", "estava com erro")
    }

    return bitmap
}

object Compress {

    fun compressBitmap(bmp: Bitmap): Bitmap {

        var scaledBitmap: Bitmap? = null
        var actualHeight = bmp.height
        var actualWidth = bmp.width

        var imgRatio = actualWidth.toFloat() / actualHeight.toFloat()
        val maxRatio = DEFAULT_WIDTH / DEFAULT_HEIGTH

        if (actualHeight > DEFAULT_HEIGTH || actualWidth > DEFAULT_WIDTH) {

            when {
                imgRatio < maxRatio -> {
                    imgRatio = (DEFAULT_HEIGTH / actualHeight).toFloat()
                    actualWidth = (imgRatio * actualWidth) as Int
                    actualHeight = DEFAULT_HEIGTH
                }
                imgRatio > maxRatio -> {
                    imgRatio = (DEFAULT_WIDTH / actualWidth).toFloat()
                    actualHeight = (imgRatio * actualHeight) as Int
                    actualWidth = DEFAULT_WIDTH
                }
                else -> {
                    actualHeight = DEFAULT_HEIGTH
                    actualWidth = DEFAULT_WIDTH
                }
            }
        }

        try {
            scaledBitmap = Bitmap.createBitmap(actualWidth, actualHeight, Bitmap.Config.ARGB_8888)
        } catch (exception: OutOfMemoryError) {
            exception.printStackTrace()
        }

        val out = ByteArrayOutputStream()
        scaledBitmap!!.compress(Bitmap.CompressFormat.JPEG, 85, out)

        return BitmapFactory.decodeByteArray(out.toByteArray(), 0, out.toByteArray().size)
    }
}

fun compressorFile(imageFile: File, quality: Int){
    decodeSampledBitmapFromFile(imageFile, DEFAULT_WIDTH, DEFAULT_HEIGTH).run {
        determineImageRotation(imageFile, this).run {
            overWrite(imageFile, this, imageFile.compressFormat(), quality)
        }
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
    // Raw height and width of image
    val (height: Int, width: Int) = options.run { outHeight to outWidth }
    var inSampleSize = 1

    if (height > reqHeight || width > reqWidth) {

        val halfHeight: Int = height / 2
        val halfWidth: Int = width / 2

        // Calculate the largest inSampleSize value that is a power of 2 and keeps both
        // height and width larger than the requested height and width.
        while (halfHeight / inSampleSize >= reqHeight && halfWidth / inSampleSize >= reqWidth) {
            inSampleSize *= 2
        }
    }

    return inSampleSize
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

private const val DEFAULT_WIDTH: Int = 612
private const val DEFAULT_HEIGTH: Int = 816