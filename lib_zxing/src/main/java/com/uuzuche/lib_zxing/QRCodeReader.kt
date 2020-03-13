package com.uuzuche.lib_zxing

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.ParcelFileDescriptor
import com.google.zxing.BinaryBitmap
import com.google.zxing.DecodeHintType
import com.google.zxing.MultiFormatReader
import com.google.zxing.common.HybridBinarizer
import com.uuzuche.lib_zxing.camera.BitmapLuminanceSource
import com.uuzuche.lib_zxing.decoding.DecodeFormatManager
import java.io.File
import java.io.FileNotFoundException


/**
 * 从 Uri 扫描二维码。扫描结果会通过传入的 [callback] 回调。
 */
fun scanQRCodeFromUri(context: Context, uri: Uri, callback: QRCodeAnalyzeCallback) {
    val pd = ParcelFileDescriptor(context.contentResolver.openFileDescriptor(uri, "r"))
    val bitmap = BitmapFactory.decodeFileDescriptor(pd.fileDescriptor)
    pd.close()
    scanQRCodeFromBitmap(bitmap, callback)
}

/**
 * 从文件的绝对路径扫描二维码。扫描结果会通过传入的 [callback] 回调。
 */
fun scanQRCodeFromFile(path: String, callback: QRCodeAnalyzeCallback) {
    //Do a leading check
    val file = File(path)
    if (!file.exists() || !file.canRead()) {
        throw FileNotFoundException("File $path does not exist or cannot read!")
    }

    val decodeOptions = BitmapFactory.Options()
    decodeOptions.inJustDecodeBounds = true
    BitmapFactory.decodeFile(path, decodeOptions)
    decodeOptions.inJustDecodeBounds = false
    var sampleSize = (decodeOptions.outHeight / 400F).toInt()
    if (sampleSize <= 0) {
        sampleSize = 1
    }
    decodeOptions.inSampleSize = sampleSize
    val bitmap = BitmapFactory.decodeFile(path, decodeOptions)
    scanQRCodeFromBitmap(bitmap, callback)
}

/**
 * 从 Bitmap 对象扫描二维码。扫描结果会通过传入的 [callback] 回调。
 */
fun scanQRCodeFromBitmap(bitmap: Bitmap, callback: QRCodeAnalyzeCallback) {
    val reader = MultiFormatReader()
    val hints = mapOf(
            DecodeHintType.POSSIBLE_FORMATS to listOf(
                    DecodeFormatManager.QR_CODE_FORMATS,
                    DecodeFormatManager.QR_CODE_FORMATS,
                    DecodeFormatManager.DATA_MATRIX_FORMATS),
            DecodeHintType.CHARACTER_SET to "UTF8")
    reader.setHints(hints)
    val rawResult = try {
        reader.decodeWithState(BinaryBitmap(HybridBinarizer(BitmapLuminanceSource(bitmap))))
    } catch (e: Exception) {
        null
    }
    if (rawResult != null) {
        callback.onQRCodeAnalyzeSuccess(bitmap, rawResult.text)
    } else {
        callback.onQRCodeAnalyzeFailed()
    }
}