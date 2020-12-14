package com.jcons.pdfconverter

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.os.Environment
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.jcons.pdfconverter.pdfconverter.PDFWriter
import com.jcons.pdfconverter.pdfconverter.PaperSize
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), 225)
        }else{
            gerarPDF()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        gerarPDF()
    }

    fun gerarPDF(){

        val photo = (ContextCompat.getDrawable(this, R.drawable.dino) as BitmapDrawable?)!!.bitmap

        val mPDFWriter = PDFWriter()
        mPDFWriter.addImage(0, 0, photo)
        mPDFWriter.newPage()
        mPDFWriter.addImage(0, 0, photo)

        val pdfContent = mPDFWriter.asString()
        val downloads =
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
        val newFile = File(downloads, "teste.pdf")

        try {
            newFile.createNewFile()
            try {
                val pdfFile = FileOutputStream(newFile)
                pdfFile.write(pdfContent.toByteArray(charset("ISO-8859-1")))
                pdfFile.close()

                Log.w("PDF", "Successo")

            } catch (e: FileNotFoundException) {
                Log.w("PDF", e)
            }
        } catch (e: IOException) {
            Log.w("PDF", e)
        }
    }

    fun resizedBitmap(bm: Bitmap): Bitmap {
        val maxSize = 100
        val outWidth: Int
        val outHeight: Int
        val inWidth = bm.width
        val inHeight = bm.height

        if (inWidth > inHeight) {
            outWidth = maxSize
            outHeight = inHeight * maxSize / inWidth
        } else {
            outHeight = maxSize
            outWidth = inWidth * maxSize / inHeight
        }

        return Bitmap.createScaledBitmap(bm, outWidth, outHeight, false)
    }

}