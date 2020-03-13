package com.uuzuche.lib_zxing

import android.graphics.Bitmap

/**
 * 二维码解析回调。
 */
interface QRCodeAnalyzeCallback {
    fun onQRCodeAnalyzeSuccess(bitmap: Bitmap, result: String?)

    fun onQRCodeAnalyzeFailed()
}