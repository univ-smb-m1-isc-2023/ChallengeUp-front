package fr.usmb.challengeup.utils

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.widget.ImageView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL

/**
 * Classe utilitaire pour afficher une image depuis une URL en ligne dans une ImageView
 */
class DownloadImageTask(private val imageView: ImageView) {

    fun downloadImage(url: String) {
        CoroutineScope(Dispatchers.Main).launch {
            val bitmap = withContext(Dispatchers.IO) { downloadImageFromUrl(url) }
            imageView.setImageBitmap(bitmap)
        }
    }

    private fun downloadImageFromUrl(urlString: String): Bitmap? {
        return try {
            val url = URL(urlString)
            val connection: HttpURLConnection = url.openConnection() as HttpURLConnection
            connection.doInput = true
            connection.connect()
            val input: InputStream = connection.inputStream
            BitmapFactory.decodeStream(input)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}
