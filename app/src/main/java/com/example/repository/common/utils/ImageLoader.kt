package com.example.repository.common.utils

import android.app.Activity
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.widget.ImageView
import com.example.repository.R
import com.example.repository.common.App
import java.io.*
import java.net.HttpURLConnection
import java.net.URL
import java.util.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class ImageLoader() {

    var memoryCache =
        MemoryCache()
    var fileCache: FileCache? = null
    private val imageViews =
        Collections.synchronizedMap(WeakHashMap<ImageView, String>())
    var executorService: ExecutorService? = null

    init {
        fileCache = FileCache(App.app)
        executorService = Executors.newFixedThreadPool(5)
    }

    var stub_id: Int = R.drawable.ic_loading
    fun DisplayImage(
        url: String,
        loader: Int,
        imageView: ImageView
    ) {
        stub_id = loader
        imageViews[imageView] = url
        val bitmap = memoryCache[url]
        if (bitmap != null) imageView.setImageBitmap(bitmap) else {
            queuePhoto(url, imageView)
            imageView.setImageResource(loader)
        }
    }

    private fun queuePhoto(
        url: String,
        imageView: ImageView
    ) {
        val p = PhotoToLoad(url, imageView)
        executorService!!.submit(PhotosLoader(p))
    }

    private fun getBitmap(url: String): Bitmap? {
        val f = fileCache!!.getFile(url)

        //from SD cache
        val b = decodeFile(f)
        return b
            ?: try {
                var bitmap: Bitmap? = null
                val imageUrl = URL(url)
                val conn =
                    imageUrl.openConnection() as HttpURLConnection
                conn.connectTimeout = 30000
                conn.readTimeout = 30000
                conn.instanceFollowRedirects = true
                val `is` = conn.inputStream
                val os: OutputStream = FileOutputStream(f)
                Utils().CopyStream(`is`, os)
                os.close()
                bitmap = decodeFile(f)
                bitmap
            } catch (ex: Exception) {
                ex.printStackTrace()
                null
            }

        //from web
    }

    //decodes image and scales it to reduce memory consumption
    private fun decodeFile(f: File?): Bitmap? {
        try {
            //decode image size
            val o = BitmapFactory.Options()
            o.inJustDecodeBounds = true
            BitmapFactory.decodeStream(FileInputStream(f), null, o)

            //Find the correct scale value. It should be the power of 2.
            val REQUIRED_SIZE = 70
            var width_tmp = o.outWidth
            var height_tmp = o.outHeight
            var scale = 1
            while (true) {
                if (width_tmp / 2 < REQUIRED_SIZE || height_tmp / 2 < REQUIRED_SIZE) break
                width_tmp /= 2
                height_tmp /= 2
                scale *= 2
            }

            //decode with inSampleSize
            val o2 = BitmapFactory.Options()
            o2.inSampleSize = scale
            return BitmapFactory.decodeStream(FileInputStream(f), null, o2)
        } catch (e: FileNotFoundException) {
        }
        return null
    }

    //Task for the queue
    class PhotoToLoad(var url: String, var imageView: ImageView)

    internal class PhotosLoader(var photoToLoad: PhotoToLoad) : Runnable {
        override fun run() {
           // if (ImageLoader(App.app).imageViewReused(photoToLoad)) return
            val bmp: Bitmap? = ImageLoader().getBitmap(photoToLoad.url)
            ImageLoader().memoryCache.put(photoToLoad.url, bmp)
          //  if (ImageLoader(App.app).imageViewReused(photoToLoad)) return
            val bd = BitmapDisplayer(bmp, photoToLoad)
            val a = photoToLoad.imageView.context as Activity
            a.runOnUiThread(bd)
        }

    }


    fun imageViewReused(photoToLoad: PhotoToLoad): Boolean {
        val tag = imageViews[photoToLoad.imageView]
        return if (tag == null || tag != photoToLoad.url) true else false
    }


    //Used to display bitmap in the UI thread
    internal class BitmapDisplayer(var bitmap: Bitmap?, var photoToLoad: PhotoToLoad) : Runnable {
        override fun run() {
           // if (ImageLoader(App.app).imageViewReused(photoToLoad)) return
            if (bitmap != null) photoToLoad.imageView.setImageBitmap(bitmap) else photoToLoad.imageView.setImageResource(
                ImageLoader().stub_id
            )
        }

    }

    fun clearCache() {
        memoryCache.clear()
        fileCache!!.clear()
    }


/*

    var memoryCache =
        MemoryCache()
    var fileCache: FileCache? = null
    var utils: Utils = Utils()
    private val imageViews: MutableMap<ImageView, String> =
        Collections.synchronizedMap(WeakHashMap<ImageView, String>())
    var executorService: ExecutorService? = null

    init {
        fileCache = context?.let { FileCache(it) }
        executorService = Executors.newFixedThreadPool(5)
    }

    var stub_id: Int = R.drawable.bg_fields_edit
    fun DisplayImage(url: String, loader: Int, imageView: ImageView) {
        stub_id = loader
        imageViews[imageView] = url
        val bitmap = memoryCache[url]
        if (bitmap != null) imageView.setImageBitmap(bitmap) else {
            queuePhoto(url, imageView)
            imageView.setImageResource(loader)
        }
    }

    private fun queuePhoto(url: String, imageView: ImageView) {
        val p = PhotoToLoad(url, imageView)
        executorService?.submit(PhotosLoader(p))
    }

    private fun getBitmap(url: String): Bitmap? {
        val f: File? = fileCache!!.getFile(url)

        //from SD cache
        val b = decodeFile(f)
        return b
            ?: try {
                var bitmap: Bitmap? = null
                val imageUrl = URL(url)
                val conn: HttpURLConnection = imageUrl.openConnection() as HttpURLConnection
                conn.setConnectTimeout(30000)
                conn.setReadTimeout(30000)
                conn.setInstanceFollowRedirects(true)
                val `is`: InputStream = conn.getInputStream()
                val os: OutputStream = FileOutputStream(f)
                utils.CopyStream(`is`, os)
                os.close()
                bitmap = decodeFile(f)
                bitmap
            } catch (ex: Exception) {
                ex.printStackTrace()
                null
            }

        //from web
    }

    //decodes image and scales it to reduce memory consumption
    private fun decodeFile(f: File?): Bitmap? {
        try {
            //decode image size
            val o = BitmapFactory.Options()
            o.inJustDecodeBounds = true
            BitmapFactory.decodeStream(FileInputStream(f), null, o)

            //Find the correct scale value. It should be the power of 2.
            val REQUIRED_SIZE = 70
            var width_tmp = o.outWidth
            var height_tmp = o.outHeight
            var scale = 1
            while (true) {
                if (width_tmp / 2 < REQUIRED_SIZE || height_tmp / 2 < REQUIRED_SIZE) break
                width_tmp /= 2
                height_tmp /= 2
                scale *= 2
            }

            //decode with inSampleSize
            val o2 = BitmapFactory.Options()
            o2.inSampleSize = scale
            return BitmapFactory.decodeStream(FileInputStream(f), null, o2)
        } catch (e: FileNotFoundException) {
        }
        return null
    }

    //Task for the queue
    class PhotoToLoad(var url: String, i: ImageView) {
        var imageView: ImageView

        init {
            imageView = i
        }
    }

    internal class PhotosLoader(var photoToLoad: PhotoToLoad) : Runnable {
        override fun run() {
            if (ImageLoader(App.app).imageViewReused(photoToLoad)) return
            val bmp: Bitmap? = ImageLoader(App.app).getBitmap(photoToLoad.url)
            ImageLoader(App.app).memoryCache.put(photoToLoad.url, bmp)
            if (ImageLoader(App.app).imageViewReused(photoToLoad)) return
            val bd = BitmapDisplayer(bmp, photoToLoad)
            val a = photoToLoad.imageView.getContext() as Activity
            a.runOnUiThread(bd)
        }

    }

    fun imageViewReused(photoToLoad: PhotoToLoad): Boolean {
        val tag = imageViews[photoToLoad.imageView]
        return if (tag == null || tag != photoToLoad.url) true else false
    }

    //Used to display bitmap in the UI thread
    internal class BitmapDisplayer(var bitmap: Bitmap?, var photoToLoad: PhotoToLoad) : Runnable {
        override fun run() {
            if (ImageLoader(App.app).imageViewReused(photoToLoad)) return
            if (bitmap != null) photoToLoad.imageView.setImageBitmap(bitmap) else photoToLoad.imageView.setImageResource(
                ImageLoader(App.app).stub_id
            )
        }

    }

    fun clearCache() {
        memoryCache.clear()
        fileCache!!.clear()
    }
*/

}