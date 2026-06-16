package com.example.aulix.util

import android.graphics.Bitmap
import android.graphics.Color
import com.google.zxing.BarcodeFormat
import com.google.zxing.qrcode.QRCodeWriter

fun generarQrBitmap(contenido: String, tamano: Int = 512): Bitmap {
    val bits = QRCodeWriter().encode(contenido, BarcodeFormat.QR_CODE, tamano, tamano)
    val bmp = Bitmap.createBitmap(tamano, tamano, Bitmap.Config.RGB_565)
    for (x in 0 until tamano) {
        for (y in 0 until tamano) {
            bmp.setPixel(x, y, if (bits[x, y]) Color.BLACK else Color.WHITE)
        }
    }
    return bmp
}
