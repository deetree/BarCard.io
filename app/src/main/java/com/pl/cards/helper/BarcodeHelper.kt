package com.pl.cards.helper

class BarcodeHelper {

    val barcodeTypes = arrayOf(
        "UPC-A",
        "UPC-E",
        "EAN-8",
        "EAN-13",
        "Code 39",
        "Code 93",
        "Code 128",
        "ITF",
        "Codabar",
        "RSS-14",
        "RSS Expanded",
        "QR Code",
        "Data Matrix",
        "Aztec",
        "PDF 417",
        "MaxiCode"
    )

    fun getStringType(code: Int): String {
        return when (code) {
            4096 -> "Aztec"
            8 -> "Codabar"
            1 -> "Code 128"
            2 -> "Code 39"
            4 -> "Code 93"
            16 -> "Data Matrix"
            32 -> "EAN-13"
            64 -> "EAN-8"
            2048 -> "PDF 417"
            256 -> "QR Code"
            512 -> "UPC-A"
            1024 -> "UPC-E"
            128 -> "ITF"
            else -> ""
        }
    }
}