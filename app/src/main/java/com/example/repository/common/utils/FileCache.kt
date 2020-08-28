package com.example.repository.common.utils

import android.content.Context
import android.os.Environment
import java.io.File

class FileCache(context: Context) {

    private var cacheDir: File? = null

    init {
        //Find the dir to save cached images
        if (Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED) cacheDir =
            File(Environment.getExternalStorageDirectory(), "TempImages") else cacheDir =
            context.cacheDir
        if (!cacheDir?.exists()!!) cacheDir?.mkdirs()
    }

    fun getFile(url: String): File? {
        val filename = url.hashCode().toString()
        return File(cacheDir, filename)
    }

    fun clear() {
        val files: Array<File> = cacheDir?.listFiles() ?: return
        for (f in files) f.delete()
    }


}