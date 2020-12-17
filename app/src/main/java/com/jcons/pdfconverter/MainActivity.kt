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

        var newFile = File(downloads, "teste.png")
        if (newFile.exists()) newFile.delete()

        photo = Compress.compressBitmap(photo, 85)

        if (newFile.exists()) newFile.delete()
        try {
            val out = FileOutputStream(newFile)
            photo.compress(Bitmap.CompressFormat.PNG, 100, out)
            out.flush()
            out.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }

        //Cria o Bitmap
        newFile = File(downloads, "teste.bmp")
        AndroidBmpUtil().saveBitmap(newFile.absolutePath, photo)


        //Cria o PDF
        val mPDFWriter = PDFWriter()
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