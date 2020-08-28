package com.example.repository.common.utils

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.widget.ImageView
import java.io.IOException
import java.net.MalformedURLException
import java.net.URL


class ImageUtils {
    companion object {
        var bmImg: Bitmap? = null

        fun downloadImagefile(url: String, img: ImageView) {
            var imageUrl: URL? = null
            try {
                imageUrl = URL(url)

            } catch (e: MalformedURLException) {
                e.printStackTrace()
            }

            try {
                val conn = imageUrl?.openConnection()
                conn?.setDoInput(true)
                conn?.connect()
                val length = conn?.getContentLength()
                val bitmapData = length?.let { IntArray(it) }
                val bitmapData2 = length?.let { ByteArray(it) }
                val `is` = conn?.getInputStream()
                val options = BitmapFactory.Options()

                bmImg = BitmapFactory.decodeStream(`is`, null, options)

                img.setImageBitmap(bmImg)

            } catch (e: IOException) {
                e.printStackTrace()
            }


        }
    }


}