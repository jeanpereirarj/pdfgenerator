package com.jcons.pdfconverter

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.os.Environment
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.jcons.pdfconverter.pdfconverter.PDFWriter
import id.zelory.compressor.Compressor
import id.zelory.compressor.constraint.default
import id.zelory.compressor.constraint.size
import kotlinx.coroutines.launch
import java.io.*


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

        var photo = (ContextCompat.getDrawable(this, R.drawable.dino) as BitmapDrawable?)!!.bitmap

        val downloads =
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
        var file = File(downloads, "dino.jpg")
        var newFile = File(downloads, "teste.png")
        if (newFile.exists()) newFile.delete()

        try {
            val out = FileOutputStream(newFile)
            photo.compress(Bitmap.CompressFormat.PNG, 100, out)
            out.flush()
            out.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }


        photo = Compress.compressBitmap(photo)


        var newFile2 = File(downloads, "teste2.png")
        if (newFile.exists()) newFile.delete()

        try {
            val out = FileOutputStream(newFile)
            photo.compress(Bitmap.CompressFormat.PNG, 100, out)
            out.flush()
            out.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }

//
//
//        newFile.createNewFile()
//        var bmpfile = FileOutputStream(newFile)
//        AndroidBmpUtil().saveBitmap(photo, bmpfile)
//
//        newFile = File(downloads, "teste.jpg")
//        if (newFile.exists()) newFile.delete()
//
//        try {
//            val out = FileOutputStream(newFile)
//            photo.compress(Bitmap.CompressFormat.JPEG, 100, out)
//            out.flush()
//            out.close()
//        } catch (e: Exception) {
//            e.printStackTrace()
//        }
//
//
//        var bmp = scaleDown(photo, 1280f, true)
//        newFile = File(downloads, "teste_compress.bmp")
//        newFile.createNewFile()
//        bmpfile = FileOutputStream(newFile)
//        AndroidBmpUtil().saveBitmap(bmp, bmpfile)



        val mPDFWriter = PDFWriter()
        mPDFWriter.addImage(0, 0, photo)
        mPDFWriter.newPage()
        mPDFWriter.addImage(0, 0, photo)

        val pdfContent = mPDFWriter.asString()

        newFile = File(downloads, "teste.pdf")


        try {
            newFile.createNewFile()
            try {
                val pdfFile = FileOutputStream(newFile)
                pdfFile.write(pdfContent.toByteArray(charset("ISO-8859-1")))
                pdfFile.close()


            } catch (e: FileNotFoundException) {

            }
        } catch (e: IOException) {

        }


    }

}