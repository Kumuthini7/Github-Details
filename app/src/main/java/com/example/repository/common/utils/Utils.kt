package com.example.repository.common.utils

import java.io.InputStream
import java.io.OutputStream

class Utils {
    fun CopyStream(`is`: InputStream, os: OutputStream) {
        val buffer_size = 1024
        try {
            val bytes = ByteArray(buffer_size)
            while (true) {
                val count: Int = `is`.read(bytes, 0, buffer_size)
                if (count == -1) break
                os.write(bytes, 0, count)
            }
        } catch (ex: Exception) {
        }
    }

}