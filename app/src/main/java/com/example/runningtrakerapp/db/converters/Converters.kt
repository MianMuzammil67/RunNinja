package com.example.runningtrakerapp.db.converters

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.room.TypeConverter
import java.io.ByteArrayOutputStream


class Converters {

//    @TypeConverter
//    fun toBitmap(bytes: ByteArray): Bitmap {
//        return BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
//    }
//
//    @TypeConverter
//    fun fromBitmap(bmp: Bitmap): ByteArray {
//        val outputStream = ByteArrayOutputStream()
//        bmp.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
//        return outputStream.toByteArray()
//    }

    @TypeConverter
    fun toBitmap(bytes: ByteArray?): Bitmap? {
        // Handle null byte arrays
        return bytes?.let { BitmapFactory.decodeByteArray(it, 0, it.size) }
    }

    @TypeConverter
    fun fromBitmap(bitmap: Bitmap?): ByteArray? {
        // Handle null Bitmaps
        return bitmap?.let {
            val outputStream = ByteArrayOutputStream()
            it.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
            outputStream.toByteArray()
        }
    }
}