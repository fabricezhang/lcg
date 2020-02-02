package top.easelink.lcg.utils

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import timber.log.Timber
import top.easelink.lcg.LCGApp
import java.io.*


fun saveImageToGallery(bmp: Bitmap, bitName: String): String {
    val appDir = File(LCGApp.getContext().externalCacheDir, "lcg")
    if (!appDir.exists()) {
        appDir.mkdir()
    }
    val fileName = "$bitName.png"
    val file = File(appDir, fileName)
    try {
        val fos = FileOutputStream(file)
        bmp.compress(Bitmap.CompressFormat.PNG, 85, fos)
        fos.flush()
        fos.close()
    } catch (e: FileNotFoundException) {
        Timber.e(e)
    } catch (e: IOException) {
        Timber.e(e)
    } finally {
        return file.path
    }
}

fun saveBmp2Gallery(context: Context, bmp: Bitmap, picName: String) {
    var fileName: String? = null
    val galleryPath = "${Environment.getExternalStorageDirectory()}/${Environment.DIRECTORY_DCIM}/Camera/"
    var file: File? = null
    var outStream: FileOutputStream? = null
    try {
        file = File(galleryPath, "$picName.jpg")
        fileName = file.toString()
        outStream = FileOutputStream(fileName)
        bmp.compress(Bitmap.CompressFormat.JPEG, 90, outStream)
    } catch (e: Exception) {
        Timber.e(e)
    } finally {
        try {
            outStream?.close()
        } catch (e: IOException) {
            Timber.e(e)
        }
    }
    MediaStore.Images.Media.insertImage(context.contentResolver, bmp, fileName, null)
    val intent = Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE)
    val uri: Uri = Uri.fromFile(file)
    intent.data = uri
    context.sendBroadcast(intent)
    showMessage(context, "图片保存成功")
}


fun syncSystemGallery(context: Context, path: String, fileName: String) {
    val file = File(path)
    if (!file.exists()) {
        Timber.e("File not exists!")
        return
    }
    try {
        MediaStore.Images.Media.insertImage(
            context.contentResolver,
            file.absolutePath, fileName, null
        )
    } catch (e: FileNotFoundException) {
        Timber.e(e)
    }
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
        val paths = arrayOf<String>(file.absolutePath)
        MediaScannerConnection.scanFile(context, paths, null, null)
    } else {
        val intent: Intent
        if (file.isDirectory) {
            intent = Intent(Intent.ACTION_MEDIA_MOUNTED)
            intent.setClassName(
                "com.android.providers.media",
                "com.android.providers.media.MediaScannerReceiver"
            )
            intent.data = Uri.fromFile(Environment.getExternalStorageDirectory())
        } else {
            intent = Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE)
            intent.data = Uri.fromFile(file)
        }
        context.sendBroadcast(intent)
    }
}


/**
 * 读取assets本地json
 * @param fileName
 * @param context
 * @return json String
 */
fun getJsonStringFromAssets(fileName: String, context: Context): String {
    val stringBuilder = StringBuilder()
    try {
        val bf = BufferedReader(InputStreamReader(context.assets.open(fileName)))
        var line: String?
        while (bf.readLine().also { line = it } != null) {
            stringBuilder.append(line)
        }
    } catch (e: IOException) {
        e.printStackTrace()
    }
    return stringBuilder.toString()
}